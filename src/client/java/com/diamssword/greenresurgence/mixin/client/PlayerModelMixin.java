package com.diamssword.greenresurgence.mixin.client;

import com.diamssword.greenresurgence.render.customPose.CustomPoseRenderManager;
import com.diamssword.greenresurgence.systems.Components;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityModel.class)
public abstract class PlayerModelMixin {


	@Inject(at = @At("TAIL"), method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V")
	public void setAngles(LivingEntity livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
		if(livingEntity instanceof AbstractClientPlayerEntity player) {
			var comp = player.getComponent(Components.PLAYER_DATA);
			PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = (PlayerEntityModel<AbstractClientPlayerEntity>) (Object) this;
			comp.getCustomPosesMap().forEach((k, v) -> {
				var rend = CustomPoseRenderManager.get(k, v);
				if(rend != null) {
					rend.angles(player, playerEntityModel, v);
				}
			});
		}
	}

}
