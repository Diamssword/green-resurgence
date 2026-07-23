package com.diamssword.greenresurgence.effects;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class ResurgenceEffects implements EffectRegistryContainer {

	public static OpeningStatutEffect HAMMER_OPENING = new OpeningStatutEffect(StatusEffectCategory.HARMFUL, 0);
	public static final StatusEffect PERCENT_STRENGTH = new DamageModifierStatusEffect(StatusEffectCategory.BENEFICIAL, 16762624, 0.1).addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0, EntityAttributeModifier.Operation.MULTIPLY_BASE);
	public static BleedingStatutEffect BLEEDING = new BleedingStatutEffect(StatusEffectCategory.HARMFUL, 0);
}
