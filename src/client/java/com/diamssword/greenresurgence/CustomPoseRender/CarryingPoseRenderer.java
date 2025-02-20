package com.diamssword.greenresurgence.CustomPoseRender;

import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.character.customPoses.IPlayerCustomPose;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

public class CarryingPoseRenderer implements ICustomPoseRenderer {
    @Override
    public void transforms(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack,IPlayerCustomPose pose) {
        var et=abstractClientPlayerEntity.getComponent(Components.PLAYER_DATA).getCarriedEntity();
        if(et.isPresent())
        {
            renderEntity(et.get(),matrixStack,abstractClientPlayerEntity.getHeight(),180);
        }

    }
    private void renderEntity(Entity et, MatrixStack matrixStack,float yoffset,float yangle)
    {
        var mc=MinecraftClient.getInstance();
        var render=mc.getEntityRenderDispatcher().getRenderer(et);
        if(render !=null)
        {
            matrixStack.push();
            var vertex=mc.getBufferBuilders().getEntityVertexConsumers();

            matrixStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(yangle));
            matrixStack.translate(0,yoffset,0);
            var l=mc.getEntityRenderDispatcher().getLight(et,mc.getTickDelta());
            render.render(et,0,0,matrixStack,vertex, l);
            matrixStack.pop();
        }
    }
    @Override
    public void firstPersonRender(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, IPlayerCustomPose pose) {
        var et=abstractClientPlayerEntity.getComponent(Components.PLAYER_DATA).getCarriedEntity();
        if(et.isPresent())
        {
            var mc=MinecraftClient.getInstance();
            renderEntity(et.get(),matrixStack,0.5f,mc.player.lastRenderYaw);
        }
    }

    @Override
    public void angles(AbstractClientPlayerEntity player, PlayerEntityModel model,IPlayerCustomPose pose) {
        model.leftArm.pitch= (float) Math.toRadians(-180);
        model.rightArm.pitch= (float) Math.toRadians(-180);
        model.leftSleeve.pitch=model.leftArm.pitch;
        model.rightSleeve.pitch=model.rightArm.pitch;
    }

    @Override
    public Vec3d Offset(AbstractClientPlayerEntity player, IPlayerCustomPose pose) {
        return Vec3d.ZERO;
    }
}
