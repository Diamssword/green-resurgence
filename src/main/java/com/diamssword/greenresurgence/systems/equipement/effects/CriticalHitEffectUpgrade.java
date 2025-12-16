package com.diamssword.greenresurgence.systems.equipement.effects;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.equipement.*;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Random;

import static net.minecraft.item.ItemStack.MODIFIER_FORMAT;

public class CriticalHitEffectUpgrade implements IEquipmentEffect {
	private Random random = new Random();

	public static final String DAMAGE_BONUS = "damage";

	@Override
	public void getAttributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> map, AdvEquipmentSlot slot, UpgradeActionContext ctx) {

	}

	@Override
	public void onInteraction(UpgradeActionContext ctx, AdvEquipmentSlot slot, IEquipmentUpgrade.InteractType interaction) {
		if(interaction == IEquipmentUpgrade.InteractType.PRE_ATTACK && slot == AdvEquipmentSlot.MAINHAND && !ctx.isClient()) {
			float cooldownProgress = 1f;
			if(ctx.getLivingSource() instanceof PlayerEntity pl) {
				cooldownProgress = pl.getComponent(Components.PLAYER_DATA).lastCooldownProgress;
			}
			if(cooldownProgress > 0.9f) { //only crit if weapon is 90% loaded CANT DO THAT
				var percent = ctx.getLevel(EquipmentEffects.CRITICAL_HIT).getLevel();
				var damagePercent = ctx.getLevel(EquipmentEffects.CRITICAL_HIT).getLevel(DAMAGE_BONUS, 20);
				if(percent > 0) {
					if(random.nextInt(0, 100) < percent) {
						var baseDamage = ctx.getContextValue();
						ctx.setReturnValue(ctx.getReturnValue() + (baseDamage * (damagePercent / 100f)));
						ctx.getSource().getWorld().playSound(null, ctx.getSource().getX(), ctx.getSource().getY(), ctx.getSource().getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, ctx.getSource().getSoundCategory(), 1.0F, 1.0F);
						this.addCritParticles(ctx.getLivingSource(), ctx.getTarget());
					}
				}
			}
		}
	}

	public void addCritParticles(LivingEntity source, Entity target) {
		if(source instanceof PlayerEntity pl) {
			pl.addCritParticles(target);
		}
	}

	public static float getDealtDamage(LivingEntity attacker) {
		float f = (float) attacker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);

		float h = 1f;
		if(attacker instanceof PlayerEntity pl)
			h = pl.getAttackCooldownProgress(0.5F);
		f *= 0.2F + h * h * 0.8F;

		return f;
	}

	@Override
	public void addTooltips(UpgradeActionContext ctx, AdvEquipmentSlot slot, List<Text> tooltip) {
		if(slot == AdvEquipmentSlot.MAINHAND) {
			tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".tooltip.percent", MODIFIER_FORMAT.format(ctx.getLevel(EquipmentEffects.CRITICAL_HIT).getLevel()), Text.translatable("equipment." + GreenResurgence.ID + ".upgrade.critical.chance")).formatted(Formatting.BLUE));
			tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".tooltip.percent.plus", MODIFIER_FORMAT.format(ctx.getLevel(EquipmentEffects.CRITICAL_HIT).getLevel(DAMAGE_BONUS, 20)), Text.translatable("equipment." + GreenResurgence.ID + ".upgrade.critical.damage")).formatted(Formatting.BLUE));
		}

	}
}
