package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.containers.EquipmentScreenHandler;
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

public class EquipmentTinkererBlockEntity extends BlockEntity {

	protected SimpleInventory inventory = new SimpleInventory(1);
	protected SimpleInventory upgrades;
	protected Item currentTool = null;
	private final Set<ServerPlayerEntity> trackedPlayers = new HashSet<>();
	private StackBasedEquipment currentEquipment;
	private InventoryChangedListener currentListener;

	public EquipmentTinkererBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inventory.addListener(this::onToolChange);
	}

	protected void onToolChange(Inventory unused) {
		if(world != null && !world.isClient) {
			var stack = inventory.getStack(0);
			if(stack.getItem() instanceof IEquipmentBlueprint bp) {
				currentEquipment = new StackBasedEquipment(bp.getEquipment(), stack);
			} else if(stack.getItem() instanceof IEquipementItem bp) {
				currentEquipment = new StackBasedEquipment(bp.getEquipment(stack).getEquipment(), stack);
			} else
				currentEquipment = null;
			if(stack.getItem() != currentTool) {
				recreateUpgradeInv();
			} else {
				updateContent();
			}
		}
		markUpdate();
	}

	protected void updateContent() {
		if(upgrades != null && currentEquipment != null) {

			upgrades.removeListener(currentListener);
			var slots = currentEquipment.getEquipment().getSlots();
			for(int i = 0; i < slots.length; i++) {
				upgrades.setStack(i, currentEquipment.getUpgradeItem(slots[i]));
			}
			currentListener = this::upgradeListener;
			upgrades.addListener(currentListener);
		}
	}

	protected void upgradeListener(Inventory inv) {
		if(currentEquipment != null) {
			var slots = currentEquipment.getEquipment().getSlots();
			for(int i = 0; i < slots.length; i++) {
				currentEquipment.setUpgrade(inv.getStack(i), slots[i]);
			}
			currentEquipment.save();
		}
	}

	protected void recreateUpgradeInv() {
		currentTool = inventory.getStack(0).getItem();
		IEquipmentDef edef = null;
		if(currentTool instanceof IEquipementItem eqi) {
			edef = eqi.getEquipment(inventory.getStack(0)).getEquipment();
		} else if(currentTool instanceof IEquipmentBlueprint bp) {
			edef = bp.getEquipment();
		}
		if(edef != null) {
			upgrades = new SimpleInventory(edef.getSlots().length);
		} else
			upgrades = null;
		updateContent();
		world.getServer().execute(() -> new HashSet<>(trackedPlayers).forEach((p) -> {
			p.closeHandledScreen();
			openInventory(p);
		}));

	}

	public void openInventory(ServerPlayerEntity player) {
		if(currentTool == null) {
			onToolChange(null);
		}
		trackedPlayers.add(player);
		Containers.createHandler(player, pos, (sync, inv, p1) -> {
			var handler = new EquipmentScreenHandler(sync, player, inventory, upgrades);
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
