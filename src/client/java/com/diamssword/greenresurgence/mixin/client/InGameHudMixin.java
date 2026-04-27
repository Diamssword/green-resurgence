package com.diamssword.greenresurgence.mixin.client;

import com.diamssword.greenresurgence.render.environment.EnvironementAreas;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import net.minecraft.entity.JumpingMount;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	@Inject(method = "renderVignetteOverlay", at = @At("TAIL"))
	private void renderCustomVignette(DrawContext context, Entity entity, CallbackInfo ci) {
		EnvironementAreas.getCurrentFogModifier().ifPresent(f -> f.vignetteRender(context, entity));
	}

	@Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
	private void cancelXp(DrawContext context, int x, CallbackInfo ci) {
		ci.cancel();

	}

	@Inject(method = "renderMountJumpBar", at = @At("HEAD"), cancellable = true)
	private void cancelMountJump(JumpingMount mount, DrawContext context, int x, CallbackInfo ci) {
		ci.cancel();

	}

	@Inject(method = "renderMountHealth", at = @At("HEAD"), cancellable = true)
	private void cancelMountHealth(DrawContext context, CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "renderHeldItemTooltip", at = @At("HEAD"), cancellable = true)
	private void cancelHeldTooltip(DrawContext context, CallbackInfo ci) {
		ci.cancel();

	}

	@Inject(method = "renderStatusBars", at = @At("HEAD"), cancellable = true)
	private void cancelHealth(DrawContext context, CallbackInfo ci) {
		ci.cancel();

	}

	@Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
	private void cancelHotbar(float tickDelta, DrawContext context, CallbackInfo ci) {
		ci.cancel();
	}
}