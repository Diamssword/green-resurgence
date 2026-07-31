package com.diamssword.greenresurgence.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BlueprintRenderer {


	public static void renderBlocks(WorldRenderContext context, BlockPos anchor, Map<BlockPos, BlockState> blocks) {
		MinecraftClient client = MinecraftClient.getInstance();
		Camera camera = context.camera();
		var prev = new PreviewBlockRenderView(context.world(), anchor, blocks);
		MatrixStack matrices = context.matrixStack();
		matrices.push();

		Vec3d camPos = camera.getPos().negate();
		matrices.translate(camPos.getX(), camPos.getY(), camPos.getZ());

		VertexConsumerProvider.Immediate consumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

		Random random = client.world != null ? client.world.random : Random.create();

		BlockRenderManager blockRenderer = client.getBlockRenderManager();

		BlockModelRenderer modelRenderer = blockRenderer.getModelRenderer();
		VertexConsumer vb = new ColoringVertexConsumer(consumers.getBuffer(RenderLayer.getTranslucent()), 1f, 1f, 1f, 0.6f);
		for(Map.Entry<BlockPos, BlockState> entry : blocks.entrySet()) {
			matrices.push();

			BlockPos pos = entry.getKey().add(anchor);
			matrices.translate(pos.getX(), pos.getY(), pos.getZ());
			matrices.scale(1.001f, 1.001f, 1.001f);
			BlockState state = entry.getValue();

			if(state.isAir())
				continue;

			BakedModel model = blockRenderer.getModel(state);

			modelRenderer.render(
					prev,
					model,
					state,
					pos,
					matrices,
					vb,
					true,
					random,
					state.getRenderingSeed(pos),
					OverlayTexture.DEFAULT_UV
			);
			matrices.pop();
		}

		consumers.draw();
		matrices.pop();
	}

	public record ColoringVertexConsumer(VertexConsumer delegate, float red, float green, float blue, float alpha) implements VertexConsumer {
		@Override
		public VertexConsumer vertex(double x, double y, double z) {
			delegate.vertex(x, y, z);
			return this;
		}

		@Override
		public VertexConsumer color(int r, int g, int b, int a) {
			delegate.color((int) (r * red), (int) (g * green), (int) (b * blue), (int) (a * alpha));
			return this;
		}

		@Override
		public VertexConsumer texture(float u, float v) {
			delegate.texture(u, v);
			return this;
		}

		@Override
		public VertexConsumer overlay(int u, int v) {
			delegate.overlay(u, v);
			return this;
		}

		@Override
		public VertexConsumer light(int u, int v) {
			delegate.light(u, v);
			return this;
		}

		@Override
		public VertexConsumer normal(float x, float y, float z) {
			delegate.normal(x, y, z);
			return this;
		}

		@Override
		public void next() {
			delegate.next();
		}

		@Override
		public void fixedColor(int r, int g, int b, int a) {
			delegate.fixedColor((int) (r * red), (int) (g * green), (int) (b * blue), (int) (a * alpha));
		}

		@Override
		public void unfixColor() {
			delegate.unfixColor();
		}
	}

	public static class PreviewBlockRenderView implements BlockRenderView {
		private final BlockRenderView world;
		private final BlockPos anchor;
		private final Map<BlockPos, BlockState> preview;

		public PreviewBlockRenderView(BlockRenderView world, BlockPos anchor, Map<BlockPos, BlockState> preview) {
			this.world = world;
			this.anchor = anchor;
			this.preview = preview;
		}

		@Override
		public float getBrightness(Direction direction, boolean shaded) {
			return world.getBrightness(direction, shaded);
		}

		@Override
		public LightingProvider getLightingProvider() {
			return world.getLightingProvider();
		}

		@Override
		public int getColor(BlockPos pos, ColorResolver colorResolver) {
			return world.getColor(pos, colorResolver);
		}

		@Override
		public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
			return null;
		}

		@Override
		public BlockState getBlockState(BlockPos pos) {
			return preview.getOrDefault(pos.subtract(anchor), Blocks.AIR.getDefaultState());
		}

		@Override
		public FluidState getFluidState(BlockPos pos) {
			return Fluids.EMPTY.getDefaultState();
		}

		@Override
		public int getHeight() {
			return world.getHeight();
		}

		@Override
		public int getBottomY() {
			return world.getBottomY();
		}
	}
}
