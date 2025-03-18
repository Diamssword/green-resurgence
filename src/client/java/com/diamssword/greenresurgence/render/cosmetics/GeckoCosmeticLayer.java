package com.diamssword.greenresurgence.render.cosmetics;

import com.diamssword.greenresurgence.GreenResurgence;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import software.bernie.geckolib.model.DefaultedGeoModel;

import java.util.HashMap;
import java.util.Map;

public class GeckoCosmeticLayer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    private GeckoCosmeticRenderer<AnimatablePlayer> renderer;
    private final AnimatablePlayer pl=new AnimatablePlayer();
    private Map<PlayerEntity,AnimatablePlayer> animations = new HashMap<>();
    private FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> ctx;
    public GeckoCosmeticLayer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
        this.ctx=context;

        renderer=new GeckoCosmeticRenderer<>(new DefaultedGeoModel<>(GreenResurgence.asRessource("test")) {
            @Override
            protected String subtype() {
                return "cosmetic";
            }
        });
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {

        var anim=getAnimation(entity);
        renderer.prepForRender(entity, entity.getMainHandStack(), EquipmentSlot.HEAD, this.getContextModel(),anim);
        renderer.render(matrices, anim,null,null,null, light);
    }
    public AnimatablePlayer getAnimation(PlayerEntity entity)
    {
        return pl;
        /*if(!animations.containsKey(entity))
            animations.put(entity,new AnimatablePlayer());
        return animations.get(entity);
        */
    }
}
