package com.diamssword.greenresurgence.render.customPose;

import com.diamssword.greenresurgence.render.customPose.animated.AnimatedPosGeoModel;
import com.diamssword.greenresurgence.render.customPose.animated.PlayerModelBakedGeoModel;
import com.diamssword.greenresurgence.systems.character.customPoses.AnimatedPose;
import com.diamssword.greenresurgence.systems.character.customPoses.IPlayerCustomPose;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib.core.animation.AnimationState;

import java.util.HashMap;
import java.util.Map;

public class AnimatedPosRenderer implements ICustomPoseRenderer {

	private static final AnimatedPosGeoModel animationModel = new AnimatedPosGeoModel();
	private static final Map<PlayerEntityModel<AbstractClientPlayerEntity>, PlayerModelBakedGeoModel> models = new HashMap<>();

	@Override
	public void transforms(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, PlayerEntityModel<AbstractClientPlayerEntity> model, IPlayerCustomPose pose) {

	}

	@Override
	public void angles(AbstractClientPlayerEntity player, PlayerEntityModel<AbstractClientPlayerEntity> model, IPlayerCustomPose pose) {
		AnimatedPose anim = (AnimatedPose) pose;
		animationModel.getAnimationProcessor().setActiveModel(models.computeIfAbsent(model, a -> new PlayerModelBakedGeoModel(model)));
		var partialTick = MinecraftClient.getInstance().getTickDelta();
		float limbSwingAmount = 0;
		float limbSwing = 0;

		boolean shouldSit = player.hasVehicle() && (player.getVehicle() != null);
		if(!shouldSit && player.isAlive()) {
			limbSwingAmount = player.limbAnimator.getSpeed(partialTick);
			limbSwing = player.limbAnimator.getPos(partialTick);

			if(limbSwingAmount > 1f)
				limbSwingAmount = 1f;
		}
		float motionThreshold = 0.015f;
		Vec3d velocity = player.getVelocity();
		float avgVelocity = (float) ((Math.abs(velocity.x) + Math.abs(velocity.z)) / 2f);
		AnimationState<AnimatedPose> animationState = new AnimationState<>(anim, limbSwing, limbSwingAmount, partialTick, avgVelocity >= motionThreshold && limbSwingAmount != 0);
		animationModel.handleAnimations(anim, player.getId(), animationState);
		model.leftSleeve.copyTransform(model.leftArm);
		model.rightSleeve.copyTransform(model.rightArm);
		model.leftPants.copyTransform(model.leftLeg);
		model.rightPants.copyTransform(model.rightPants);
		model.hat.copyTransform(model.head);
		model.jacket.copyTransform(model.body);
	}


	@Override
	public void beforeRender(AbstractClientPlayerEntity player, PlayerEntityModel<AbstractClientPlayerEntity> model, IPlayerCustomPose pose) {

	}

	@Override
	public void firstPersonRender(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, IPlayerCustomPose pose) {

	}

}
