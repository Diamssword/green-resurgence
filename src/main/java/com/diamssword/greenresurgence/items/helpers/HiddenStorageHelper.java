package com.diamssword.greenresurgence.items.helpers;

import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;

public class HiddenStorageHelper extends ItemStorageHelper {


	public HiddenStorageHelper(int maxSlot) {
		super(maxSlot);
	}

	@Override
	public TooltipData getTooltipData(ItemStack stack) {
		return null;
	}

	@Override
	public ItemStack removeFirstStack(ItemStack stack) {
		var inv = this.getAsInventory(stack);
		var res = ItemStack.EMPTY;
		for(int i = 0; i < inv.size(); i++) {
			var st = inv.getStack(i);
			if(!st.isEmpty()) {
				res = inv.removeStack(i);
				inv.markDirty();
				break;
			}
		}

		return res;
	}

}
