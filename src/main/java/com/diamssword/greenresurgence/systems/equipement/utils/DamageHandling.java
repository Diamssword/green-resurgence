package com.diamssword.greenresurgence.systems.equipement.utils;

import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.character.PlayerData;
import com.diamssword.greenresurgence.systems.equipement.AdvEquipmentSlot;
import com.diamssword.greenresurgence.systems.equipement.IEquipementItem;
import com.diamssword.greenresurgence.systems.equipement.IEquipmentUpgrade;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class DamageHandling {
	public static void attackWithTool(PlayerEntity source, Entity target, Hand usedHand, ItemStack usedStack, IEquipementItem equipment) {
		if(target.isAttackable()) {
			if(!target.handleAttack(source)) {
				if(usedHand == Hand.OFF_HAND) {
					if(!source.getMainHandStack().isEmpty()) {
						source.getAttributes().removeModifiers(source.getMainHandStack().getAttributeModifiers(EquipmentSlot.MAINHAND));
					}
					source.getAttributes().addTemporaryModifiers(usedStack.getAttributeModifiers(EquipmentSlot.MAINHAND));
				}
				var eq = equipment.getEquipment(usedStack);
				float f = (float) source.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);

				float h = source.getAttackCooldownProgress(0.5F);
				var comp = source.getComponent(Components.PLAYER_DATA);
				comp.lastCooldownProgress = h;
				f *= 0.2F + h * h * 0.8F;
				source.resetLastAttackedTicks();
				if(f > 0.0F) {
					float j = 0.0F;
					if(target instanceof LivingEntity) {
						j = ((LivingEntity) target).getHealth();

					}

					Vec3d vec3d = target.getVelocity();
					var modifiedDmg = eq.onInteraction(source, AdvEquipmentSlot.MAINHAND, IEquipmentUpgrade.InteractType.PRE_ATTACK, new ExtraEntityHitResult(target, usedStack, f));
					boolean bl6 = target.damage(source.getDamageSources().playerAttack(source), modifiedDmg);
					if(bl6) {

						if(target instanceof ServerPlayerEntity && target.velocityModified) {
							((ServerPlayerEntity) target).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
							target.velocityModified = false;
							target.setVelocity(vec3d);
						}


						source.onAttacking(target);
						if(target instanceof LivingEntity) {
							EnchantmentHelper.onUserDamaged((LivingEntity) target, source);
						}

						EnchantmentHelper.onTargetDamaged(source, target);
						Entity entity = target;
						if(target instanceof EnderDragonPart) {
							entity = ((EnderDragonPart) target).owner;
						}

						if(!source.getWorld().isClient && !usedStack.isEmpty() && entity instanceof LivingEntity) {
							usedStack.postHit((LivingEntity) entity, source);
							if(usedStack.isEmpty()) {
								source.setStackInHand(usedHand, ItemStack.EMPTY);
							}
						}

						if(target instanceof LivingEntity) {
							float m = j - ((LivingEntity) target).getHealth();
							source.increaseStat(Stats.DAMAGE_DEALT, Math.round(m * 10.0F));
							if(source.getWorld() instanceof ServerWorld && m > 2.0F) {
								int n = (int) (m * 0.5);
								((ServerWorld) source.getWorld())
										.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getBodyY(0.5), target.getZ(), n, 0.1, 0.0, 0.1, 0.2);
							}
						}

						source.addExhaustion(0.1F);
					} else {
						source.getWorld().playSound(null, source.getX(), source.getY(), source.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, source.getSoundCategory(), 1.0F, 1.0F);

					}
				}
				if(usedHand == Hand.OFF_HAND) {
					source.getAttributes().removeModifiers(usedStack.getAttributeModifiers(EquipmentSlot.MAINHAND));
					if(!source.getMainHandStack().isEmpty()) {
						source.getAttributes().addTemporaryModifiers(source.getMainHandStack().getAttributeModifiers(EquipmentSlot.MAINHAND));
					}
					comp.nextHandSwing = Hand.MAIN_HAND;
				} else
					comp.nextHandSwing = Hand.OFF_HAND;
				if(!source.getWorld().isClient)
					PlayerData.syncApparence(source);
			}
		}
	}
}
