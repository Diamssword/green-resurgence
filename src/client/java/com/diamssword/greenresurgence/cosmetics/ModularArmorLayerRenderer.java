package com.diamssword.greenresurgence.cosmetics;

import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.GeckoLibException;
import software.bernie.geckolib.animatable.client.RenderProvider;

public class ModularArmorLayerRenderer implements ArmorRenderer {
    private final Identifier texture;

    public ModularArmorLayerRenderer(Identifier texture) {
        this.texture = texture;
    }

    @Override
    public void render(MatrixStack matrices,VertexConsumerProvider vertexConsumers,ItemStack stack,LivingEntity entity,EquipmentSlot slot,int light,BipedEntityModel<LivingEntity> model) {

        var model1=RenderProvider.of(stack).getGenericArmorModel(entity,stack, slot, model);
        try {
            if(model1 !=null)
                ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, model1, texture);

        }catch (GeckoLibException ex)
        {
            ex.printStackTrace();

        }
    }
}