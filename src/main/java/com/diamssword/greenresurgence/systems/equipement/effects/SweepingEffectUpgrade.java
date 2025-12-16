package com.diamssword.greenresurgence.systems.equipement.effects;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.equipement.*;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.List;

import static net.minecraft.item.ItemStack.MODIFIER_FORMAT;

public class SweepingEffectUpgrade implements IEquipmentEffect {


	public static final String DAMAGE_BONUS = "damage";
	public static final String RADIUS_BONUS = "radius";

	@Override
	public void getAttributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> map, AdvEquipmentSlot slot, UpgradeActionContext ctx) {

	}

	@Override
	public void onInteraction(UpgradeActionContext ctx, AdvEquipmentSlot slot, IEquipmentUpgrade.InteractType interaction) {
		if(interaction == IEquipmentUpgrade.InteractType.PRE_ATTACK && ctx.getTarget() != null && !ctx.isClient()) {
			float cooldownProgress = 1f;
			if(ctx.getLivingSource() instanceof PlayerEntity pl) {
				cooldownProgress = pl.getComponent(Components.PLAYER_DATA).lastCooldownProgress;
			}
			if(cooldownProgress > 0.6f) {
				var baseDmgs = ctx.getContextValue();
				var targets = ctx.getLevel(EquipmentEffects.SWEEPING).getLevel();
				var percent = ctx.getLevel(EquipmentEffects.SWEEPING).getLevel(DAMAGE_BONUS, 20f) / 100f;
				var radius = ctx.getLevel(EquipmentEffects.SWEEPING).getLevel(RADIUS_BONUS, 1f);
				if(targets > 0) {
					sweepAttack(ctx.getLivingSource(), ctx.getTarget(), (int) targets, baseDmgs * percent, radius);
				}
			}
		}
	}

	private static boolean isTeammate(LivingEntity attacker, LivingEntity target) {
		if(target instanceof PlayerEntity)
			return true;
		return attacker.isTeammate(target);
	}

	public static void sweepAttack(LivingEntity attacker, LivingEntity target, int maxCount, float amount, float radius) {
		int i = 0;
		for(LivingEntity livingEntity : attacker.getWorld().getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox().expand(1.0 * radius, 0.25 * radius, 1.0 * radius))) {
			if(i > maxCount)
				break;
			if(livingEntity != attacker && livingEntity != target && !isTeammate(attacker, target) && (!(livingEntity instanceof ArmorStandEntity) || !((ArmorStandEntity) livingEntity).isMarker())
					&& attacker.squaredDistanceTo(livingEntity) < 9 * radius) {
				livingEntity.takeKnockback(
						0.2F * radius, (double) MathHelper.sin(attacker.getYaw() * (float) (Math.PI / 180.0)), (double) (-MathHelper.cos(attacker.getYaw() * (float) (Math.PI / 180.0)))
				);
				DamageSource dmg;
				if(attacker instanceof PlayerEntity pl)
					dmg = attacker.getDamageSources().playerAttack(pl);
				else
					dmg = attacker.getDamageSources().mobAttack(attacker);
				livingEntity.damage(dmg, amount);
				i++;
			}
		}

		attacker.getWorld().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, attacker.getSoundCategory(), 1.0F, 1.0F);
		if(attacker instanceof PlayerEntity pl)
			pl.spawnSweepAttackParticles();
	}

	@Override
	public void addTooltips(UpgradeActionContext ctx, AdvEquipmentSlot slot, List<Text> tooltip) {
		if(slot == AdvEquipmentSlot.MAINHAND) {
			tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".upgrade.sweeping.targets", MODIFIER_FORMAT.format(ctx.getLevel(EquipmentEffects.SWEEPING).getLevel())).formatted(Formatting.BLUE));
			tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".upgrade.sweeping.range", MODIFIER_FORMAT.format(ctx.getLevel(EquipmentEffects.SWEEPING).getLevel(RADIUS_BONUS, 1))).formatted(Formatting.BLUE));
			tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".tooltip.percent.plus", MODIFIER_FORMAT.format(ctx.getLevel(EquipmentEffects.SWEEPING).getLevel(DAMAGE_BONUS, 20)), Text.translatable("equipment." + GreenResurgence.ID + ".upgrade.sweeping.damage")).formatted(Formatting.BLUE));
		}

	}
}
