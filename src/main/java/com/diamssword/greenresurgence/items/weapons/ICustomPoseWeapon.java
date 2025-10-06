package com.diamssword.greenresurgence.items.weapons;

import net.minecraft.item.ItemStack;

public interface ICustomPoseWeapon {

	boolean shouldRemoveOffHand();

	public String customPoseId(ItemStack stack);

	int customPoseMode(ItemStack stack);
}
