package com.diamssword.greenresurgence.systems.equipement;

import com.diamssword.greenresurgence.effects.ResurgenceEffects;
import com.diamssword.greenresurgence.systems.attributs.Attributes;
import com.diamssword.greenresurgence.systems.equipement.effects.*;
import com.diamssword.greenresurgence.systems.lootables.Lootables;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EquipmentEffects {

	private static final Map<String, IEquipmentEffect> effects = new HashMap<>();
	public static final String ATTACK_SPEED = "attack_speed";
	public static final String ATTACK_RANGE = "attack_range";
	public static final String ATTACK_DAMAGE = "attack_damage";
	public static final String MOVE_SPEED = "move_speed";
	public static final String CONTAMINATION_REDUCTION = "contamination_reduction";
	public static final String CRITICAL_HIT = "critical_hit";
	public static final String TOOL_HAMMER = "tool_hammer";
	public static final String TOOL_WRENCH = "tool_wrench";
	public static final String SWEEPING = "sweeping";
	public static final String BASE_DAMAGE_MOD = "base_dmg_mod";
	public static final String OPENING = "effect_opening";
	public static final String WITHER = "effect_wither";
	public static final String POISON = "effect_poison";
	public static final String FLAME = "effect_flame";
	public static final String BLEEDING = "effect_bleeding";
	public static final String ABSORPTION = "effect_absorption";
	public static final String POWERED_DAMAGE = "powered_damage";
	public static final String POWERED_FLAME = "powered_flame";
	public static final String THREAT_MODIFIER = "threat_modifier";
	public static final String ARMOR = "armor";
	public static final String WEAPON_ARMOR_TOUGHNESS = "weapon_armor_toughness";
	public static final String THROWABLE = "throwable";
	public static final String COMMANDER_BANNER = "commander_banner";
	public static final String SHIELD_BOOST = "shield_boost";
	public static final String KNOCKBACK_RESISTANCE = "knockback_resistance";
	public static final String KNOCKBACK = "knockback";
	public static final String FALL_RESISTANCE = "fall_resistance";
	public static final String ELECTRIC_EFFICIENCY = "electric_efficiency";
	public static final String ELECTRIC_WARMUP_SPEED = "electric_warmup_speed";

	public static void init() {
		register(SWEEPING, new SweepingEffectUpgrade());
		register(ATTACK_SPEED, SimpleAttributeEffect.addition(ATTACK_SPEED, EntityAttributes.GENERIC_ATTACK_SPEED, EquipmentValues.ATTACK_SPEED_MODIFIER_ID));
		register(ATTACK_DAMAGE, SimpleAttributeEffect.addition(ATTACK_DAMAGE, EntityAttributes.GENERIC_ATTACK_DAMAGE, EquipmentValues.ATTACK_DAMAGE_MODIFIER_ID));
		register(POWERED_DAMAGE, new PoweredUpgradeWrapper(SimpleAttributeEffect.addition(POWERED_DAMAGE, EntityAttributes.GENERIC_ATTACK_DAMAGE, EquipmentValues.POWERED_ATTACK_DAMAGE_MODIFIER_ID)));
		register(ATTACK_RANGE, new AttackRangeEffectUpgrade());
		register(CRITICAL_HIT, new CriticalHitEffectUpgrade());
		register(BASE_DAMAGE_MOD, new DamageModifierEffectUpgrade());
		register(OPENING, new PotionApplierEffectUpgrade(OPENING, ResurgenceEffects.HAMMER_OPENING));
		register(POISON, new PotionApplierEffectUpgrade(POISON, StatusEffects.POISON));
		register(WITHER, new PotionApplierEffectUpgrade(WITHER, StatusEffects.WITHER));
		register(BLEEDING, new PotionApplierEffectUpgrade(BLEEDING, ResurgenceEffects.BLEEDING));
		register(TOOL_HAMMER, new LootingToolEffect(Lootables.HAMMER, "hammer"));
		register(TOOL_WRENCH, new LootingToolEffect(Lootables.WRENCH, "wrench"));
		register(MOVE_SPEED, SimpleAttributeEffect.multiplyBase(MOVE_SPEED, EntityAttributes.GENERIC_MOVEMENT_SPEED, EquipmentValues.MOVE_SPEED_MODIFIER_ID).workOnAllSlot());
		register(WEAPON_ARMOR_TOUGHNESS, SimpleAttributeEffect.addition(WEAPON_ARMOR_TOUGHNESS, EntityAttributes.GENERIC_ARMOR_TOUGHNESS, EquipmentValues.ARMOR_TOUGHNESS_MODIFIER_ID));
		register(CONTAMINATION_REDUCTION, SimpleAttributeEffect.addition(CONTAMINATION_REDUCTION, Attributes.CONTAMINATION_REDUCTION, EquipmentValues.CONTAMINATION_REDUCTION_MODIFIER_ID));
		register(THROWABLE, new ThrowableEffectUpgrade());
		register(COMMANDER_BANNER, new RadiusEffect(COMMANDER_BANNER, ResurgenceEffects.PERCENT_STRENGTH, 10f));
		register(SHIELD_BOOST, SimpleAttributeEffect.addition(SHIELD_BOOST, Attributes.MAX_SHIELD, EquipmentValues.MAX_SHIELD_MODIFIER_ID));
		register(KNOCKBACK_RESISTANCE, SimpleAttributeEffect.addition(KNOCKBACK_RESISTANCE, EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, EquipmentValues.KNOCKBACK_RESISTANCE_MODIFIER_ID).workOnAllSlot());
		register(FALL_RESISTANCE, SimpleAttributeEffect.addition(FALL_RESISTANCE, Attributes.FALL_DAMAGE_REDUCTION, EquipmentValues.FALL_RESISTANCE_MODIFIER_ID).workOnAllSlot());
		register(KNOCKBACK, SimpleAttributeEffect.addition(KNOCKBACK, Attributes.PLAYER_KNOCKBACK, EquipmentValues.KNOCKBACK_MODIFIER_ID));
		register(THREAT_MODIFIER, SimpleAttributeEffect.addition(THREAT_MODIFIER, Attributes.THREAT_MULTIPLIER, EquipmentValues.THREAT_MODIFIER_ID));
		register(ABSORPTION, SimpleAttributeEffect.addition(THREAT_MODIFIER, Attributes.THREAT_MULTIPLIER, EquipmentValues.THREAT_MODIFIER_ID));
		register(FLAME, new FireEffectUpgrade(FLAME));
		register(POWERED_FLAME, new PoweredUpgradeWrapper(new FireEffectUpgrade(POWERED_FLAME)));
		register(ELECTRIC_EFFICIENCY, new SimpleDisplayEffect(ELECTRIC_EFFICIENCY, true));
		register(ELECTRIC_WARMUP_SPEED, new SimpleDisplayEffect(ELECTRIC_WARMUP_SPEED, true));

		register(ARMOR, new ArmorEffectUpgrade());
	}

	private static void register(String id, IEquipmentEffect effect) {
		effects.put(id, effect);
	}

	public static Optional<IEquipmentEffect> get(String name) {
		return Optional.ofNullable(effects.get(name));
	}
}
