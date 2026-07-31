package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.systems.equipement.EffectLevel;

import java.util.Map;

public class EquipmentSecondHand extends EquipmentTool implements IOffHandAttack {


	public EquipmentSecondHand(String category, String subCategory) {
		super(category, subCategory);
	}

	public EquipmentSecondHand(String category, String subCategory, Map<String, EffectLevel> bases) {
		super(category, subCategory, bases);
	}

}
