package com.diamssword.greenresurgence.systems.equipement.effects;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.equipement.AdvEquipmentSlot;
import com.diamssword.greenresurgence.systems.equipement.IEquipmentEffect;
import com.diamssword.greenresurgence.systems.equipement.IEquipmentUpgrade;
import com.diamssword.greenresurgence.systems.equipement.UpgradeActionContext;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.List;

import static net.minecraft.item.ItemStack.MODIFIER_FORMAT;

public class SweepingEffectUpgrade implements IEquipmentEffect {

	@Override
	public void getAttributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> map, AdvEquipmentSlot slot, UpgradeActionContext ctx) {

	}

	@Override
	public void onInteraction(UpgradeActionContext ctx, AdvEquipmentSlot slot, IEquipmentUpgrade.InteractType interaction) {
		if(interaction == IEquipmentUpgrade.InteractType.ATTACK && ctx.getTarget() != null) {
			sweepAttack(ctx.getPlayerSource(), ctx.getTarget(), ctx.getLevel("sweeping").getLevel(), 10f, 2);
		}
	}

	public static void sweepAttack(PlayerEntity attacker, LivingEntity target, int maxCount, float amount, float radius) {

		int i = 0;
		for(LivingEntity livingEntity : attacker.getWorld().getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox().expand(1.0 * radius, 0.25 * radius, 1.0 * radius))) {
			if(i > maxCount)
				break;
			if(livingEntity != attacker
					&& livingEntity != target
					&& !attacker.isTeammate(livingEntity)
					&& (!(livingEntity instanceof ArmorStandEntity) || !((ArmorStandEntity) livingEntity).isMarker())
					&& attacker.squaredDistanceTo(livingEntity) < 9.0) {
				livingEntity.takeKnockback(
						0.4F, (double) MathHelper.sin(attacker.getYaw() * (float) (Math.PI / 180.0)), (double) (-MathHelper.cos(attacker.getYaw() * (float) (Math.PI / 180.0)))
				);
				livingEntity.damage(attacker.getDamageSources().playerAttack(attacker), amount);
				i++;
			}
		}

		attacker.getWorld().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, attacker.getSoundCategory(), 1.0F, 1.0F);
		attacker.spawnSweepAttackParticles();
	}

	@Override
	public void addTooltips(UpgradeActionContext ctx, AdvEquipmentSlot slot, List<Text> tooltip) {
		if(slot == AdvEquipmentSlot.MAINHAND) {
			tooltip.add(
					Text.translatable(
									"attribute.modifier.plus." + EntityAttributeModifier.Operation.ADDITION.getId(),
									MODIFIER_FORMAT.format(ctx.getLevel("sweeping").getLevel()),
									Text.translatable("equipment." + GreenResurgence.ID + ".upgrade.sweeping.targets")
							)
							.formatted(Formatting.BLUE)
			);
		}

	}
}
