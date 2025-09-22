package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.blocks.ArmorTinkererBlock;
import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import com.diamssword.greenresurgence.containers.grids.ContainerArmorGrid;
import com.diamssword.greenresurgence.containers.grids.IGridContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ArmorTinkererBlockEntity extends BlockEntity {

	public SimpleInventory inventory = new SimpleInventory(4);

	public ArmorTinkererBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public void openInventory(ServerPlayerEntity player) {
		if(inventory == null) {createInventory();}
		Containers.createHandler(player, pos, (sync, inv, p1) -> new Container(sync, player, new ContainerArmorGrid("armor_tinkerer", inventory, 1, 4)));

	}

	public SimpleInventory getInventory() {
		return inventory;
	}

	public ItemStack getArmorStack(EquipmentSlot slot) {
		if(inventory != null) {
			return inventory.getStack(revertedArmorIndex(slot));
		}
		return ItemStack.EMPTY;
	}

	public static int revertedArmorIndex(EquipmentSlot slot) {
		switch(slot) {
			case FEET -> {
				return 3;
			}
			case LEGS -> {
				return 2;
			}
			case CHEST -> {
				return 1;
			}
			default -> {
				return 0;
			}
		}
	}

	private void createInventory() {
		inventory = new SimpleInventory(4);
		inventory.addListener(ls -> this.markUpdate());
		this.markDirty();
	}

	protected void markUpdate() {
		this.markDirty();
		if(this.world instanceof ServerWorld sw) {sw.getChunkManager().markForUpdate(pos);}
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		if(!this.getCachedState().get(ArmorTinkererBlock.BOTTOM)) {
			super.writeNbt(nbt);
			return;
		}
		if(inventory != null) {

			nbt.put("inventory", Inventories.writeNbt(new NbtCompound(), inventory.stacks));
		}
		super.writeNbt(nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		if(!this.getCachedState().get(ArmorTinkererBlock.BOTTOM)) {return;}
		if(nbt.contains("inventory")) {
			inventory = new SimpleInventory(4);
			Inventories.readNbt(nbt.getCompound("inventory"), inventory.stacks);
			inventory.addListener(ls -> markUpdate());
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

	public static class Container extends MultiInvScreenHandler {
		public Container(int syncId, PlayerInventory playerInventory) {
			super(syncId, playerInventory);
		}

		public Container(int syncId, PlayerEntity player, IGridContainer... inventories) {
			super(syncId, player, inventories);
		}

		@Override
		public ScreenHandlerType<Container> type() {
			return Containers.ARMOR_TINKERER;
		}
	}

}
