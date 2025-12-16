package com.diamssword.greenresurgence.containers.grids;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class ExtractOnlyGrid extends GridContainer {
	public ExtractOnlyGrid(String name, int width, int height) {
		super(name, width, height);
	}

	public ExtractOnlyGrid(String name, Inventory inv, int width, int height) {
		super(name, inv, width, height);
	}

	public ExtractOnlyGrid(String name, Inventory inv, int width, int height, int index) {
		super(name, inv, width, height, index);
	}

	@Override
	public Slot createSlotFor(int index, int x, int y) {

		return new Slot(this.getInventory(), index, x, y) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return false;
			}
		};
	}
}
