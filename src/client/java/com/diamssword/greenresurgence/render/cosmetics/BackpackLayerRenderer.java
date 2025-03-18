package com.diamssword.greenresurgence.render.cosmetics;

import com.diamssword.greenresurgence.containers.player.CustomPlayerInventory;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.character.PlayerInventoryData;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.GeckoLibException;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;

public class BackpackLayerRenderer <T extends LivingEntity, M extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {


    public BackpackLayerRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if(entity instanceof PlayerEntity pl)
        {
            var inventory=pl.getComponent(Components.PLAYER_INVENTORY);

            var stack=inventory.getBackpackStack();
            if(!stack.isEmpty() && stack.getItem() instanceof GeoItem) {
                var model1 = RenderProvider.of(stack).getHumanoidArmorModel(entity, stack, EquipmentSlot.CHEST, (BipedEntityModel<LivingEntity>) this.getContextModel());

                try {
                    if (model1 != null) {

                        VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getCutout(), false, false);

                        model1.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
                    }

                } catch (GeckoLibException ex) {
                    ex.printStackTrace();

                }
        }
        }
    }
}