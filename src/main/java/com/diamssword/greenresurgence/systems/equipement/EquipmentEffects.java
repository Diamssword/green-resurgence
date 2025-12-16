package com.diamssword.greenresurgence.systems.equipement;

import com.diamssword.greenresurgence.systems.equipement.effects.*;
import com.diamssword.greenresurgence.systems.lootables.Lootables;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EquipmentEffects {

	private static final Map<String, IEquipmentEffect> effects = new HashMap<>();
	public static final String ATTACK_SPEED = "attack_speed";
	public static final String ATTACK_RANGE = "attack_range";
	public static final String ATTACK_DAMAGE = "attack_damage";
	public static final String CRITICAL_HIT = "critical_hit";
	public static final String TOOL_HAMMER = "tool_hammer";
	public static final String TOOL_WRENCH = "tool_wrench";
	public static final String SWEEPING = "sweeping";
	public static final String BASE_DAMAGE_MOD = "base_dmg_mod";
	public static final String OPENING = "opening";

	public static void init() {
		register(SWEEPING, new SweepingEffectUpgrade());
		register(ATTACK_SPEED, new AttackSpeedEffectUpgrade());
		register(ATTACK_DAMAGE, new DamageEffectUpgrade());
		register(ATTACK_RANGE, new AttackRangeEffectUpgrade());
		register(CRITICAL_HIT, new CriticalHitEffectUpgrade());
		register(BASE_DAMAGE_MOD, new DamageModifierEffectUpgrade());
		register(OPENING, new OpeningEffectUpgrade());
		register(TOOL_HAMMER, new LootingToolEffect(Lootables.HAMMER, "hammer"));
		register(TOOL_WRENCH, new LootingToolEffect(Lootables.WRENCH, "wrench"));
	}

	private static void register(String id, IEquipmentEffect effect) {
		effects.put(id, effect);
	}

	public static Optional<IEquipmentEffect> get(String name) {
		return Optional.ofNullable(effects.get(name));
	}
}
