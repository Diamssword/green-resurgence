package com.diamssword.greenresurgence.mixin.client;

import com.diamssword.greenresurgence.render.CustomPoseRender.CustomPoseRenderManager;
import com.diamssword.greenresurgence.render.Entities;
import com.diamssword.greenresurgence.render.cosmetics.BackpackLayerRenderer;
import com.diamssword.greenresurgence.render.cosmetics.ClothingLayer;
import com.diamssword.greenresurgence.render.cosmetics.CustomPlayerModel;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.clothing.ClothingLoader;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerRenderMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

	public PlayerRenderMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
		super(ctx, model, shadowRadius);
	}

	@Inject(at = @At("HEAD"), method = "getPositionOffset(Lnet/minecraft/client/network/AbstractClientPlayerEntity;F)Lnet/minecraft/util/math/Vec3d;", cancellable = true)
	public void getPositionOffset(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, CallbackInfoReturnable<Vec3d> cir) {
		var comp = abstractClientPlayerEntity.getComponent(Components.PLAYER_DATA);
		if (comp.getCustomPoseID() != null) {
			var rend = CustomPoseRenderManager.get(comp.getCustomPoseID());
			if (rend != null) {
				cir.setReturnValue(rend.Offset(abstractClientPlayerEntity, comp.getCustomPose()));
			}

		}

	}

	@Inject(at = @At("TAIL"), method = "setupTransforms(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/util/math/MatrixStack;FFF)V")
	public void setupTransforms(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f, float g, float h, CallbackInfo ci) {
		var comp = abstractClientPlayerEntity.getComponent(Components.PLAYER_DATA);
		if (comp.getCustomPoseID() != null) {
			var rend = CustomPoseRenderManager.get(comp.getCustomPoseID());
			if (rend != null) {

				rend.transforms(abstractClientPlayerEntity, matrixStack, this.model, comp.getCustomPose());
			}

		}
	}

	@Inject(at = @At("TAIL"), method = "setModelPose")
	public void setModelPose(AbstractClientPlayerEntity player, CallbackInfo ci) {
		var comp = player.getComponent(Components.PLAYER_DATA);
		if (comp.getCustomPoseID() != null) {
			var rend = CustomPoseRenderManager.get(comp.getCustomPoseID());
			if (rend != null) {
				PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = this.getModel();
				rend.angles(player, playerEntityModel, comp.getCustomPose());
			}

		}
	}

	@Inject(at = @At("TAIL"), method = "<init>")
	private void init(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo info) {

		var th = ((PlayerEntityRenderer) (Object) this);
		((LivingRendererAccessor) th).setModel(new PlayerEntityModel<>(ctx.getPart(slim ? Entities.PLAYER_MODEL_S : Entities.PLAYER_MODEL), slim));
		for (ClothingLoader.Layer value : ClothingLoader.Layer.values()) {
			this.addFeature(new ClothingLayer(th, value, false, slim));
			if (value.layer2 > -1)
				this.addFeature(new ClothingLayer(th, value, true, slim));
		}
		this.addFeature(new BackpackLayerRenderer<>(th));
	}

	@Inject(at = @At("TAIL"), method = "scale(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/util/math/MatrixStack;F)V")
	protected void scale(LivingEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f, CallbackInfo info) {
		CustomPlayerModel.scale(abstractClientPlayerEntity, matrixStack, f, info);
//        float g = 0.9375F;
		//var comp=abstractClientPlayerEntity.getComponent(Components.PLAYER_DATA);


		//  matrixStack.scale(g*comp.appearance.width, g*comp.appearance.height, g*comp.appearance.width);
	}


}
