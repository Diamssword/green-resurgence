package com.diamssword.greenresurgence.mixin.client;

import com.diamssword.greenresurgence.render.SmallFontRenderState;
import com.diamssword.greenresurgence.utils.TextUtils;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.text.Style;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.font.TextRenderer$Drawer")
public abstract class TextRendererDrawerMixin {
	/**
	 * X position before Minecraft renders/advances the current character.
	 */
	private float smallfont$startX;
	@Shadow
	float x;

	@Shadow
	float y;

	@Inject(
			method = "accept",
			at = @At("HEAD")
	)
	private void smallfont$before(
			int index,
			Style style,
			int codePoint,
			CallbackInfoReturnable<Boolean> cir
	) {
		smallfont$startX = this.x;
		if(TextUtils.MC_LITTLE.equals(style.getFont())) {
			SmallFontRenderState.set(TextUtils.MC_LITTLE_SCALE);
		} else {
			SmallFontRenderState.reset();
		}
	}

	@Inject(
			method = "accept",
			at = @At("RETURN")
	)
	private void smallfont$after(
			int index,
			Style style,
			int codePoint,
			CallbackInfoReturnable<Boolean> cir
	) {
		SmallFontRenderState.reset();
	}

	@Redirect(
			method = "accept",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/font/TextRenderer;drawGlyph(Lnet/minecraft/client/font/GlyphRenderer;ZZFFFLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumer;FFFFI)V"
			)
	)
	private void smallfont$drawGlyph(
			TextRenderer instance, GlyphRenderer glyphRenderer, boolean bold, boolean italic, float weight, float x, float y, Matrix4f matrix, VertexConsumer vertexConsumer, float red, float green, float blue, float alpha, int light
	) {
		float scale = SmallFontRenderState.get();

		if(scale == 1.0f) {
			glyphRenderer.draw(
					italic,
					x,
					y,
					matrix,
					vertexConsumer,
					red,
					green,
					blue,
					alpha,
					light
			);
			return;
		}

		/*
		 * Scale around the glyph's own position.
		 */
		Matrix4f scaledMatrix = new Matrix4f(matrix);

		float verticalOffset = 7.0f * (1.0f - scale);

		scaledMatrix.translate(x, y + verticalOffset, 0.0f);
		scaledMatrix.scale(scale, scale, 1.0f);
		scaledMatrix.translate(-x, -y, 0.0f);

		glyphRenderer.draw(
				italic,
				x,
				y,
				scaledMatrix,
				vertexConsumer,
				red,
				green,
				blue,
				alpha,
				light
		);
	}

	/**
	 * Scale the cursor advance.
	 * <p>
	 * This fixes the large gaps between characters.
	 */
	@Inject(
			method = "accept",
			at = @At("RETURN")
	)
	private void smallfont$afterCharacter(
			int index,
			Style style,
			int codePoint,
			CallbackInfoReturnable<Boolean> cir
	) {
		if(TextUtils.MC_LITTLE.equals(style.getFont())) {
			float scale = TextUtils.MC_LITTLE_SCALE;

			float vanillaAdvance = this.x - smallfont$startX;

			this.x = smallfont$startX + vanillaAdvance * scale;
		}

		SmallFontRenderState.reset();
	}
}