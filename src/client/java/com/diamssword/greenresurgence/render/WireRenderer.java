package com.diamssword.greenresurgence.render;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blockEntities.ConnectorBlockEntity;
import com.diamssword.greenresurgence.blocks.IDisplayOffset;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.joml.Math;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class WireRenderer {
	private static final Vec3d UP = new Vec3d(0, 1, 0);
	private static final Vec3d RIGHT = new Vec3d(1, 0, 0);

	private static final float CABLE_SIZE = 0.05f;
	private static final float SEGMENTS = 24;
	private static final float baseSag = 1.2f;
	private static final float distanceSag = 0.01f;

	public static void render(net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext ctx) {
		PlayerEntity p = ctx.gameRenderer().getClient().player;
		List<Pair<BlockPos, BlockPos>> toRemove = new ArrayList<>();
		ConnectorBlockEntity.clientCables.forEach((k) -> {
			if(!k.getRight().isWithinDistance(p.getPos(), 1000) && !k.getLeft().isWithinDistance(p.getPos(), 256)) {
				toRemove.add(k);
			} else {
				Vec3d off1 = Vec3d.ZERO;
				Vec3d off2 = Vec3d.ZERO;
				float scale = 1f;
				BlockState st1 = ctx.world().getBlockState(k.getLeft());
				BlockState st2 = ctx.world().getBlockState(k.getRight());
				if(st1.getBlock() == Blocks.AIR && st2.getBlock() == Blocks.AIR)
					toRemove.add(k);
				if(st1.getBlock() instanceof IDisplayOffset) {
					off1 = ((IDisplayOffset) st1.getBlock()).getOffset(st1, ctx.world());
					scale = ((IDisplayOffset) st1.getBlock()).getScale(st1, ctx.world());
				}
				if(st2.getBlock() instanceof IDisplayOffset) {
					off2 = ((IDisplayOffset) st2.getBlock()).getOffset(st2, ctx.world());
				}
				if(k.getLeft().getZ() == k.getRight().getZ() && k.getLeft().getX() == k.getRight().getX())
					off1 = off1.add(0.01, 0, 0);
				WireRenderer.renderWireInWorld(ctx.world(), ctx.camera().getPos(), k.getLeft().toCenterPos().add(off1), k.getRight().toCenterPos().add(off2), ctx.matrixStack(), ctx.consumers());
				//renderLeashFrom(ctx, k.getLeft().toCenterPos().add(off1), k.getRight().toCenterPos().add(off2), scale);
			}
		});
		toRemove.forEach(ConnectorBlockEntity.clientCables::remove);
	}

	public static void renderWire(World level, VertexConsumerProvider source, MatrixStack poseStack, Vec3d from, Vec3d to) {
		VertexConsumer consumer = source.getBuffer(RenderLayer.getEntitySolid(GreenResurgence.asRessource("textures/entity/wire.png")));

		Matrix4f matrix = poseStack.peek().getPositionMatrix();

		float distance = (float) (to.subtract(from)).length();

		Vec3d middle = from.lerp(to, 0.5).subtract(0, baseSag + (distance * distanceSag), 0);

		Vec3d lastTopLeft = null;
		Vec3d lastBottomLeft = null;
		Vec3d lastTopRight = null;
		Vec3d lastBottomRight = null;
		int fromSkyLight = level.getLightLevel(LightType.SKY, BlockPos.ofFloored(from.x, from.y, from.z));
		int toSkyLight = level.getLightLevel(LightType.SKY, BlockPos.ofFloored(to.x, to.y, to.z));

		int fromBlockLight = level.getLightLevel(LightType.BLOCK, BlockPos.ofFloored(from.x, from.y, from.z));
		int toBlockLight = level.getLightLevel(LightType.BLOCK, BlockPos.ofFloored(to.x, to.y, to.z));

		float tile = 0f;//distance*(1f/24f)*CABLE_SIZE;
		float vOffset = ((distance / CABLE_SIZE) / SEGMENTS) * 0.065f;

		for(int i = 0; i <= SEGMENTS; i++) {
			float progress = i / SEGMENTS;

			float effector = 0;
		/*	if(wire != null && SimpleRadioLibrary.CLIENT_CONFIG.wire.effect) {
				double effectDuration = distance * SimpleRadioLibrary.CLIENT_CONFIG.wire.effectTime;
				for(Wire.Effect effect : wire.effectList) {
					float effectProgress = (float) ((effect.progress + (effect.direction * partialTick)) / effectDuration);

					float effectDistance = Math.abs(effectProgress - progress);
					if(effectDistance <= 0.1f) {
						effector += (0.1f - effectDistance) * 10f;
					}
				}
			}
*/
			Vec3d fromMiddle = from.lerp(middle, progress);
			Vec3d middleTo = middle.lerp(to, progress);

			Vec3d segmentPosition = fromMiddle.lerp(middleTo, progress);

			Vec3d direction = fromMiddle.lerp(middleTo, progress + 0.05f).subtract(segmentPosition).normalize();

			Vec3d side = direction.crossProduct((direction.y == -1 || direction.y == 1) ? RIGHT : UP).normalize().multiply(CABLE_SIZE / 2);
			Vec3d up = side.crossProduct(direction).normalize().multiply(CABLE_SIZE / 2);

			Vec3d otherSide = side.negate();
			Vec3d down = up.negate();

			Vec3d topLeft = segmentPosition.add(otherSide).add(up);
			Vec3d bottomLeft = segmentPosition.add(otherSide).add(down);
			Vec3d topRight = segmentPosition.add(side).add(up);
			Vec3d bottomRight = segmentPosition.add(side).add(down);

			if(lastTopLeft != null) {
				int skyLight = (int) MathHelper.lerp(progress, (float) fromSkyLight, (float) toSkyLight);
				int blockLight = (int) MathHelper.lerp(
						Math.clamp(0, 1, effector),
						MathHelper.lerp(progress, (float) fromBlockLight, (float) toBlockLight),
						15f
				);

				float newTile = vOffset + ((vOffset * SEGMENTS) * progress);

				int overlay = Math.clamp(0, 10, Math.round(effector * 5));
				int packedLight = LightmapTextureManager.pack(blockLight, skyLight);

				buildQuad(consumer, matrix, overlay, packedLight, up.normalize(), 0.0625f, vOffset, newTile, lastTopRight, topRight, topLeft, lastTopLeft);
				buildQuad(consumer, matrix, overlay, packedLight, side.normalize(), 2f, vOffset, newTile, lastBottomRight, bottomRight, topRight, lastTopRight);
				buildQuad(consumer, matrix, overlay, packedLight, down.normalize(), 1.1875f, vOffset, newTile, lastBottomLeft, bottomLeft, bottomRight, lastBottomRight);
				buildQuad(consumer, matrix, overlay, packedLight, otherSide.normalize(), 3.25f, vOffset, newTile, lastTopLeft, topLeft, bottomLeft, lastBottomLeft);
			}

			lastTopLeft = topLeft;
			lastBottomLeft = bottomLeft;
			lastTopRight = topRight;
			lastBottomRight = bottomRight;
		}
	}

	public static void renderWireInWorld(World level, Vec3d offset, Vec3d from, Vec3d to, MatrixStack poseStack, VertexConsumerProvider source) {
		//Vec3d offset = wire.getPosition(partialTick);

		poseStack.push();
		poseStack.translate(-offset.x, -offset.y, -offset.z);

		renderWire(level, source, poseStack, from, to);

		poseStack.pop();
	}

	/*
		public static void renderPlayer(AbstractClientPlayer player, MultiBufferSource source, PoseStack poseStack, float partialTick, @Nullable Camera camera) {
			ItemStack wire = RadioManager.getInstance().isEntityHolding(player, stack -> stack.is(SimpleRadioItems.COPPER_WIRE));
			if(wire != null) {
				CompoundTag tag = wire.getOrCreateTag();
				if(tag.contains("connectTo")) {
					Router router = ClientRadioManager.getInstance().getRouter(tag.getUUID("connectTo"));
					if(router == null) return;

					ClientLevel level = player.clientLevel;

					Vec3d holdPosition = player.getRopeHoldPosition(partialTick);
					Vec3d connectionPosition = router.getConnectionPosition();

					Vec3d offset = player.getPosition(partialTick);

					poseStack.pushPose();
					if(camera != null) {
						Vec3d cameraPos = camera.getPosition();
						poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

						renderWire(level, source, poseStack, holdPosition, connectionPosition, null, partialTick);
					} else {
						poseStack.translate(-offset.x, -offset.y, -offset.z);

						renderWire(level, source, poseStack, holdPosition, connectionPosition, null, partialTick);
					}
					poseStack.popPose();
				}
			}
		}
	*/
	public static void buildQuad(VertexConsumer consumer, Matrix4f matrix, int overlay, int packedLight, Vec3d normal, float index, float offset, float tile, Vec3d one, Vec3d two, Vec3d three, Vec3d four) {
		consumer.vertex(matrix, (float) one.x, (float) one.y, (float) one.z).color(1f, 1f, 1f, 1f).texture(index, tile)
				.overlay(OverlayTexture.packUv(overlay, 15)).light(packedLight).normal((float) normal.x, (float) normal.y, (float) normal.z).next();
		consumer.vertex(matrix, (float) two.x, (float) two.y, (float) two.z).color(1f, 1f, 1f, 1f).texture(index, offset + tile)
				.overlay(OverlayTexture.packUv(overlay, 15)).light(packedLight).normal((float) normal.x, (float) normal.y, (float) normal.z).next();
		consumer.vertex(matrix, (float) three.x, (float) three.y, (float) three.z).color(1f, 1f, 1f, 1f).texture(index, offset + tile)
				.overlay(OverlayTexture.packUv(overlay, 15)).light(packedLight).normal((float) normal.x, (float) normal.y, (float) normal.z).next();
		consumer.vertex(matrix, (float) four.x, (float) four.y, (float) four.z).color(1f, 1f, 1f, 1f).texture(index, tile)
				.overlay(OverlayTexture.packUv(overlay, 15)).light(packedLight).normal((float) normal.x, (float) normal.y, (float) normal.z).next();
	}
}