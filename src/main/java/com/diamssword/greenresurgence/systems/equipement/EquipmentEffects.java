package com.diamssword.greenresurgence.systems.equipement;

import com.diamssword.greenresurgence.systems.equipement.effects.DamageEffectUpgrade;
import com.diamssword.greenresurgence.systems.equipement.effects.HammerEffect;
import com.diamssword.greenresurgence.systems.equipement.effects.SpeedEffectUpgrade;
import com.diamssword.greenresurgence.systems.equipement.effects.SweepingEffectUpgrade;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EquipmentEffects {
	private static final Map<String, IEquipmentEffect> effects = new HashMap<>();

	public static void init() {
		register("sweeping", new SweepingEffectUpgrade());
		register("speed", new SpeedEffectUpgrade());
		register("damage", new DamageEffectUpgrade());
		register("hammer", new HammerEffect());
	}

	private static void register(String id, IEquipmentEffect effect) {
		effects.put(id, effect);
	}

	public static Optional<IEquipmentEffect> get(String name) {
		return Optional.ofNullable(effects.get(name));
	}
}
