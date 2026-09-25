package com.diamssword.greenresurgence.render.customPose;

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
	public void beforeRender(AbstractClientPlayerEntity player, PlayerEntityModel<AbstractClientPlayerEntity> model, IPlayerCustomPose pose) {

	}

	@Override
	public void firstPersonRender(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, IPlayerCustomPose pose) {

	}

	@Override
	public void angles(AbstractClientPlayerEntity player, PlayerEntityModel model, IPlayerCustomPose pose) {
		Vec3d movement = Vec3d.ZERO;
		if(player.hasVehicle()) {
			var v = player.getVehicle();
			movement = v.getPos().subtract(v.prevX, v.prevY, v.prevZ);
		}
		double horizontalSpeed = Math.sqrt(movement.x * movement.x + movement.z * movement.z);
		float intensity = MathHelper.clamp((float) (horizontalSpeed / 1.5), 0.0F, 1.0F);
		float phase = (player.age + MinecraftClient.getInstance().getTickDelta()) * 0.5F;

		model.leftLeg.pitch = MathHelper.cos(phase * 0.6662F) * 1.4F * intensity;

		model.rightLeg.pitch = MathHelper.cos(phase * 0.6662F + (float) Math.PI) * 1.4F * intensity;

		model.rightLeg.roll = (float) Math.toRadians(10);
		model.leftLeg.roll = (float) Math.toRadians(-10);
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
