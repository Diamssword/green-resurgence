package com.diamssword.greenresurgence.systems.equipement;

import net.minecraft.item.ItemStack;

@FunctionalInterface
public interface IEquipementItem {
	IUpgradableEquipment getEquipment(ItemStack stack);
}
