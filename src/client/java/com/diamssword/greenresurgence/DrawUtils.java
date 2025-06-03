package com.diamssword.greenresurgence;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public class DrawUtils {
	public static void drawTexture(DrawContext ctx,
								   Identifier texture, int x, int y, int width, int height, float u, float v, float regionWidth, float regionHeight, int textureWidth, int textureHeight
	) {
		drawTexturedQuad(ctx, texture, x, x + width, y, y + height, 0, (u + 0.0F) / (float) textureWidth,
				(u + regionWidth) / (float) textureWidth,
				(v + 0.0F) / (float) textureHeight,
				(v + regionHeight) / (float) textureHeight);
	}

	static void drawTexturedQuad(DrawContext ctx, Identifier texture, int x1, int x2, int y1, int y2, int z, float u1, float u2, float v1, float v2) {
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		Matrix4f matrix4f = ctx.getMatrices().peek().getPositionMatrix();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(matrix4f, (float) x1, (float) y1, (float) z).texture(u1, v1).next();
		bufferBuilder.vertex(matrix4f, (float) x1, (float) y2, (float) z).texture(u1, v2).next();
		bufferBuilder.vertex(matrix4f, (float) x2, (float) y2, (float) z).texture(u2, v2).next();
		bufferBuilder.vertex(matrix4f, (float) x2, (float) y1, (float) z).texture(u2, v1).next();
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
	}
}
