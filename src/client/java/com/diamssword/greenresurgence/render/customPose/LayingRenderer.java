package com.diamssword.greenresurgence.render.customPose;

import com.diamssword.greenresurgence.systems.character.customPoses.IPlayerCustomPose;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public class LayingRenderer implements ICustomPoseRenderer {
	@Override
	public void transforms(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, PlayerEntityModel<AbstractClientPlayerEntity> model, IPlayerCustomPose pose) {
		matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(-90));
		matrixStack.translate(0, -1, -0.08);
		if(model.sneaking)
			matrixStack.translate(0, 0, -0.2);
		model.sneaking = false;
	}

	@Override
	public void beforeRender(AbstractClientPlayerEntity player, PlayerEntityModel<AbstractClientPlayerEntity> model, IPlayerCustomPose pose) {

	}

	@Override
	public void firstPersonRender(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, IPlayerCustomPose pose) {

	}

	@Override
	public void angles(AbstractClientPlayerEntity player, PlayerEntityModel<AbstractClientPlayerEntity> model, IPlayerCustomPose pose) {

		model.rightLeg.roll = (float) Math.toRadians(12);
		model.leftLeg.roll = (float) Math.toRadians(-5);
		model.rightPants.roll = model.rightLeg.roll;
		model.leftPants.roll = model.leftLeg.roll;
		model.leftArm.roll = (float) Math.toRadians(-20);
		model.rightArm.roll = (float) Math.toRadians(15);
		model.rightSleeve.roll = model.rightArm.roll;
		model.leftSleeve.roll = model.leftArm.roll;
	}
}
