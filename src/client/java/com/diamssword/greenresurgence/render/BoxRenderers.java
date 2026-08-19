package com.diamssword.greenresurgence.render;

import com.diamssword.greenresurgence.gui.faction.ClaimAntennaGui;
import com.diamssword.greenresurgence.items.helpers.IStructureProvider;
import com.diamssword.greenresurgence.network.CurrentZonePacket;
import com.diamssword.greenresurgence.render.environment.EnvironementAreas;
import com.diamssword.greenresurgence.structure.StructureInfos;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

public class BoxRenderers {
	public static void drawAdventureOutline(BlockPos pos, net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext ctx) {
		drawAdventureOutline(pos, ctx, 1, 1, 1);
	}

	public static void drawAdventureOutline(BlockPos pos, net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext ctx, float r, float g, float b) {

		BlockState st = ctx.world().getBlockState(pos);
		MatrixStack matrix = ctx.matrixStack();
		matrix.push();
		VoxelShape shape = st.getOutlineShape(ctx.world(), pos, ShapeContext.of(ctx.gameRenderer().getClient().player));
		Vec3d camPos = ctx.camera().getPos();
		matrix.translate(-camPos.x, -camPos.y, -camPos.z);
		if(shape != null && !shape.isEmpty()) {
			shape = shape.offset(pos.getX(), pos.getY(), pos.getZ());
			Box box = shape.getBoundingBox().expand(0.005);
			long ticks = MinecraftClient.getInstance().world.getTime();
			float tot = (float) (Math.sin(2 * Math.PI * ticks / 40) * (0.7f - -0f) / 2 + (0.7f + -0f) / 2); // Math.min(0.5f,(ticks % 20) / 20f);
			WorldRenderer.drawBox(matrix, ctx.consumers().getBuffer(RenderLayer.LINES), box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, r, g, b, 0.3f + tot);

		}
		matrix.pop();
	}

	public static void drawStructureBox(WorldRenderContext ctx, VertexConsumerProvider.Immediate consumers, Vec3d pos, Vec3d size, float r, float g, float b, float a) {

		var matrix = ctx.matrixStack();
		matrix.push();
		var cam = ctx.camera().getPos();
		matrix.translate(pos.x - cam.x, pos.y - cam.y, pos.z - cam.z);

		WorldRenderer.drawBox(matrix, consumers.getBuffer(RenderLayer.LINES), 0, 0, 0, size.x, size.y, size.z, r, g, b, a);
		matrix.pop();
	}

	public static void drawStructureItemOverlay(WorldRenderContext ctx, VertexConsumerProvider.Immediate consumers) {
		MinecraftClient mc = MinecraftClient.getInstance();
		ItemStack st = mc.player.getMainHandStack();
		if(st != null && st.getItem() instanceof IStructureProvider provider) {
			drawStructureOverlay(ctx, consumers, provider, st);
		}
		ItemStack st1 = mc.player.getStackInHand(Hand.OFF_HAND);
		if(st1 != null && st1.getItem() instanceof IStructureProvider provider) {
			drawStructureOverlay(ctx, consumers, provider, st1);
		}
	}

	public static void drawStructureOverlay(WorldRenderContext ctx, VertexConsumerProvider.Immediate consumers, IStructureProvider provider, ItemStack st) {
		MinecraftClient mc = MinecraftClient.getInstance();
		Direction d = provider.getDirection(st, mc.world);
		Identifier name = provider.getStructureName(st, mc.world);
		BlockPos pos = provider.getPosition(st, mc.world);
		IStructureProvider.StructureType jigsaw = provider.strutctureType(st, mc.world);
		if(pos != null && name != null && d != null) {

			StructureInfos.StructureInfo inf = StructureInfos.getInfos(name, d, jigsaw);
			BlockPos pos1 = pos;
			int i, j, k;
			if(!inf.blocks().isEmpty())
				BlueprintRenderer.renderBlocks(ctx, pos, inf.blocks());
			if(jigsaw == IStructureProvider.StructureType.jigsaw) {
				i = j = k = 1;
				switch(d) {
					case NORTH -> {
						pos1 = pos.add(-inf.offset().getX() - (inf.size().getX()), -inf.offset().getY(), -inf.offset().getZ() - (inf.size().getZ()));
					}
					case SOUTH -> {
						pos1 = pos.add(-inf.offset().getX(), -inf.offset().getY(), -inf.offset().getZ());
					}
					case WEST -> {
						pos1 = pos.add(-inf.offset().getX() - inf.size().getX(), -inf.offset().getY(), -inf.offset().getZ());
					}
					case EAST -> {
						pos1 = pos.add(-inf.offset().getX(), -inf.offset().getY(), -inf.offset().getZ() - inf.size().getZ());
					}
				}
			} else {
				i = j = k = 0;
				switch(d) {
					case NORTH -> {
						pos1 = pos.add(-inf.offset().getX(), inf.offset().getY(), -inf.offset().getZ());
					}
					case SOUTH -> {
						pos1 = pos.add(inf.offset().getX(), inf.offset().getY(), inf.offset().getZ());
					}
					case WEST -> {
						pos1 = pos.add(-inf.offset().getX(), inf.offset().getY(), inf.offset().getZ());
					}
					case EAST -> {
						pos1 = pos.add(inf.offset().getX(), inf.offset().getY(), -inf.offset().getZ());
					}
				}
			}

			drawStructureBox(ctx, consumers, new Vec3d(pos.getX(), pos.getY(), pos.getZ()), new Vec3d(1, 1, 1), 0.5f, 1f, 1, 1);
			drawStructureBox(ctx, consumers, new Vec3d(pos1.getX(), pos1.getY(), pos1.getZ()), new Vec3d(inf.size().getX() + i, inf.size().getY() + j, inf.size().getZ() + k), 1, 0.5f, 1, 1);
		}
	}

	public static void drawBaseOverlays(WorldRenderContext ctx, VertexConsumerProvider.Immediate consumers) {
		MinecraftClient mc = MinecraftClient.getInstance();

		if(mc.world != null) {
			if(mc.getEntityRenderDispatcher().shouldRenderHitboxes()) {
				CurrentZonePacket.DebugViews.forEach(b -> {
					VertexConsumerProvider.Immediate store = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
					VertexConsumerProvider.Immediate store1 = MinecraftClient.getInstance().getBufferBuilders().getEffectVertexConsumers();

					int hash = b.getLeft().hashCode();
					int c1 = (hash) & 0xFF;
					int c2 = (hash >> 8) & 0xFF;
					int c3 = (hash >> 16) & 0xFF;
					float red = Math.min(Math.max(c1 % 255f, 0f), 255f);
					float green = Math.min(Math.max(c2 % 255f, 0f), 255f);
					float blue = Math.min(Math.max(c3 % 255f, 0f), 255f);
					DebugRenderer.drawBox(ctx.matrixStack(), store, new BlockPos(b.getRight().getMinX(), b.getRight().getMinY(), b.getRight().getMinZ()), new BlockPos(b.getRight().getMaxX() + 1, b.getRight().getMaxY() + 1, b.getRight().getMaxZ() + 1), red, green, blue, 0.2f);
					BlockPos p1 = b.getRight().getCenter();
					DebugRenderer.drawString(ctx.matrixStack(), store1, "Camp: " + b.getLeft(), p1.getX(), p1.getY(), p1.getZ(), 0xffffff, 0.1f, true, 0, true);
					DebugRenderer.drawString(ctx.matrixStack(), store1, b.getMiddle().toString(), p1.getX(), p1.getY() - 1, p1.getZ(), 0xffffff, 0.1f, true, 0, true);
					drawStructureBox(ctx, consumers, new Vec3d(b.getRight().getMinX(), b.getRight().getMinY(), b.getRight().getMinZ()), Vec3d.of(b.getRight().getDimensions().add(1, 1, 1)), red, green, blue, 1);
				});
			} else if(ClaimAntennaGui.viewBounds && mc.player != null) {
				CurrentZonePacket.OwnFactionBounds.forEach(b -> {
					if(b.getCenter().isWithinDistance(mc.cameraEntity.getPos(), 128)) {
						drawStructureBox(ctx, consumers, new Vec3d(b.getMinX(), Math.min(b.getMaxY(), Math.max(b.getMinY(), Math.ceil(mc.player.getY()))), b.getMinZ()), new Vec3d(b.getDimensions().getX() + 1, 0, b.getDimensions().getY() + 1), 1f, 0.8f, 0.8f, 0.8f);
						drawStructureBox(ctx, consumers, new Vec3d(b.getMinX(), b.getMinY(), b.getMinZ()), Vec3d.of(b.getDimensions().add(1, 1, 1)), 1f, 1f, 1f, 1);
					}
				});
			}
		}
	}

	public static void drawEnvironmentOverlays(WorldRenderContext ctx, VertexConsumerProvider.Immediate consumers) {
		MinecraftClient mc = MinecraftClient.getInstance();

		if(mc.world != null && mc.getEntityRenderDispatcher().shouldRenderHitboxes()) {
			EnvironementAreas.fogAreas.forEach(b -> {
				VertexConsumerProvider.Immediate store = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
				VertexConsumerProvider.Immediate store1 = MinecraftClient.getInstance().getBufferBuilders().getEffectVertexConsumers();

				int hash = b.hashCode();
				int c1 = (hash) & 0xFF;
				int c2 = (hash >> 8) & 0xFF;
				int c3 = (hash >> 16) & 0xFF;
				float red = Math.min(Math.max(c1 % 255f, 0f), 255f);
				float green = Math.min(Math.max(c2 % 255f, 0f), 255f);
				float blue = Math.min(Math.max(c3 % 255f, 0f), 255f);
				Vec3d p1 = b.getBox().getCenter();
				DebugRenderer.drawString(ctx.matrixStack(), store1, "EnvArea:  " + b.getType(), p1.getX(), p1.getY(), p1.getZ(), 0xffffff, 0.1f, true, 0, true);
				drawStructureBox(ctx, consumers, new Vec3d(b.getBox().minX, b.getBox().minY, b.getBox().minZ), new Vec3d(b.getBox().getXLength(), b.getBox().getYLength(), b.getBox().getZLength()).add(1, 1, 1), red, green, blue, 1);
			});
		}


	}
}
