package com.diamssword.greenresurgence.items.helpers;

import net.minecraft.item.ItemStack;

public interface IRadiationMitigator {
	public float getRadiationMultiplicator(ItemStack stack, double amount);

	public default double getRadiationAfterMitigation(ItemStack stack, double amount) {
		return amount * getRadiationMultiplicator(stack, amount);
	}
}
