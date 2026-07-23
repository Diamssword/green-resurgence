package com.diamssword.greenresurgence.items.helpers;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public interface ISecondaryDurabilityBar {

	default boolean isSecondItemBarVisible(ItemStack stack) {
		return true;
	}

	default int getSecondItemBarStep(ItemStack stack) {
		return Math.round(getSecondDurabilityProgress(stack) * 13f);
	}

	public float getSecondDurabilityProgress(ItemStack stack);

	default int getSecondItemBarColor(ItemStack stack) {
		return MathHelper.hsvToRgb(getSecondDurabilityProgress(stack) / 3.0F, 1.0F, 1.0F);
	}
}
