package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.items.weapons.ICustomPoseWeapon;
import com.diamssword.greenresurgence.systems.character.PosesManager;
import com.diamssword.greenresurgence.systems.equipement.EffectLevel;
import com.diamssword.greenresurgence.systems.equipement.EquipmentSkins;
import net.minecraft.item.ItemStack;

import java.util.Map;

public class EquipmentTwoHanded extends EquipmentTool implements ICustomPoseWeapon {


	public EquipmentTwoHanded(String category, String subCategory) {
		super(category, subCategory);
	}

	public EquipmentTwoHanded(String category, String subCategory, Map<String, EffectLevel> baseEffects) {
		super(category, subCategory, baseEffects);
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
