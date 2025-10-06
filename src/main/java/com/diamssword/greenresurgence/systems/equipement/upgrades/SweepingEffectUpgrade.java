package com.diamssword.greenresurgence.systems.equipement.upgrades;

import com.diamssword.greenresurgence.systems.equipement.AdvEquipmentSlot;
import com.diamssword.greenresurgence.systems.equipement.IEquipmentDef;
import com.diamssword.greenresurgence.systems.equipement.IEquipmentUpgrade;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class SweepingEffectUpgrade implements IEquipmentUpgrade {
	@Override
	public boolean canBeApplied(IEquipmentDef equipment, ItemStack stack) {
		return true;
	}

	@Override
	public String slot(IEquipmentDef equipment) {
		return "";
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(AdvEquipmentSlot slot, @Nullable PlayerEntity player) {
		return null;
	}

	@Override
	public void onInteraction(PlayerEntity wearer, AdvEquipmentSlot slot, InteractType interaction, HitResult context) {
		if(interaction == InteractType.ATTACK && context instanceof EntityHitResult res && res.getEntity() instanceof LivingEntity living) {
			sweepAttack(wearer, living, 2, 10f, 2);
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
	public float damageWheight() {
		return 0;
	}

	@Override
	public void onTick(ItemStack stack, Entity parent) {

	}
}
