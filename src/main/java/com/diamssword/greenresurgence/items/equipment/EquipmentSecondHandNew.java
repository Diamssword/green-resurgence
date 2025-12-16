package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.systems.equipement.EffectLevel;

import java.util.Map;

public class EquipmentSecondHandNew extends EquipmentTool implements IOffHandAttack {


	public EquipmentSecondHandNew(String category, String subCategory) {
		super(category, subCategory);
	}

	public EquipmentSecondHandNew(String category, String subCategory, Map<String, EffectLevel> bases) {
		super(category, subCategory, bases);
	}

}
