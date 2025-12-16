package com.diamssword.greenresurgence.systems.equipement;

import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public interface IEquipmentUpgrade {
	boolean canBeApplied(IEquipmentDef equipment, ItemStack stack);

	public Map<String, EffectLevel> getEffectsLevels();

	String[] slots(IEquipmentDef equipment);

	public default Map<String, IEquipmentEffect> getEffects() {
		var res = new HashMap<String, IEquipmentEffect>();
		for(String s : getEffectsLevels().keySet()) {
			EquipmentEffects.get(s).ifPresent(c -> res.put(s, c));
		}
		return res;
	}

	/**
	 * The chance for this upgrade to take damage instead of others
	 */
	float damageWeight();


	enum InteractType {
		POST_ATTACK,
		/**
		 * Append before damage is applied, you can set the Return value to modify the damage output and the context value to know the base damage
		 */
		PRE_ATTACK,
		ATTACKED,
		BREAK,
		INTERACT,
		TICK
	}
}
