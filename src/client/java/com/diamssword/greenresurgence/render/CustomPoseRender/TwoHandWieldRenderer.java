package com.diamssword.greenresurgence.render.CustomPoseRender;

import com.diamssword.greenresurgence.items.weapons.ICustomPoseWeapon;
import com.diamssword.greenresurgence.systems.character.customPoses.IPlayerCustomPose;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3d;

public class TwoHandWieldRenderer implements ICustomPoseRenderer {
    @Override
    public void transforms(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack,PlayerEntityModel model,IPlayerCustomPose pose) {

    }

    @Override
    public void firstPersonRender(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, IPlayerCustomPose pose) {

    }

    @Override
    public void angles(AbstractClientPlayerEntity player, PlayerEntityModel model,IPlayerCustomPose pose) {
        var alt=false;
        var chain=false;
        if(player.getMainHandStack().getItem() instanceof ICustomPoseWeapon wi)
        {
            alt=wi.customPoseMode()==1;
            chain =wi.customPoseMode()==2;
        }
        if(!chain) {
            if (player.getMainArm() == Arm.RIGHT) {
                model.rightArm.roll = (float) Math.toRadians(-20);
                model.rightArm.pitch = model.rightArm.pitch + (float) Math.toRadians(-50);
                model.leftArm.pitch = (model.rightArm.pitch * 0.5f) + (float) Math.toRadians(alt ? -10 : -30);
                model.leftArm.yaw = -(float) Math.toRadians(-30);
                model.rightArm.yaw = -model.leftArm.yaw;
            } else {
                model.leftArm.roll = (float) Math.toRadians(20);
                model.leftArm.pitch = model.leftArm.pitch + (float) Math.toRadians(-50);
                model.rightArm.pitch = (model.leftArm.pitch * 0.5f) + (float) Math.toRadians(-10);
                model.rightArm.yaw = (float) Math.toRadians(-30);
                model.leftArm.yaw = -model.rightArm.yaw;
            }
        }
        else
        {
            if (player.getMainArm() == Arm.RIGHT) {
                model.rightArm.roll = (float) Math.toRadians(20);
                model.leftArm.pitch = (model.rightArm.pitch * 0.5f) + (float) Math.toRadians(-80);
                model.leftArm.yaw = -(float) Math.toRadians(-30);
                model.rightArm.yaw = -model.leftArm.yaw;
            }
            else{
                model.leftArm.roll = (float) Math.toRadians(20);
                model.rightArm.pitch = (model.leftArm.pitch * 0.5f) + (float) Math.toRadians(-80);
                model.rightArm.yaw = -(float) Math.toRadians(30);
                model.leftArm.yaw = -model.rightArm.yaw;
            }
        }
        model.leftSleeve.copyTransform(model.leftArm);
        model.rightSleeve.copyTransform(model.rightArm);
    }

    @Override
    public Vec3d Offset(AbstractClientPlayerEntity player, IPlayerCustomPose pose) {
        return Vec3d.ZERO;
    }
}
