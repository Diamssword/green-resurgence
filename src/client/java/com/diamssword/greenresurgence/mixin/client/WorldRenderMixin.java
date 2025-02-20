package com.diamssword.greenresurgence.mixin.client;

import com.diamssword.greenresurgence.CustomPoseRender.CustomPoseRenderManager;
import com.diamssword.greenresurgence.systems.Components;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRenderMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void injectCustomRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        PlayerEntity player = client.player;

        if (world == null || player == null) return;
        var comp=client.player.getComponent(Components.PLAYER_DATA);
        if(comp.getCustomPoseID() !=null)
        {
            var rend= CustomPoseRenderManager.get(comp.getCustomPoseID());
            if(rend !=null)
            {
                rend.firstPersonRender(client.player,matrices,comp.getCustomPose());
            }

        }
    }
}