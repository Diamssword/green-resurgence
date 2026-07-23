package com.diamssword.greenresurgence.items.equipment.upgrades;

import com.diamssword.greenresurgence.systems.equipement.IEquipmentDef;
import io.wispforest.owo.itemgroup.OwoItemSettings;

public class EquipmentExtraUpgradeItem extends EquipmentUpgradeItem {

	public EquipmentExtraUpgradeItem(String allowed, String slot, float wheight) {
		super(allowed, slot, wheight);
	}

	public EquipmentExtraUpgradeItem(String allowed, String slot, int durability, float wheight) {
		super(allowed, slot, durability, wheight);
	}

	public EquipmentExtraUpgradeItem(String allowed, String slot) {
		super(allowed, slot);
	}

	public EquipmentExtraUpgradeItem(OwoItemSettings settings, String allowed, String slot, int durability, float weight) {
		super(settings, allowed, slot, durability, weight);
	}

	@Override
	public String[] slots(IEquipmentDef equipment) {
		return new String[]{"extra_" + slot, "extra2_" + slot};
	}
}
