package com.diamssword.greenresurgence.render.environment;

import com.diamssword.greenresurgence.GreenResurgence;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

public abstract class FogModifier {
	private final Box box;
	public boolean strongAtBottom;

	public FogModifier(Box box, boolean strongAtBottom) {
		this.box = box;
		this.strongAtBottom = strongAtBottom;
	}

	public abstract Vector3f getFogColor(float intensity);

	public abstract Vector4f getVignetteColor(float intensity);

	public Vec3d modifySky(Vec3d cameraPos, float tickDelta, Vec3d colorIn) {
		float intensity = getIntensity(cameraPos, getBox(), strongAtBottom);
		var col = getFogColor(intensity);
		float r = (float) MathHelper.lerp(1f - intensity, col.x, colorIn.x);
		float g = (float) MathHelper.lerp(1f - intensity, col.y, colorIn.y);
		float b = (float) MathHelper.lerp(1f - intensity, col.z, colorIn.z);
		return new Vec3d(r, g, b);
	}

	public void vignetteRender(DrawContext context, Entity entity) {
		double intensity = getIntensity(entity.getPos(), getBox(), strongAtBottom);
		MinecraftClient client = MinecraftClient.getInstance();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderTexture(0, GreenResurgence.asRessource("textures/gui/fog_vignette.png"));
		var col = getVignetteColor((float) intensity);
		RenderSystem.setShaderColor(col.x, col.y, col.z, col.w);

		context.drawTexture(
				GreenResurgence.asRessource("textures/gui/fog_vignette.png"),
				0, 0,
				0, 0,
				client.getWindow().getScaledWidth(),
				client.getWindow().getScaledHeight(),
				client.getWindow().getScaledWidth(),
				client.getWindow().getScaledHeight()
		);

		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.disableBlend();
	}


	public void insideZoneUpdate(double distanceFromCenter, long time) {

	}

	public void outsideZoneUpdate(double distance, long time) {

	}

	public Box getBox() {
		return box;
	}

	public void effectRender(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness, float red, float green, float blue) {

	}

	public void fogRender(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, float red, float green, float blue) {
		if(fogType == BackgroundRenderer.FogType.FOG_SKY)
			return;
		float base;
		float baseEnd;
		if(thickFog) {
			base = viewDistance * 0.05f;
			baseEnd = Math.min(viewDistance, 192.0F) * 0.5F;
		} else {
			float f = MathHelper.clamp(viewDistance / 10.0F, 4.0F, 64.0F);
			base = viewDistance - f;
			baseEnd = viewDistance;
		}
		float intensity = getIntensity(camera.getPos(), getBox(), strongAtBottom);
		RenderSystem.setShaderFogShape(FogShape.CYLINDER);  // starts close
		RenderSystem.setShaderFogStart(MathHelper.lerp(intensity, base, -8f));  // starts close
		RenderSystem.setShaderFogEnd(MathHelper.lerp(intensity, baseEnd, 96f));  // starts close
		//RenderSystem.setShaderFogEnd(100.0f);    // fades out far
		var col = getFogColor(intensity);
		float r = MathHelper.lerp(1f - intensity, col.x, red);
		float g = MathHelper.lerp(1f - intensity, col.y, green);
		float b = MathHelper.lerp(1f - intensity, col.z, blue);
		float brightness = (red + green + blue) / 3.0f;
		r = r * brightness;
		g = g * brightness;
		b = b * brightness;
		RenderSystem.setShaderFogColor(r, g, b);
	}

	static float getIntensity(Vec3d pos, Box box, boolean strongerAtBottom) {
		double dx = Math.min(pos.x - box.minX, box.maxX - pos.x);
		double dz = Math.min(pos.z - box.minZ, box.maxZ - pos.z);
		double distToEdge = Math.min(dx, dz);

		double maxDistance = 20.0;
		double intensity = distToEdge / maxDistance;
		intensity = MathHelper.clamp(intensity, 0.0, 1.0);
		intensity = intensity * intensity;
		if(strongerAtBottom) {
			double height = (pos.y - box.minY) / (box.maxY - box.minY);
			height = MathHelper.clamp(height, 0.0, 1.0);
			double yFactor = 1.0 - height;
			intensity *= yFactor;
		}
		return (float) intensity;
	}
}
