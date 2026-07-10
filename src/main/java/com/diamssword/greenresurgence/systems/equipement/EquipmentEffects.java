package com.diamssword.greenresurgence.systems.equipement;

import com.diamssword.greenresurgence.systems.attributs.Attributes;
import com.diamssword.greenresurgence.systems.equipement.effects.*;
import com.diamssword.greenresurgence.systems.lootables.Lootables;
import net.minecraft.entity.attribute.EntityAttributes;

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
	public static final String OPENING = "opening";
	public static final String ARMOR = "armor";
	public static final String WEAPON_ARMOR_TOUGHNESS = "weapon_armor_toughness";
	public static final String LOYALTY = "loyalty";


	public static void init() {
		register(SWEEPING, new SweepingEffectUpgrade());
		register(ATTACK_SPEED, SimpleAttributeEffect.addition(ATTACK_SPEED, EntityAttributes.GENERIC_ATTACK_SPEED, EquipmentValues.ATTACK_SPEED_MODIFIER_ID));
		register(ATTACK_DAMAGE, SimpleAttributeEffect.addition(ATTACK_DAMAGE, EntityAttributes.GENERIC_ATTACK_DAMAGE, EquipmentValues.ATTACK_DAMAGE_MODIFIER_ID));
		register(ATTACK_RANGE, new AttackRangeEffectUpgrade());
		register(CRITICAL_HIT, new CriticalHitEffectUpgrade());
		register(BASE_DAMAGE_MOD, new DamageModifierEffectUpgrade());
		register(OPENING, new OpeningEffectUpgrade());
		register(TOOL_HAMMER, new LootingToolEffect(Lootables.HAMMER, "hammer"));
		register(TOOL_WRENCH, new LootingToolEffect(Lootables.WRENCH, "wrench"));
		register(MOVE_SPEED, SimpleAttributeEffect.multiplyBase(MOVE_SPEED, EntityAttributes.GENERIC_MOVEMENT_SPEED, EquipmentValues.MOVE_SPEED_MODIFIER_ID).workOnAllSlot());
		register(WEAPON_ARMOR_TOUGHNESS, SimpleAttributeEffect.addition(WEAPON_ARMOR_TOUGHNESS, EntityAttributes.GENERIC_ARMOR_TOUGHNESS, EquipmentValues.ARMOR_TOUGHNESS_MODIFIER_ID));
		register(CONTAMINATION_REDUCTION, SimpleAttributeEffect.addition(CONTAMINATION_REDUCTION, Attributes.CONTAMINATION_REDUCTION, EquipmentValues.CONTAMINATION_REDUCTION_MODIFIER_ID));
		register(LOYALTY, new LoyaltyEffectUpgrade());
		register(ARMOR, new ArmorEffectUpgrade());
	}

	private static void register(String id, IEquipmentEffect effect) {
		effects.put(id, effect);
	}

	public static Optional<IEquipmentEffect> get(String name) {
		return Optional.ofNullable(effects.get(name));
	}
}
