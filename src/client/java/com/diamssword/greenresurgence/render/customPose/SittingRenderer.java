package com.diamssword.greenresurgence.render.customPose;

import com.diamssword.greenresurgence.systems.character.customPoses.IPlayerCustomPose;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class SittingRenderer implements ICustomPoseRenderer {
	@Override
	public void transforms(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, PlayerEntityModel model, IPlayerCustomPose pose) {

	}

	@Override
	public void beforeRender(AbstractClientPlayerEntity player, PlayerEntityModel<AbstractClientPlayerEntity> model, IPlayerCustomPose pose) {

	}

	@Override
	public void firstPersonRender(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, IPlayerCustomPose pose) {

	}

	@Override
	public void angles(AbstractClientPlayerEntity player, PlayerEntityModel model, IPlayerCustomPose pose) {
		model.leftLeg.pitch = (float) Math.toRadians(-80);
		model.rightLeg.pitch = (float) Math.toRadians(-80);
		model.rightLeg.yaw = (float) Math.toRadians(12);
		model.leftLeg.yaw = (float) Math.toRadians(-12);
		model.rightPants.pitch = model.rightLeg.pitch;
		model.leftPants.pitch = model.leftLeg.pitch;
		model.rightPants.yaw = model.rightLeg.yaw;
		model.leftPants.yaw = model.leftLeg.yaw;
	}

	@Override
	public Vec3d Offset(AbstractClientPlayerEntity player, IPlayerCustomPose pose) {
		return new Vec3d(0, -0.5, 0);
	}

}
