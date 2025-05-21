package com.diamssword.greenresurgence.render.blockEntityRenderer;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blockEntities.ImageBlockEntity;
import com.diamssword.greenresurgence.blocks.ImageBlock;
import com.diamssword.greenresurgence.render.images.TextureCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class ImageBlockEntityRenderer implements BlockEntityRenderer<ImageBlockEntity> {
	private static final Identifier EMPTY_IMAGE = GreenResurgence.asRessource("textures/block/empty_image.png");
	private static final float THICKNESS = 1F / 16F;
	private final BlockRenderManager blockRenderManager;

	public ImageBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {

		this.blockRenderManager = ctx.getRenderManager();
	}

	@Override
	public void render(ImageBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

		Identifier id = TextureCache.instance().getImage(blockEntity.getContent());

		renderImage(id, blockEntity, blockEntity.getCachedState().get(ImageBlock.FACING), matrices, vertexConsumers, light);

	}

	public static void renderImage(Identifier image, ImageBlockEntity te, Direction facing, MatrixStack matrixStack, VertexConsumerProvider buffer1, int light) {
		matrixStack.push();
		var width = te.getSize().x;
		var height = te.getSize().y;
		float imageRatio = 1F;
		Identifier resourceLocation = EMPTY_IMAGE;
		if (image != null) {
			resourceLocation = image;
			var size = TextureCache.instance().getSize(image);
			if (size != null)
				imageRatio = (float) size.getLeft() / (float) size.getRight();
		}

		//   matrixStack.translate(-0.5D, 0D, -0.5D);
		if (!te.isStretch())
			height = width;
		rotate(facing, matrixStack);

		float frameRatio = width / height;

		float ratio = imageRatio / frameRatio;

		float ratioX;
		float ratioY;

		if (te.isStretch()) {
			ratioX = 0F;
			ratioY = 0F;
		} else {
			if (ratio >= 1F) {
				ratioY = (1F - 1F / ratio) / 2F;
				ratioX = 0F;
			} else {
				ratioX = (1F - ratio) / 2F;
				ratioY = 0F;
			}

			ratioX *= width;
			ratioY *= height;
		}
		matrixStack.translate(te.isOffsetX() ? 0 : 0.5, te.isOffsetY() ? 0 : 0.5, -0.05);
		matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(te.getRotation()));
		matrixStack.translate(-width / 2f, -height / 2f, 0D);
		VertexConsumer builderFront = buffer1.getBuffer(RenderLayer.getEntityCutout(resourceLocation));

		// Front
		vertex(builderFront, matrixStack, 0F + ratioX, ratioY, THICKNESS, 0F, 1F, light);
		vertex(builderFront, matrixStack, width - ratioX, ratioY, THICKNESS, 1F, 1F, light);
		vertex(builderFront, matrixStack, width - ratioX, height - ratioY, THICKNESS, 1F, 0F, light);
		vertex(builderFront, matrixStack, ratioX, height - ratioY, THICKNESS, 0F, 0F, light);
		matrixStack.pop();
	}

	public static void rotate(Direction facing, MatrixStack matrixStack) {
		switch (facing) {
			case NORTH:
				matrixStack.translate(1D, 0D, 1D);
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180F));
				break;
			case SOUTH:
				break;
			case EAST:
				matrixStack.translate(0D, 0D, 1D);
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90F));
				break;
			case WEST:
				matrixStack.translate(1D, 0D, 0D);
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270F));
				break;
			case UP:

				matrixStack.translate(0D, 0, 1D);
				matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90F));
				break;
			case DOWN:
				matrixStack.translate(0D, 1, 0D);
				matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90F));
				break;
		}
	}

	private static void vertex(VertexConsumer builder, MatrixStack matrixStack, float x, float y, float z, float u, float v, int light) {
		var entry = matrixStack.peek();
		Matrix4f matrix4f = entry.getPositionMatrix();
		builder.vertex(matrix4f, x, y, z)
				.color(255, 255, 255, 255)
				.texture(u, v)
				.overlay(OverlayTexture.DEFAULT_UV)
				.light(light)
				.normal(entry.getNormalMatrix(), 0F, 0F, -1F)
				.next();

	}


}