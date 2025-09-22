package com.diamssword.greenresurgence.containers.grids;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public interface IGridContainer {
	String getName();

	default Slot createSlotFor(int index, int x, int y) {
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
