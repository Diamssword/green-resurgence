package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.items.weapons.ICustomPoseWeapon;
import com.diamssword.greenresurgence.systems.character.PosesManager;
import com.diamssword.greenresurgence.systems.equipement.EffectLevel;
import com.diamssword.greenresurgence.systems.equipement.EquipmentSkins;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EquipmentSecondHand extends EquipmentTool implements ICustomPoseWeapon {


	public EquipmentSecondHand(String category, String subCategory) {
		super(category, subCategory);
	}

	@Override
	public Map<String, EffectLevel> getBaseUpgrades() {
		return new HashMap<>();
	}

	@Override
	public boolean shouldRemoveOffHand() {
		return false;
	}

	@Override
	public String customPoseId(ItemStack stack) {
		//TODO activate depending on stack upgrades
		return PosesManager.KNUCLESHANDWIELD;
	}

	@Override
	public int customPoseMode(ItemStack stack) {
		return EquipmentSkins.get(this.getEquipment(stack).getSkin(), this).map(v -> v.extra).orElse(0);
	}
}
