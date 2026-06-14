package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.containers.EquipmentScreenHandler;
import com.diamssword.greenresurgence.containers.SlotedSimpleInventory;
import com.diamssword.greenresurgence.systems.equipement.IEquipementItem;
import com.diamssword.greenresurgence.systems.equipement.IEquipmentBlueprint;
import com.diamssword.greenresurgence.systems.equipement.IEquipmentDef;
import com.diamssword.greenresurgence.systems.equipement.StackBasedEquipment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class MultiEquipmentTinkererBlockEntity extends BlockEntity {

	protected SlotedSimpleInventory inventory = new SlotedSimpleInventory(equipmentsSlots());
	protected SimpleInventory[] upgrades = new SimpleInventory[equipmentsSlots()];
	protected Item[] currentTools = new Item[equipmentsSlots()];
	protected final Set<ServerPlayerEntity> trackedPlayers = new HashSet<>();
	private final StackBasedEquipment[] currentEquipments = new StackBasedEquipment[equipmentsSlots()];
	private InventoryChangedListener[] currentListener = new InventoryChangedListener[equipmentsSlots()];

	public int equipmentsSlots() {
		return 4;
	}

	public MultiEquipmentTinkererBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inventory.addListener(this::onToolChange);
	}

	protected void onToolChange(Inventory unused) {
		if(world != null && !world.isClient) {
			boolean flag = false;
			for(int i = 0; i < inventory.size(); i++) {
				var stack = inventory.getStack(i);
				IEquipmentDef oldEquipment = null;
				if(currentEquipments[i] != null)
					oldEquipment = currentEquipments[i].getEquipment();
				if(stack.getItem() instanceof IEquipmentBlueprint bp) {
					currentEquipments[i] = new StackBasedEquipment(bp.getEquipment(), stack);
				} else if(stack.getItem() instanceof IEquipementItem bp) {
					currentEquipments[i] = new StackBasedEquipment(bp.getEquipment(stack).getEquipment(), stack);
				} else
					currentEquipments[i] = null;
				if(stack.getItem() != currentTools[i] && (currentEquipments[i] == null || oldEquipment != currentEquipments[i].getEquipment())) {
					flag = true;
				} else
					updateContent(i);
			}
			if(flag)
				recreateUpgradeInv();
		}

		markUpdate();
	}

	protected void updateContent(int index) {
		if(upgrades[index] != null) {
			upgrades[index].removeListener(currentListener[index]);

			currentTools[index] = inventory.getStack(index).getItem();
			if(currentEquipments[index] != null) {
				var slots = currentEquipments[index].getEquipment().getSlots();
				for(int i = 0; i < slots.length; i++) {
					upgrades[index].setStack(i, currentEquipments[index].getUpgradeItem(slots[i]));
				}
			}
			currentListener[index] = this::upgradeListener;
			upgrades[index].addListener(currentListener[index]);
		}

	}

	protected void upgradeListener(Inventory inv) {
		int slot = 0;
		int i = 0;
		for(int j = 0; j < upgrades.length; j++) {
			if(upgrades[j] == inv) {
				i = j;
				break;
			}
		}

		if(currentEquipments[i] != null) {
			var slots = currentEquipments[i].getEquipment().getSlots();
			for(String s : slots) {
				currentEquipments[i].setUpgrade(inv.getStack(slot), s);
				slot++;
			}
			currentEquipments[i].save();
			if(currentEquipments[i].isMinimalUpgradesSet() && currentTools[i] instanceof IEquipmentBlueprint bp) {
				var nbts = currentEquipments[i].stack.getNbt();
				var st = bp.getEquipment().getEquipmentItem().getDefaultStack();
				st.setNbt(nbts);
				st.addHideFlag(ItemStack.TooltipSection.MODIFIERS);
				inventory.setStack(i, st);
			} else if(!currentEquipments[i].isMinimalUpgradesSet() && currentTools[i] instanceof IEquipementItem bp) {
				var nbts = currentEquipments[i].stack.getNbt();
				var st = new ItemStack(currentEquipments[i].getEquipment().getBlueprintItem(), 1);
				st.setNbt(nbts);
				inventory.setStack(i, st);
			}
		}

	}

	protected void recreateUpgradeInv() {
		for(int index = 0; index < currentTools.length; index++) {
			currentTools[index] = inventory.getStack(index).getItem();
			IEquipmentDef edef = null;
			if(currentTools[index] instanceof IEquipementItem eqi) {
				edef = eqi.getEquipment(inventory.getStack(0)).getEquipment();
			} else if(currentTools[index] instanceof IEquipmentBlueprint bp) {
				edef = bp.getEquipment();
			}

			if(edef != null) {
				upgrades[index] = new SimpleInventory(edef.getSlots().length);
			} else
				upgrades[index] = null;
			updateContent(index);
		}
		world.getServer().execute(() -> new HashSet<>(trackedPlayers).forEach((p) -> {
			p.closeHandledScreen();
			openInventory(p);
		}));

	}

	public void openInventory(ServerPlayerEntity player) {
		if(currentTools[0] == null) {
			onToolChange(null);
		}
		trackedPlayers.add(player);
		Containers.createHandler(player, pos, (sync, inv, p1) -> {
			var handler = new EquipmentScreenHandler(sync, player, new EquipmentTinkererBlockEntity.EquipmentGrid(inventory), upgrades);
			handler.onClosed = () -> trackedPlayers.remove(player);
			return handler;
		});

	}

	public SimpleInventory getInventory() {
		return inventory;
	}

	protected void markUpdate() {
		this.markDirty();
		if(this.world instanceof ServerWorld sw) {sw.getChunkManager().markForUpdate(pos);}
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		nbt.put("inventory", inventory.toNbtList());
		super.writeNbt(nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		if(nbt.contains("inventory")) {
			inventory.readNbtList(nbt.getList("inventory", NbtElement.COMPOUND_TYPE));
		}
	}

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return createNbt();
	}


}
