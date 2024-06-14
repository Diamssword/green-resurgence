package com.diamssword.greenresurgence.cosmetics;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.clothing.ClothingLoader;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import software.bernie.geckolib.model.DefaultedGeoModel;

public class ClothingLayer  extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private final FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> ctx;
    private final ClothingLoader.Layer layer;
    private final boolean altTexture;
    private ClothingModel<AbstractClientPlayerEntity> model;
    public ClothingLayer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context, ClothingLoader.Layer layer, boolean altTexture, boolean thinArm) {
        super(context);
        this.ctx=context;
        this.altTexture=altTexture;
        this.layer=layer;
        model= new ClothingModel<>(thinArm, altTexture?layer.layer2:layer.layer1,altTexture);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        ctx.getModel().copyBipedStateTo(model);
        model.animateModel(entity,limbAngle,limbDistance,tickDelta);
        model.setAngles(entity,limbAngle,limbDistance,animationProgress,headYaw,headPitch);

        ClothingLoader.instance.getFirstForLayer(layer).ifPresent((c)->{
            model.render(matrices,vertexConsumers.getBuffer(model.getLayer(GreenResurgence.asRessource("textures/cloth/"+layer.toString()+"/"+c.getLeft()+".png"))),light, LivingEntityRenderer.getOverlay(entity,0),1,1,1,1);
        });

    }

}
