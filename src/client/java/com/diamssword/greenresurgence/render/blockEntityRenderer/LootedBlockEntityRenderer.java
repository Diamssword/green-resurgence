package com.diamssword.greenresurgence.render.blockEntityRenderer;

import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.OverlayVertexConsumer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class LootedBlockEntityRenderer implements BlockEntityRenderer<LootedBlockEntity> {

	private final BlockRenderManager blockRenderManager;

	public LootedBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {

		this.blockRenderManager = ctx.getRenderManager();
	}

	@Override
	public void render(LootedBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {


		BlockState blockState = blockEntity.getDisplayBlock();
		if (blockState == null || blockState.getRenderType() != BlockRenderType.MODEL) {
			return;
		}
		World world = blockEntity.getWorld();

		matrices.push();
		this.blockRenderManager.getModelRenderer().render(world, this.blockRenderManager.getModel(blockState), blockState, blockEntity.getPos(), matrices, vertexConsumers.getBuffer(RenderLayers.getMovingBlockLayer(blockState)), false, Random.create(), blockState.getRenderingSeed(blockEntity.getPos()), OverlayTexture.DEFAULT_UV);
		if (blockEntity.durability > 0) {
			BlockPos blockPos = blockEntity.getPos();
			MatrixStack.Entry entry3 = matrices.peek();
			int tot = (int) (((LootedBlockEntity.MAX - blockEntity.durability) / (float) LootedBlockEntity.MAX) * 9);
			OverlayVertexConsumer vertexConsumer2 = new OverlayVertexConsumer(MinecraftClient.getInstance().getBufferBuilders().getOutlineVertexConsumers().getBuffer(ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.get(Math.max(0, Math.min(tot, 9)))), entry3.getPositionMatrix(), entry3.getNormalMatrix(), 1f);
			this.blockRenderManager.renderDamage(blockState, blockPos, blockEntity.getWorld(), matrices, vertexConsumer2);
		}
		matrices.pop();
	}
}