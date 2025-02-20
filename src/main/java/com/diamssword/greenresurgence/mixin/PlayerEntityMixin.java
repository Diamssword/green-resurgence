package com.diamssword.greenresurgence.mixin;

import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.PosesPackets;
import com.diamssword.greenresurgence.systems.Components;
import com.mojang.authlib.GameProfile;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity {

	@Shadow public abstract Text getDisplayName();

	@Shadow @Final private static Map<EntityPose, EntityDimensions> POSE_DIMENSIONS;


	@Shadow public abstract void playSound(SoundEvent sound, float volume, float pitch);

	public PlayerEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}
	@Inject(at = @At("TAIL"), method = "<init>")
	private void init(World world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci) {
		this.setSneaking(true);
		this.setSneaking(false);
	}
	@Inject(at = @At("TAIL"), method = "canFoodHeal",cancellable = true)
	private void canFoodHeal(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(false);
	}

	@Inject(at = @At("HEAD"), method = "updatePose",cancellable = true)
	protected void updatePose(CallbackInfo ci) {
		var comp=Components.PLAYER_DATA.get(this);
		if(comp.isForcedPose()) {
			this.setPose(comp.getPose());
			ci.cancel();
		}
	}
	@Inject(at=@At("HEAD"),method = "dismountVehicle")
	public void dismountVehicle(CallbackInfo ci)
	{
		if(this.getVehicle() instanceof PlayerEntity&& !this.getEntityWorld().isClient)
		{
			Channels.MAIN.serverHandle((PlayerEntity) this.getVehicle()).send(new PosesPackets.DismountedPlayerNotify(this.getUuid()));
		}
	}
	@Inject(at = @At("HEAD"), method = "damageShield",cancellable = true)
	protected void damageShield(float amount, CallbackInfo ci) {
		var pl=(PlayerEntity)(Object) this;
		if (pl.getActiveItem().getItem() instanceof ShieldItem ) {
			if (!this.getWorld().isClient) {
				pl.incrementStat(Stats.USED.getOrCreateStat(pl.getActiveItem().getItem()));
			}

			if (amount >= 3.0F) {
				int i = 1 + MathHelper.floor(amount);
				Hand hand = pl.getActiveHand();
				pl.getActiveItem().damage(i, pl, player -> player.sendToolBreakStatus(hand));
				if (pl.getActiveItem().isEmpty()) {
					if (hand == Hand.MAIN_HAND) {
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
	@Inject(at = @At("HEAD"), method = "disableShield")
	public void disableShield(boolean sprinting, CallbackInfo ci) {
		var pl=(PlayerEntity)(Object) this;

		float f = 0.25F + (float)EnchantmentHelper.getEfficiency(pl) * 0.05F;
		if (sprinting) {
			f += 0.75F;
		}

		if (this.random.nextFloat() < f) {
			pl.getItemCooldownManager().set(pl.getActiveItem().getItem(), 100);
			pl.clearActiveItem();
			this.getWorld().sendEntityStatus(this, EntityStatuses.BREAK_SHIELD);
		}
		ci.cancel();

	}
	@Inject(at = @At("HEAD"), method = "getDimensions", cancellable = true)
	public void getDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir)
	{
		if(pose==EntityPose.STANDING||pose==EntityPose.CROUCHING||pose==EntityPose.SITTING) {
			var comp = this.getComponent(Components.PLAYER_DATA);
			var h = comp.appearance.getRestrainedHeight();
			//Disabled for now var w = comp.appearance.getRestrainedWidth();
			var baseH = this.getPose() == EntityPose.CROUCHING ? 1.5f : 1.8f;
			var dim=EntityDimensions.changing(0.6f/* * w*/, baseH * h);
			var custpo=comp.getCustomPose();
			if(custpo !=null)
				dim=custpo.changeHitBox(comp.player,dim);
			cir.setReturnValue(dim);
		}
		else
		{
			var comp = this.getComponent(Components.PLAYER_DATA);
			var custpo=comp.getCustomPose();
			if(custpo !=null)
				cir.setReturnValue(custpo.changeHitBox(comp.player,POSE_DIMENSIONS.get(pose)));
		}

	}
	@Inject(at = @At("HEAD"), method = "getActiveEyeHeight", cancellable = true)
	public void getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
		if(pose==EntityPose.STANDING)
			cir.setReturnValue(dimensions.height*0.9f);
		else if(pose==EntityPose.CROUCHING)
			cir.setReturnValue(dimensions.height*0.85f);

	}
}