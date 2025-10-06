package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.items.weapons.ICustomPoseWeapon;
import com.diamssword.greenresurgence.systems.character.PosesManager;
import com.diamssword.greenresurgence.systems.equipement.EquipmentSkins;
import com.diamssword.greenresurgence.systems.equipement.IEquipmentUpgrade;
import net.minecraft.item.ItemStack;

public class EquipmentTwoHanded extends EquipmentTool implements ICustomPoseWeapon {


	public EquipmentTwoHanded(String category, String subCategory) {
		super(category, subCategory);
	}

	@Override
	public IEquipmentUpgrade[] getBaseUpgrades() {
		return new IEquipmentUpgrade[0];
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
		return EquipmentSkins.get(this.getEquipment(stack).getSkin(), this).map(v -> v.extra).orElse(0);
	}
}
