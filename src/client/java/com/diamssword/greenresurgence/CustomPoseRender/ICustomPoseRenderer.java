package com.diamssword.greenresurgence.CustomPoseRender;

import com.diamssword.greenresurgence.systems.character.customPoses.IPlayerCustomPose;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public interface ICustomPoseRenderer {

    public void transforms(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack,IPlayerCustomPose pose);
    public void angles(AbstractClientPlayerEntity player, PlayerEntityModel<AbstractClientPlayerEntity> model,IPlayerCustomPose pose);
    public void firstPersonRender(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack,IPlayerCustomPose pose);
    public Vec3d Offset(AbstractClientPlayerEntity player,IPlayerCustomPose pose);
}
