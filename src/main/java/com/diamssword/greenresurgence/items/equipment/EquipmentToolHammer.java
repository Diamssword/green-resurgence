package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.systems.equipement.IEquipmentUpgrade;
import com.diamssword.greenresurgence.systems.equipement.upgrades.SweepingEffectUpgrade;

public class EquipmentToolHammer extends EquipmentTool {
	public EquipmentToolHammer(String category, String subCategory) {
		super(category, subCategory);
	}


	@Override
	public IEquipmentUpgrade[] getBaseUpgrades() {
		return new IEquipmentUpgrade[]{new SweepingEffectUpgrade()};
	}
}
