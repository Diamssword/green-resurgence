package com.diamssword.greenresurgence.cosmetics;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.Components;
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

import java.awt.*;
import java.util.Optional;

public class ClothingLayer  extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private final FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> ctx;
    private final ClothingLoader.Layer layer;
    public final boolean altTexture;
    private final ClothingModel<AbstractClientPlayerEntity> model;
    public ClothingLayer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context, ClothingLoader.Layer layer, boolean altTexture, boolean thinArm) {
        super(context);
        this.ctx=context;
        this.altTexture=altTexture;
        this.layer=layer;
        model= new ClothingModel<>(thinArm, altTexture?layer.layer2:layer.layer1,altTexture);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        var data=entity.getComponent(Components.PLAYER_DATA);
        if(data.appearance!=null)
        {
                var c1=data.appearance.getClothDatas(layer);
                if(c1.isPresent())
                {
                    var c=c1.get();
                    ctx.getModel().copyBipedStateTo(model);
                    model.animateModel(entity,limbAngle,limbDistance,tickDelta);
                 //   model.setAngles(entity,limbAngle,limbDistance,animationProgress,headYaw,headPitch);
                    var col=new Color(255,255,255);
                    if(c.needColor())
                    {
                        col=new Color(c.color()).brighter();//dirty trick to make color fit more with the website viewer
                    }
                        model.render(matrices,vertexConsumers.getBuffer(model.getLayer(GreenResurgence.asRessource("textures/cloth/"+layer.toString()+"/"+c.texture()+".png"))),light, LivingEntityRenderer.getOverlay(entity,0),col.getRed()/255f, col.getGreen() /255f,  col.getBlue() /255f,1);
                }
        }
    }

}
