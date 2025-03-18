package com.diamssword.greenresurgence.render.blockEntityRenderer;

import com.diamssword.greenresurgence.blockEntities.ItemBlockEntity;
import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.ItemFrameEntityRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class ItemBlockEntityRenderer implements BlockEntityRenderer<ItemBlockEntity> {

    private final BlockRenderManager blockRenderManager;

    public ItemBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {

        this.blockRenderManager = ctx.getRenderManager();
    }
 
    @Override
    public void render(ItemBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {


        ItemStack st=blockEntity.getItem();

        World world = blockEntity.getWorld();

        matrices.push();
        matrices.translate(0.5,0.5,0.5);
        matrices.translate(blockEntity.getPosition().x/20,blockEntity.getPosition().y/20,blockEntity.getPosition().z/20);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)blockEntity.getRotation().y));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float)blockEntity.getRotation().x));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)blockEntity.getRotation().z));
        matrices.scale((float) (0.2*blockEntity.getSize()), (float) (0.2*blockEntity.getSize()), (float) (0.2*blockEntity.getSize()));
        int seed=blockEntity.getPos().getX()+blockEntity.getPos().getY()+blockEntity.getPos().getZ();


        MinecraftClient.getInstance().getItemRenderer().renderItem(st, ModelTransformationMode.FIXED,blockEntity.isLightOffset()? getLight(blockEntity):light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, world,seed);

        matrices.pop();
    }

    public final int getLight(ItemBlockEntity te) {
        BlockPos blockPos = BlockPos.ofFloored(te.getPosition().multiply(0.05).add(te.getPos().getX(),te.getPos().getY(),te.getPos().getZ()));
        return LightmapTextureManager.pack(te.getWorld().getLightLevel(LightType.BLOCK, blockPos), te.getWorld().getLightLevel(LightType.SKY, blockPos));
    }


}