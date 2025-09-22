package com.diamssword.greenresurgence.blockEntities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class GenericStorageBlockEntity extends BlockEntity implements Inventory {
	private int size = 9;
	private SimpleInventory inventory;

	public GenericStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.inventory = new SimpleInventory(size);
		this.inventory.addListener(l -> {
			markDirty();
		});
	}

	public GenericStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int size) {
		super(type, pos, state);
		this.size = size;
		this.inventory = new SimpleInventory(size);
		this.inventory.addListener(l -> {
			markDirty();
		});
	}

	public void addListener(InventoryChangedListener l) {
		this.inventory.addListener(l);
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		// Save the current value of the number to the nbt
		nbt.putInt("size", size);
		Inventories.writeNbt(nbt, inventory.stacks);
		super.writeNbt(nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);

		if(nbt.contains("size")) {
			this.size = nbt.getInt("size");
			inventory = new SimpleInventory(size);
			this.inventory.addListener(l -> {
				markDirty();
			});
		}
		Inventories.readNbt(nbt, inventory.stacks);
	}

	@Override
	public int size() {
		return inventory.size();
	}

	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	@Override
	public ItemStack getStack(int slot) {
		return inventory.getStack(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return inventory.removeStack(slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		return inventory.removeStack(slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		inventory.setStack(slot, stack);
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return inventory.canPlayerUse(player);
	}

	@Override
	public void clear() {
		inventory.clear();
	}
}
