package com.diamssword.greenresurgence.render.CustomPoseRender;

import com.diamssword.greenresurgence.systems.character.customPoses.IPlayerCustomPose;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RidingBikeRenderer implements ICustomPoseRenderer {
	@Override
	public void transforms(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, PlayerEntityModel model, IPlayerCustomPose pose) {
		model.riding = false;
	}

	@Override
	public void firstPersonRender(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, IPlayerCustomPose pose) {

	}

	@Override
	public void angles(AbstractClientPlayerEntity player, PlayerEntityModel model, IPlayerCustomPose pose) {
		float o = player.limbAnimator.getSpeed(MinecraftClient.getInstance().getTickDelta()) * (player == MinecraftClient.getInstance().player ? 10f : 1f);
		float n = player.limbAnimator.getPos(MinecraftClient.getInstance().getTickDelta()) * (player == MinecraftClient.getInstance().player ? 10f : 1f);
		if(o > 1.0F) {
			o = 1.0F;
		}
		model.rightLeg.pitch = MathHelper.cos(n * 0.6662F) * 0.4F * o;
		model.leftLeg.pitch = MathHelper.cos(n * 0.6662F + (float) Math.PI) * 0.4F * o;
		model.rightLeg.yaw = (float) Math.toRadians(5);
		model.rightLeg.roll = (float) Math.toRadians(5);
		//model.rightLeg.pitch = (float) Math.toRadians(-10);
		model.leftLeg.yaw = (float) Math.toRadians(-5);
		model.leftLeg.roll = (float) Math.toRadians(-5);
		model.rightArm.pitch = (float) Math.toRadians(-50);
		model.leftArm.pitch = (float) Math.toRadians(-50);

		model.leftPants.copyTransform(model.leftLeg);
		model.rightPants.copyTransform(model.rightLeg);
		model.leftSleeve.copyTransform(model.leftArm);
		model.rightSleeve.copyTransform(model.rightArm);
	}

	@Override
	public Vec3d Offset(AbstractClientPlayerEntity player, IPlayerCustomPose pose) {
		return Vec3d.ZERO;
	}
}
