package com.diamssword.greenresurgence.blockEntityRenderer;

import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.client.render.entity.FallingBlockEntityRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;

import java.util.SortedSet;

@Environment(EnvType.CLIENT)
public class LootedBlockEntityRenderer implements BlockEntityRenderer<LootedBlockEntity> {

    private final BlockRenderManager blockRenderManager;
 
    public LootedBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {

        this.blockRenderManager = ctx.getRenderManager();
    }
 
    @Override
    public void render(LootedBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {


        BlockState blockState = blockEntity.getRealBlock();
        if (blockState.getRenderType() != BlockRenderType.MODEL || blockEntity.durability ==0) {
            return;
        }
        World world = blockEntity.getWorld();
        if (blockState.getRenderType() == BlockRenderType.INVISIBLE) {
            return;
        }
        matrices.push();
        this.blockRenderManager.getModelRenderer().render((BlockRenderView)world, this.blockRenderManager.getModel(blockState), blockState, blockEntity.getPos(), matrices, vertexConsumers.getBuffer(RenderLayers.getMovingBlockLayer(blockState)), false, Random.create(), blockState.getRenderingSeed(blockEntity.getPos()), OverlayTexture.DEFAULT_UV);
       // matrices.push();
        BlockPos blockPos = blockEntity.getPos();
        MatrixStack.Entry entry3 = matrices.peek();
        int tot =(int) (((LootedBlockEntity.MAX-blockEntity.durability)/(float)LootedBlockEntity.MAX)*9);

        OverlayVertexConsumer vertexConsumer2 = new OverlayVertexConsumer(MinecraftClient.getInstance().getBufferBuilders().getEffectVertexConsumers().getBuffer(ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.get(Math.max(0,Math.min(tot,9)))), entry3.getPositionMatrix(), entry3.getNormalMatrix(), 1.0f);
        this.blockRenderManager.renderDamage(blockEntity.getRealBlock(), blockPos, (BlockRenderView)blockEntity.getWorld(), matrices, vertexConsumer2);
       // matrices.pop();

        matrices.pop();
    }
}