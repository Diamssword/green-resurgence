package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.items.weapons.ICustomPoseWeapon;
import com.diamssword.greenresurgence.systems.character.PosesManager;
import net.minecraft.item.ItemStack;

public class EquipmentToolElectricTwoHanded extends EquipmentToolElectric implements ICustomPoseWeapon {
	public EquipmentToolElectricTwoHanded(String category, String subCategory, boolean emissive) {
		super(category, subCategory, emissive);
	}

	@Override
	public boolean shouldRemoveOffHand() {
		return true;
	}

	@Override
	public String customPoseId(ItemStack stack) {
		return PosesManager.TWOHANDWIELD;
	}

	@Override
	public int customPoseMode(ItemStack stack) {
		return 2;
	}
}
