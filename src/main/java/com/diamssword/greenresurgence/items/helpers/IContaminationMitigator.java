package com.diamssword.greenresurgence.items.helpers;

import net.minecraft.item.ItemStack;

public interface IContaminationMitigator {
	public float getContaminationMultiplicator(ItemStack stack, double amount);

	public default double getContaminationAfterMitigation(ItemStack stack, double amount) {
		return amount * getContaminationMultiplicator(stack, amount);
	}
}
