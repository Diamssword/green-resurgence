package com.diamssword.greenresurgence;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public class DrawUtils {
	public static void drawTexture(DrawContext ctx,
								   Identifier texture, float x, float y, float width, float height, float u, float v, float regionWidth, float regionHeight, int textureWidth, int textureHeight
	) {
		drawTexturedQuad(ctx, texture, x, x + width, y, y + height, 0, (u + 0.0F) / (float) textureWidth,
				(u + regionWidth) / (float) textureWidth,
				(v + 0.0F) / (float) textureHeight,
				(v + regionHeight) / (float) textureHeight);
	}

	static void drawTexturedQuad(DrawContext ctx, Identifier texture, float x1, float x2, float y1, float y2, int z, float u1, float u2, float v1, float v2) {
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		Matrix4f matrix4f = ctx.getMatrices().peek().getPositionMatrix();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(matrix4f, x1, y1, (float) z).texture(u1, v1).next();
		bufferBuilder.vertex(matrix4f, x1, y2, (float) z).texture(u1, v2).next();
		bufferBuilder.vertex(matrix4f, x2, y2, (float) z).texture(u2, v2).next();
		bufferBuilder.vertex(matrix4f, x2, y1, (float) z).texture(u2, v1).next();
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
	}


	public static void renderHotbarItem(MinecraftClient client, OwoUIDrawContext context, int x, int y, float delta, PlayerEntity player, ItemStack stack, int seed) {
		if (!stack.isEmpty()) {
			float g = (float) stack.getBobbingAnimationTime() - delta;
			if (g > 0.0F) {
				float h = 1.0F + g / 5.0F;
				context.getMatrices().push();
				context.getMatrices().translate((float) (x + 8), (float) (y + 12), 0.0F);
				context.getMatrices().scale(1.0F / h, (h + 1.0F) / 2.0F, 1.0F);
				context.getMatrices().translate((float) (-(x + 8)), (float) (-(y + 12)), 0.0F);
			}

			context.drawItem(player, stack, x, y, seed);
			if (g > 0.0F) {
				context.getMatrices().pop();
			}

			context.drawItemInSlot(client.textRenderer, stack, x, y);
		}
	}
}
