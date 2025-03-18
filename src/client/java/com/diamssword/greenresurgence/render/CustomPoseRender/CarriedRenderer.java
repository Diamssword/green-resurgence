package com.diamssword.greenresurgence.render.CustomPoseRender;

import com.diamssword.greenresurgence.systems.character.customPoses.IPlayerCustomPose;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class CarriedRenderer implements ICustomPoseRenderer {
    @Override
    public void transforms(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack,PlayerEntityModel model,IPlayerCustomPose pose) {
        matrixStack.translate(0,0.5f,1.2f);
        matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));

    }

    @Override
    public void firstPersonRender(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, IPlayerCustomPose pose) {

    }

    @Override
    public void angles(AbstractClientPlayerEntity player, PlayerEntityModel model,IPlayerCustomPose pose) {
        model.leftArm.pitch= (float) Math.toRadians(-85);
        model.rightArm.pitch= (float) Math.toRadians(-85);
        model.leftSleeve.pitch=model.leftArm.pitch;
        model.rightSleeve.pitch=model.rightArm.pitch;
    }

    @Override
    public Vec3d Offset(AbstractClientPlayerEntity player, IPlayerCustomPose pose) {
        return Vec3d.ZERO;
    }
}
