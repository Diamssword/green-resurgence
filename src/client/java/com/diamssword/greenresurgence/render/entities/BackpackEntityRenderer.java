package com.diamssword.greenresurgence.render.entities;

import com.diamssword.greenresurgence.entities.BackpackEntity;
import com.diamssword.greenresurgence.items.BackPackItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;

public class BackpackEntityRenderer extends EntityRenderer<BackpackEntity> {

    public BackpackEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(BackpackEntity itemEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
       var stack= itemEntity.getStack();
       if(!stack.isEmpty() && stack.getItem() instanceof BackPackItem) {
           var model1 = RenderProvider.of(stack).getCustomRenderer();
           if(model1 !=null)
           {
               matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(itemEntity.getBodyYaw()));
               matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-75f));
               matrixStack.translate(-0.5f,-1.6f,-0.5f);
               model1.render(stack,ModelTransformationMode.FIXED,matrixStack,vertexConsumerProvider,i,OverlayTexture.DEFAULT_UV);
           }
       }
       else
       {
           matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(itemEntity.getBodyYaw()));
           matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-75f));
           MinecraftClient.getInstance().getItemRenderer().renderItem(stack,ModelTransformationMode.FIXED,i,OverlayTexture.DEFAULT_UV,matrixStack,vertexConsumerProvider,itemEntity.getWorld(),0);

       }
            matrixStack.pop();
        super.render(itemEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
    @Override
    public Identifier getTexture(BackpackEntity entity) {
        return  SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
