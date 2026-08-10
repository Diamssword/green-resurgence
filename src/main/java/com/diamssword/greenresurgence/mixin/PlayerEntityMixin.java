package com.diamssword.greenresurgence.mixin;

import com.diamssword.greenresurgence.items.equipment.IOffHandAttack;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.PosesPackets;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.attributs.Attributes;
import com.diamssword.greenresurgence.systems.character.HealthManager;
import com.diamssword.greenresurgence.systems.equipement.IEquipementItem;
import com.diamssword.greenresurgence.systems.equipement.utils.DamageHandling;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Map;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Shadow
	public abstract Text getDisplayName();

	@Shadow
	@Final
	private static Map<EntityPose, EntityDimensions> POSE_DIMENSIONS;


	@Shadow
	public abstract void playSound(SoundEvent sound, float volume, float pitch);

	@Inject(at = @At("HEAD"), method = "attack", cancellable = true)
	private void cleanedOnAttack(Entity target, CallbackInfo ci) {
		var main = this.getMainHandStack();
		var off = this.getOffHandStack();
		var inverted = this.getComponent(Components.PLAYER_DATA).nextHandSwing == Hand.OFF_HAND;
		if(inverted && off.getItem() instanceof IOffHandAttack && off.getItem() instanceof IEquipementItem eq) {
			DamageHandling.attackWithTool((PlayerEntity) (Object) this, target, Hand.OFF_HAND, off, eq);
			ci.cancel();
		} else if(main.getItem() instanceof IEquipementItem eq) {
			DamageHandling.attackWithTool((PlayerEntity) (Object) this, target, Hand.MAIN_HAND, main, eq);
			ci.cancel();
		} else if(off.getItem() instanceof IOffHandAttack)
			this.getComponent(Components.PLAYER_DATA).nextHandSwing = inverted ? Hand.MAIN_HAND : Hand.OFF_HAND;

	}


	@Inject(at = @At("TAIL"), method = "canFoodHeal", cancellable = true)
	private void canFoodHeal(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(false);
	}

	@Inject(at = @At("HEAD"), method = "updatePose", cancellable = true)
	protected void updatePose(CallbackInfo ci) {
		var comp = Components.PLAYER_DATA.get(this);
		if(comp.isForcedPose()) {
			this.setPose(comp.getPose());
			ci.cancel();
		}
	}

	@Shadow
	public abstract void increaseStat(Identifier stat, int amount);

	@Shadow
	public abstract PlayerInventory getInventory();

	@Shadow
	public abstract float getAttackCooldownProgress(float baseTime);

	@Inject(at = @At("HEAD"), method = "dismountVehicle")
	public void dismountVehicle(CallbackInfo ci) {
		if(this.getVehicle() instanceof PlayerEntity && !this.getEntityWorld().isClient) {
			Channels.MAIN.serverHandle((PlayerEntity) this.getVehicle()).send(new PosesPackets.DismountedPlayerNotify(this.getUuid()));
		}
	}

	@Inject(at = @At("HEAD"), method = "applyDamage", cancellable = true)
	protected void applyDamage(DamageSource source, float amount, CallbackInfo ci) {
		if(!this.isInvulnerableTo(source)) {
			var man = this.getComponent(Components.PLAYER_DATA).healthManager;
			if(!HealthManager.damageByPassShield(source) && man.getShieldAmount() > 0) {
				amount = this.applyArmorToDamage(source, amount);
				amount = this.modifyAppliedDamage(source, amount);
				float var7 = Math.max(amount - this.getAbsorptionAmount(), 0.0F);
				this.setAbsorptionAmount(this.getAbsorptionAmount() - (amount - var7));
				float g = amount - var7;
				if(g > 0.0F && g < 3.4028235E37F) {
					this.increaseStat(Stats.DAMAGE_ABSORBED, Math.round(g * 10.0F));
				}

				if(var7 != 0.0F) {
					// this.addExhaustion(source.getExhaustion());
					this.getDamageTracker().onDamage(source, var7);
					var r = man.attackShield(var7, ((PlayerEntity) (Object) this));
					// this.setHealth(this.getHealth() - var7);
					if(var7 < 3.4028235E37F) {
						this.increaseStat(Stats.DAMAGE_TAKEN, Math.round(var7 * 10.0F));
					}

					this.emitGameEvent(GameEvent.ENTITY_DAMAGE);
					if(r < 0)
						this.setHealth((float) (this.getHealth() + r));
				}
				ci.cancel();
			}
		}
	}


	@Inject(at = @At("HEAD"), method = "damageShield", cancellable = true)
	protected void damageShield(float amount, CallbackInfo ci) {
		var pl = (PlayerEntity) (Object) this;
		if(pl.getActiveItem().getItem() instanceof ShieldItem) {
			if(!this.getWorld().isClient) {
				pl.incrementStat(Stats.USED.getOrCreateStat(pl.getActiveItem().getItem()));
			}

			if(amount >= 3.0F) {
				int i = 1 + MathHelper.floor(amount);
				Hand hand = pl.getActiveHand();
				pl.getActiveItem().damage(i, pl, player -> player.sendToolBreakStatus(hand));
				if(pl.getActiveItem().isEmpty()) {
					if(hand == Hand.MAIN_HAND) {
						this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
					} else {
						this.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
					}
					pl.clearActiveItem();
					this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.getWorld().random.nextFloat() * 0.4F);
				}
			}
			ci.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "disableShield", cancellable = true)
	public void disableShield(boolean sprinting, CallbackInfo ci) {
		var pl = (PlayerEntity) (Object) this;

		float f = 0.25F + (float) EnchantmentHelper.getEfficiency(pl) * 0.05F;
		if(sprinting) {
			f += 0.75F;
		}

		if(this.random.nextFloat() < f) {
			pl.getItemCooldownManager().set(pl.getActiveItem().getItem(), 100);
			pl.clearActiveItem();
			this.getWorld().sendEntityStatus(this, EntityStatuses.BREAK_SHIELD);
		}
		ci.cancel();

	}

	@Inject(at = @At("HEAD"), method = "getDimensions", cancellable = true)
	public void getDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {

		var comp = this.getComponent(Components.PLAYER_DATA);
		var poses = new ArrayList<>(comp.getCustomPoses());
		var p = POSE_DIMENSIONS.get(pose);
		for(int i = poses.size() - 1; i >= 0; i--) {
			var p1 = poses.get(i).changeHitBox(p);
			if(p1 != p) {
				cir.setReturnValue(p1);
				break;
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "getActiveEyeHeight", cancellable = true)
	public void getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
		if(pose == EntityPose.STANDING)
			cir.setReturnValue(dimensions.height * 0.9f);
		else if(pose == EntityPose.CROUCHING)
			cir.setReturnValue(dimensions.height * 0.85f);

	}

	@Inject(at = @At("TAIL"), method = "createPlayerAttributes")
	private static void createLivingAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
		Attributes.plAttributes.values().forEach(v -> cir.getReturnValue().add(v, v.getDefaultValue()));

	}


}