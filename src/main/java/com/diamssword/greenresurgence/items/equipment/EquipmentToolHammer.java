package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.systems.equipement.EffectLevel;

import java.util.HashMap;
import java.util.Map;

public class EquipmentToolHammer extends EquipmentTool {
	public EquipmentToolHammer(String category, String subCategory) {
		super(category, subCategory);
	}


	@Override
	public Map<String, EffectLevel> getBaseUpgrades() {
		var m = new HashMap<String, EffectLevel>();
		m.put("sweeping", new EffectLevel(1));
		return m;
	}
}
