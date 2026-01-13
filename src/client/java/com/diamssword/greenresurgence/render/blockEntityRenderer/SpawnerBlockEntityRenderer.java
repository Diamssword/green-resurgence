package com.diamssword.greenresurgence.render.blockEntityRenderer;

import com.diamssword.greenresurgence.blockEntities.SpawnerBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class SpawnerBlockEntityRenderer implements BlockEntityRenderer<SpawnerBlockEntity> {

	private final BlockRenderManager blockRenderManager;

	public SpawnerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {

		this.blockRenderManager = ctx.getRenderManager();
	}

	@Override
	public void render(SpawnerBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {


		BlockState blockState = blockEntity.getCamo();
		if(blockState == null || blockState.getRenderType() != BlockRenderType.MODEL) {
			return;
		}
		World world = blockEntity.getWorld();
		matrices.push();
		this.blockRenderManager.getModelRenderer().render(world, this.blockRenderManager.getModel(blockState), blockState, blockEntity.getPos(), matrices, vertexConsumers.getBuffer(RenderLayers.getMovingBlockLayer(blockState)), false, Random.create(), blockState.getRenderingSeed(blockEntity.getPos()), OverlayTexture.DEFAULT_UV);
		matrices.pop();
	}
}