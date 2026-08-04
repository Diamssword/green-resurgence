package com.diamssword.greenresurgence.render.blockEntityRenderer;

import com.diamssword.greenresurgence.blockEntities.DeployableMachineBlockEntity;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class DeployableBlockEntityRenderer implements BlockEntityRenderer<DeployableMachineBlockEntity> {

	private final BlockRenderManager blockRenderManager;

	public DeployableBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {

		this.blockRenderManager = ctx.getRenderManager();
	}

	@Override
	public void render(DeployableMachineBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {


		BlockState blockState = blockEntity.getDisplayBlock();
		if(blockState == null || blockState.getRenderType() != BlockRenderType.MODEL) {
			return;
		}
		World world = blockEntity.getWorld();
		Random rand = world != null ? world.random : Random.create();
		matrices.push();
		this.blockRenderManager.getModelRenderer().render(world, this.blockRenderManager.getModel(blockState), blockState, blockEntity.getPos(), matrices, vertexConsumers.getBuffer(RenderLayers.getBlockLayer(blockState)), false, rand, blockState.getRenderingSeed(blockEntity.getPos()), OverlayTexture.DEFAULT_UV);
		var ls = blockEntity.getMachine().getCurrentAnimated();
		if(ls != null) {
			ls.forEach(a -> {
				if(a.animation().tickLength() > 0) {
					matrices.push();
					matrices.scale(0.999f, 0.999f, 0.999f);
					var from = blockEntity.getMachine().rotatedCoordinate(a.animation().from());
					var to = blockEntity.getMachine().rotatedCoordinate(a.animation().to());
					if(blockEntity.getMachine().isDeconstructing()) {
						var f1 = from;
						from = to;
						to = f1;
					}
					float progress = Math.min(1, Math.max(0, ((float) blockEntity.getMachine().getCurrentTick() + tickDelta) / (float) a.animation().tickLength()));
					double x = MathHelper.lerp(progress, (float) from.getX(), (float) to.getX());
					double y = MathHelper.lerp(progress, (float) from.getY(), (float) to.getY());
					double z = MathHelper.lerp(progress, (float) from.getZ(), (float) to.getZ());
					matrices.translate(x, y, z);
					this.blockRenderManager.getModelRenderer().render(world, this.blockRenderManager.getModel(a.blockState()), a.blockState(), a.animation().to(), matrices, vertexConsumers.getBuffer(RenderLayers.getBlockLayer(a.blockState())), false, rand, a.blockState().getRenderingSeed(blockEntity.getPos()), OverlayTexture.DEFAULT_UV);
					matrices.pop();
				}
			});
		}
		matrices.pop();
	}
}