package com.diamssword.greenresurgence.mixin.client;

import com.diamssword.greenresurgence.render.environment.EnvironementAreas;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

	@Accessor("red")
	static float getRed() {
		throw new AssertionError();
	}

	@Accessor("green")
	static float getGreen() {
		throw new AssertionError();
	}

	@Accessor("blue")
	static float getBlue() {
		throw new AssertionError();
	}

	@Inject(at = @At("TAIL"), method = "render")
	private static void render(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness, CallbackInfo ci) {
		EnvironementAreas.getCurrentFogModifier().ifPresent(f -> f.effectRender(camera, tickDelta, world, viewDistance, skyDarkness, getRed(), getGreen(), getBlue()));
	}

	@Inject(at = @At("TAIL"), method = "applyFog")
	private static void fogRenderer(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
		EnvironementAreas.getCurrentFogModifier().ifPresent(f -> f.fogRender(camera, fogType, viewDistance, thickFog, tickDelta, getRed(), getGreen(), getBlue()));
	}
}
