package com.diamssword.greenresurgence.containers.grids;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public interface IGridContainer {
	String getName();

	default Slot createSlotFor(int index, int x, int y) {
		if(this.getInventory() instanceof SimpleInventory se)
			return new Slot(this.getInventory(), index, x, y) {
				@Override
				public void setStack(ItemStack stack) {
					super.setStack(stack);
				}

				@Override
				public boolean canInsert(ItemStack stack) {
					return se.canInsert(stack);
				}
			};
		return new Slot(this.getInventory(), index, x, y);
	}

	default boolean revert() {
		return false;
	}

	int getWidth();

	int getHeight();

	int getStartIndex();

	default int getSize() {
		return getWidth() * getHeight();
	}

	Inventory getInventory();

	boolean isPlayerContainer();

	int getQuickSlotPriority(ItemStack item);
}
