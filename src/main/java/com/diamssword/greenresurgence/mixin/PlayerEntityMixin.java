package com.diamssword.greenresurgence.mixin;

import com.diamssword.greenresurgence.systems.Components;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity {

	public PlayerEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}
	@Inject(at = @At("TAIL"), method = "<init>")
	private void init(World world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci) {
		this.setSneaking(true);
	}
	@Inject(at = @At("HEAD"), method = "updatePose",cancellable = true)
	protected void updatePose(CallbackInfo ci) {
		var comp=Components.PLAYER_DATA.get(this);
		if(comp.isForcedPose()) {
			this.setPose(comp.getPose());
			ci.cancel();
		}
	}
	@Inject(at = @At("HEAD"), method = "getDimensions", cancellable = true)
	public void getDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir)
	{
		if(pose==EntityPose.STANDING||pose==EntityPose.CROUCHING) {
			var comp = this.getComponent(Components.PLAYER_DATA);
			var h = comp.appearance.getRestrainedHeight();
			var w = comp.appearance.getRestrainedWidth();
			var baseH = this.getPose() == EntityPose.CROUCHING ? 1.5f : 1.8f;
			cir.setReturnValue(EntityDimensions.changing(0.6f * w, baseH * h));
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