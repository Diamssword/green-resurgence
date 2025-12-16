package com.diamssword.greenresurgence.systems.equipement;

import com.diamssword.greenresurgence.systems.equipement.effects.CriticalHitEffectUpgrade;
import com.diamssword.greenresurgence.systems.equipement.effects.SweepingEffectUpgrade;
import com.diamssword.greenresurgence.systems.equipement.utils.MapEffectMaker;

import java.util.Map;
import java.util.UUID;

public class EquipmentValues {
	public static final UUID ATTACK_DAMAGE_MODIFIER_ID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	public static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	public static final UUID ATTACK_RANGE_MODIFIER_ID = UUID.fromString("ae5630ab-af0f-4c38-8f5e-b7d369078812");
	public static final UUID REACH_RANGE_MODIFIER_ID = UUID.fromString("e3f95e9d-9eeb-48ba-ba90-a7c551b1608c");
	//BASE_ATTACK_SPEED is 4;
	public static final float SPEED_SLOWER = -3.5f;
	public static final float SPEED_SLOW = -3f;
	public static final float SPEED_NORMAL = -2.4f;
	public static final float SPEED_FAST = -2f;
	public static final float SPEED_FASTER = -1f;
	public static final float SPEED_FASTEST = 1f;
	public static final Map<String, EffectLevel> SMALL_BLADE_BASE = MapEffectMaker.create(EquipmentEffects.ATTACK_SPEED, SPEED_FAST).add(EquipmentEffects.ATTACK_DAMAGE, 2.4f).add(EquipmentEffects.ATTACK_RANGE, -1).add(EquipmentEffects.CRITICAL_HIT, 50, CriticalHitEffectUpgrade.DAMAGE_BONUS, 30).get();
	public static final Map<String, EffectLevel> MEDIUM_BLADE_BASE = MapEffectMaker.create(EquipmentEffects.ATTACK_SPEED, SPEED_NORMAL).add(EquipmentEffects.ATTACK_DAMAGE, 2.4f).add(EquipmentEffects.CRITICAL_HIT, 40, CriticalHitEffectUpgrade.DAMAGE_BONUS, 50).get();
	public static final Map<String, EffectLevel> LONG_BLADE_BASE = MapEffectMaker.create(EquipmentEffects.ATTACK_SPEED, SPEED_SLOW).add(EquipmentEffects.ATTACK_DAMAGE, 4f).add(EquipmentEffects.ATTACK_RANGE, 0.5f).add(EquipmentEffects.CRITICAL_HIT, 20, CriticalHitEffectUpgrade.DAMAGE_BONUS, 20).add(EquipmentEffects.SWEEPING, 10).get();

	public static final Map<String, EffectLevel> SMALL_HAMMER_BASE = MapEffectMaker.create(EquipmentEffects.ATTACK_SPEED, SPEED_NORMAL).add(EquipmentEffects.ATTACK_DAMAGE, 3.5f).add(EquipmentEffects.ATTACK_RANGE, -1).add(EquipmentEffects.OPENING, 2).get();
	public static final Map<String, EffectLevel> MEDIUM_HAMMER_BASE = MapEffectMaker.create(EquipmentEffects.ATTACK_SPEED, SPEED_SLOW).add(EquipmentEffects.ATTACK_DAMAGE, 2.4f).add(EquipmentEffects.SWEEPING, 3, SweepingEffectUpgrade.DAMAGE_BONUS, 80f).get();
	public static final Map<String, EffectLevel> LONG_HAMMER_BASE = MapEffectMaker.create(EquipmentEffects.ATTACK_SPEED, SPEED_SLOWER).add(EquipmentEffects.ATTACK_DAMAGE, 4f).add(EquipmentEffects.ATTACK_RANGE, 0.5f).add(EquipmentEffects.SWEEPING, 50, SweepingEffectUpgrade.DAMAGE_BONUS, 50f, SweepingEffectUpgrade.RADIUS_BONUS, 2.5f).get();

	public static final Map<String, EffectLevel> SMALL_SPIKE_BASE = MapEffectMaker.create(EquipmentEffects.ATTACK_SPEED, SPEED_FAST).add(EquipmentEffects.ATTACK_DAMAGE, 2.4f).add(EquipmentEffects.ATTACK_RANGE, -1).get();
	public static final Map<String, EffectLevel> MEDIUM_SPIKE_BASE = MapEffectMaker.create(EquipmentEffects.ATTACK_SPEED, SPEED_NORMAL).add(EquipmentEffects.ATTACK_DAMAGE, 2.4f).add(EquipmentEffects.ATTACK_RANGE, 1f).get();
	public static final Map<String, EffectLevel> LONG_SPIKE_BASE = MapEffectMaker.create(EquipmentEffects.ATTACK_SPEED, SPEED_NORMAL).add(EquipmentEffects.ATTACK_DAMAGE, 4f).add(EquipmentEffects.ATTACK_RANGE, 2f).get();
}
