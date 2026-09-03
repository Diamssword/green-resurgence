package com.diamssword.greenresurgence.systems.crafting;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record CraftExtraControl(boolean shift, boolean ctrl) {

	public int getOperationCount(UniversalResource result) {
		if(result.getType().isItem) {
			var it = result.asItem();
			var max = it.getMaxCount();
			if(shift && !ctrl)
				return (int) Math.floor(max / (float) it.getCount());
			else if(shift && ctrl)
				return (int) Math.floor(100 / (float) it.getCount());
			else if(ctrl) {
				return (int) Math.ceil(10 / (float) it.getCount());
			} else
				return 1;
		}
		return 1;
	}

	public static List<ItemStack> applyMultiplierToItemStack(ItemStack stack, int multiplier) {
		var tot = stack.getCount() * multiplier;
		if(tot <= stack.getMaxCount())
			return List.of(stack.copyWithCount(tot));
		else {
			var ls = new ArrayList<ItemStack>();
			while(tot >= stack.getMaxCount()) {
				tot -= stack.getMaxCount();
				ls.add(stack.copyWithCount(stack.getMaxCount()));
			}
			if(tot > 0)
				ls.add(stack.copyWithCount(tot));
			return ls;
		}
	}
}