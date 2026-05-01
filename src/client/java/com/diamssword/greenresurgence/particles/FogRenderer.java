package com.diamssword.greenresurgence.particles;

import com.diamssword.greenresurgence.GreenResurgence;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FogRenderer {
	private static final Identifier FOG_TEXTURE = GreenResurgence.asRessource("textures/particle/fog.png");
	private static List<FogArea> areas = Collections.synchronizedList(new ArrayList<>());

	public static void addArea(FogArea area) {
		areas.add(area);
	}

	public static class FogLayer {
		Vector3d position;
		Vector3f velocity;
		Vector3f color;
		float alpha;
		float scale;
		float seed;
		int age;
		int maxAge;

		public FogLayer(Vector3f color, float alpha, Vector3d position, Vector3f velocity, float scale, float seed, int maxAge) {
			this.position = position;
			this.velocity = velocity;
			this.scale = scale;
			this.seed = seed;
			this.age = 0;
			this.maxAge = maxAge;
			this.color = color;
			this.alpha = alpha;
		}

		public FogLayer(Vector4f color, Vector3d position, Vector3f velocity, float scale, float seed, int maxAge) {
			this.position = position;
			this.velocity = velocity;
			this.scale = scale;
			this.seed = seed;
			this.age = 0;
			this.maxAge = maxAge;
			this.color = new Vector3f(color.x, color.y, color.z);
			this.alpha = color.w;
		}
	}

	public static void render(WorldRenderContext context) {
		if(MinecraftClient.getInstance().world != null) {
			render(context, MinecraftClient.getInstance().options.getClampedViewDistance() * 16);
		}
	}

	public static void updateFog(ClientWorld clientWorld) {
		for(FogArea area : areas) {
			area.tick(clientWorld);
		}

	}

	public static void render(WorldRenderContext ctx, double maxDist) {
		Camera camera = ctx.camera();
		MatrixStack matrices = ctx.matrixStack();

		Vec3d camPos = camera.getPos();

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableCull();
		RenderSystem.depthMask(false);
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
		RenderSystem.setShaderTexture(0, FOG_TEXTURE);


		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		for(FogArea area : areas) {
			if(camPos.distanceTo(new Vec3d(area.getArea().minX, area.getArea().minY, area.getArea().minZ)) < maxDist || camPos.distanceTo(new Vec3d(area.getArea().maxX, area.getArea().maxY, area.getArea().maxZ)) < maxDist) {

				for(FogLayer layer : area.getLayers0()) {
					var dist = camPos.distanceTo(new Vec3d(layer.position.x, layer.position.y, layer.position.z));
					matrices.push();
					matrices.translate(
							layer.position.x - camPos.x,
							layer.position.y - camPos.y,
							layer.position.z - camPos.z
					);
					matrices.multiply(camera.getRotation());

					Matrix4f mat = matrices.peek().getPositionMatrix();

					float size = layer.scale;
					float density = layer.alpha;
					if(layer.age < 40) {
						var f = layer.age / 40f;
						density = layer.alpha * f;
					} else if(layer.age > layer.maxAge - 40) {
						var f = (layer.maxAge - layer.age) / 40f;
						density = layer.alpha * f;
					}
					float fade = MathHelper.clamp((float) (dist / (layer.scale * 2)), 0f, 1f);
					density *= fade;
					drawSoftQuad(buffer, mat, size, density, layer.color, 0, 0);
					matrices.pop();
				}
				for(FogLayer layer : area.getLayers1()) {
					matrices.push();
					matrices.translate(
							layer.position.x - camPos.x,
							layer.position.y - camPos.y,
							layer.position.z - camPos.z
					);
					matrices.multiply(camera.getRotation());

					Matrix4f mat = matrices.peek().getPositionMatrix();
					var dist = camPos.distanceTo(new Vec3d(layer.position.x, layer.position.y, layer.position.z));
					float size = layer.scale;
					float density = layer.alpha;
					if(layer.age < 40) {
						var f = layer.age / 40f;
						density = layer.alpha * f;
					} else if(layer.age > layer.maxAge - 40) {
						var f = (layer.maxAge - layer.age) / 40f;
						density = layer.alpha * f;
					}
					float fade = MathHelper.clamp((float) (dist / (layer.scale * 2)), 0f, 1f);
					density *= fade;
					drawSoftQuad(buffer, mat, size, density, layer.color, 0, 0);
					matrices.pop();
				}
			}
		}
		Tessellator.getInstance().draw();
		RenderSystem.depthMask(true);
		RenderSystem.enableCull();
	}

	private static void drawSoftQuad(BufferBuilder buffer, Matrix4f matrix, float size, float alpha, Vector3f color, float uOff, float vOff) {
		float r = color.x;
		float g = color.y;
		float b = color.z;
		buffer.vertex(matrix, -size, -size, 0).texture(0 + uOff, 0 + vOff).color(r, g, b, alpha).next();
		buffer.vertex(matrix, -size, size, 0).texture(0 + uOff, 1 + vOff).color(r, g, b, alpha).next();
		buffer.vertex(matrix, size, size, 0).texture(1 + uOff, 1 + vOff).color(r, g, b, alpha).next();
		buffer.vertex(matrix, size, -size, 0).texture(1 + uOff, 0 + vOff).color(r, g, b, alpha).next();
	}
}
