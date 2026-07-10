package com.diamssword.greenresurgence.systems.equipement.utils;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface IOnUpgradeBreak {
	public void onUpgradeBreak(LivingEntity player, ItemStack toolStack, ItemStack upgradeStack);
}
