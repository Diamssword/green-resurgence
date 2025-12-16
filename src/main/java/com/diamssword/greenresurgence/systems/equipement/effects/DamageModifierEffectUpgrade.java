package com.diamssword.greenresurgence.systems.equipement.effects;

import com.diamssword.greenresurgence.effects.ResurgenceEffects;
import com.diamssword.greenresurgence.systems.equipement.*;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.List;

public class DamageModifierEffectUpgrade implements IEquipmentEffect {
	@Override
	public void getAttributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> map, AdvEquipmentSlot slot, UpgradeActionContext ctx) {

	}

	@Override
	public void onInteraction(UpgradeActionContext ctx, AdvEquipmentSlot slot, IEquipmentUpgrade.InteractType interaction) {
		if(interaction == IEquipmentUpgrade.InteractType.PRE_ATTACK && !ctx.isClient()) {
			var dmgs = ctx.getContextValue();
			if(ctx.getWeapon().getItem() instanceof IEquipementItem equi) {
				var equipment = equi.getEquipment(ctx.getWeapon());
				if(ctx.getTarget().hasStatusEffect(ResurgenceEffects.HAMMER_OPENING) && !equipment.getEquipment().getEquipmentType().equals(Equipments.TYPE_HAMMER)) {
					ctx.getTarget().removeStatusEffect(ResurgenceEffects.HAMMER_OPENING);
					ctx.setReturnValue(ctx.getReturnValue() + (dmgs * 0.2f));
					ctx.getSource().getWorld().playSound(null, ctx.getSource().getX(), ctx.getSource().getY(), ctx.getSource().getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, ctx.getSource().getSoundCategory(), 1.0F, 1.0F);
					this.addMagicCritParticles(ctx.getLivingSource(), ctx.getTarget());
				}
			}
		}
	}

	public void addMagicCritParticles(LivingEntity source, Entity target) {
		if(source instanceof PlayerEntity pl) {
			pl.addEnchantedHitParticles(target);
		}
	}

	@Override
	public void addTooltips(UpgradeActionContext ctx, AdvEquipmentSlot slot, List<Text> tooltip) {

	}
}
