package com.diamssword.greenresurgence.gui.components.panels;

import com.diamssword.greenresurgence.gui.components.RButtonComponent;
import com.diamssword.greenresurgence.utils.ColorUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Surface;

import java.util.Random;

public class HoloPanels {

	public static HoloStyle getCurrentStyle() {
		return new HoloStyle(ColorUtils.GREEN, ColorUtils.GRAY_GREEN, ColorUtils.ORANGE, ColorUtils.WHITE, 0xA0, 0x70);
	}

	public static Surface outline() {
		return Surface.outline(getCurrentStyle().getMainColor().border());

	}

	public static Surface panel(boolean secondary, boolean opaque) {

		var style = HoloPanels.getCurrentStyle();
		var c = secondary ? style.getSecondaryColor() : style.getMainColor();
		return Surface.flat(opaque ? c.solid() : c.background());
	}

	public static <T extends ButtonComponent> T button(T button) {
		var style = HoloPanels.getCurrentStyle();
		button.renderer(HoloPanels.button());
		if(button instanceof RButtonComponent rb)
			rb.textColor(style.getTextColor().solid()).hoveredTextColor(style.getTextColor().solid());
		else
			button.setMessage(button.getMessage().getWithStyle(button.getMessage().getStyle().withColor(style.getTextColor().solid())).get(0));
		return button;
	}

	public static ButtonComponent.Renderer button() {
		var style = HoloPanels.getCurrentStyle();
		int color = style.getHightlightColor().border();
		int hoveredColor = style.getTextColor().border();
		int disabledColor = style.getSecondaryColor().background();
		return (context, button, delta) -> {
			RenderSystem.enableDepthTest();
			RenderSystem.enableBlend();
			var colorF = button.active ? (button.isHovered() ? hoveredColor : color) : disabledColor;
			if(button instanceof RButtonComponent bt) {
				colorF = bt.active ? bt.isHovered() || bt.activated() ? hoveredColor : color : disabledColor;
			}
			context.drawRectOutline(button.x(), button.y(), button.width(), button.height(), colorF);
		};
	}

	private static int scaleAlpha(int effectAlpha, int opacity) {
		return Math.round(effectAlpha * (Math.max(0, Math.min(255, opacity)) / 255f));
	}

	public static Surface crtLinesPanel(int margin, int opacity) {

		var fn = crtLines(margin, opacity);
		return (context, component) -> {
			fn.draw(context, component.x(), component.y(), component.width(), component.height());
		};
	}

	public static RenderSurface crtLines(int margin, int opacity) {
		long startTime = System.currentTimeMillis();
		Random random = new Random();
		int[] glitchLine = {-1, -1, -1};
		long[] glitchEnd = {0};
		long[] nextGlitch = {startTime + 1000};
		int baseColor = getCurrentStyle().getMainColor().solid();
		return (context, x1, y1, width1, height1) -> {

			long now = System.currentTimeMillis();
			double elapsed = now - startTime;

			int x = x1 + margin;
			int y = y1 + margin;
			int width = width1 - margin;
			int height = height1 - margin;

			// Schedule the next glitch
			if(now >= nextGlitch[0]) {
				int lineCount = Math.max(1, height / 3);
				glitchLine[0] = random.nextInt(lineCount);
				if(random.nextBoolean()) {
					glitchLine[1] = random.nextInt(lineCount);
					if(random.nextBoolean())
						glitchLine[2] = random.nextInt(lineCount);
					else
						glitchLine[2] = -1;
				} else
					glitchLine[1] = -1;
				// Short flash: 50-150ms
				glitchEnd[0] = now + 100 + random.nextInt(300);

				// Next glitch in 1-4 seconds
				nextGlitch[0] = glitchEnd[0] + 1000 + random.nextInt(3000);
			}

			boolean glitchActive = now < glitchEnd[0];

			// Static scanlines
			for(int i = 0; i < height; i += 3) {
				int lineIndex = i / 3;
				int alpha = scaleAlpha(
						glitchActive && (lineIndex == glitchLine[0]
								|| lineIndex == glitchLine[1]
								|| lineIndex == glitchLine[2]) ? 255 : 128,
						opacity
				);


				int color = (alpha << 24) | (baseColor & 0xFFFFFF);
				context.fill(x, y + i, x + width, y + i + 1, color);
			}

			// Moving band
			double progress = (elapsed / 3000.0) % 1.0;
			int bandY = y + (int) (progress * height);

			for(int offset = -5; offset <= 5; offset++) {
				int lineY = bandY + offset;

				if(lineY < y || lineY >= y + height)
					continue;

				float strength = 1.0f - Math.abs(offset) / 6.0f;
				// Moving band
				int alpha = scaleAlpha(Math.round(strength * 255), opacity);

				int color = (alpha << 24) | (baseColor & 0x00FFFFFF);

				context.fill(
						x,
						lineY,
						x + width,
						lineY + 1,
						color
				);
			}
		};
	}

	public static RenderSurface crtCircle(int margin) {
		long startTime = System.currentTimeMillis();
		Random random = new Random();

		int[] glitchLine = {-1, -1, -1};
		long[] glitchEnd = {0};
		long[] nextGlitch = {startTime + 1000};

		int baseColor = getCurrentStyle().getMainColor().solid();
		return (context, x, y1, width, height) -> {
			long now = System.currentTimeMillis();
			double elapsed = now - startTime;

			int centerX = x + width / 2;
			int centerY = y1 + height / 2;
			double r = (width / 2f) - margin;
			if(r <= 0) return;
			int yStart = (int) Math.ceil(centerY - r);
			int yEnd = (int) Math.floor(centerY + r);
			int lineCount = Math.max(1, (yEnd - yStart) / 3);

			// Schedule the next glitch
			if(now >= nextGlitch[0]) {
				glitchLine[0] = random.nextInt(lineCount);

				if(random.nextBoolean()) {
					glitchLine[1] = random.nextInt(lineCount);
					glitchLine[2] = random.nextBoolean()
							? random.nextInt(lineCount)
							: -1;
				} else {
					glitchLine[1] = -1;
					glitchLine[2] = -1;
				}

				glitchEnd[0] = now + 100 + random.nextInt(300);
				nextGlitch[0] = glitchEnd[0] + 1000 + random.nextInt(3000);
			}

			boolean glitchActive = now < glitchEnd[0];

			// Static scanlines, clipped to the circle
			for(int y = yStart; y < yEnd; y += 3) {
				int lineIndex = (y - yStart) / 3;

				double dy = y - centerY;
				double halfWidth = Math.sqrt(Math.max(0, r * r - dy * dy));

				int x1 = (int) Math.ceil(centerX - halfWidth);
				int x2 = (int) Math.floor(centerX + halfWidth);

				int alpha = 100;

				if(glitchActive &&
						(lineIndex == glitchLine[0]
								|| lineIndex == glitchLine[1]
								|| lineIndex == glitchLine[2])) {
					alpha = 200;
				}

				int color = (alpha << 24) | (baseColor & 0xFFFFFF);
				context.fill(x1, y, x2, y + 1, color);
			}

			// Moving band, also clipped to the circle
			double progress = (elapsed / 3000.0) % 1.0;
			int bandY = yStart + (int) (progress * (yEnd - yStart));

			for(int offset = -5; offset <= 5; offset++) {
				int lineY = bandY + offset;

				if(lineY < yStart || lineY >= yEnd) continue;

				double dy = lineY - centerY;
				double halfWidth = Math.sqrt(Math.max(0, r * r - dy * dy));

				int x1 = (int) Math.ceil(centerX - halfWidth);
				int x2 = (int) Math.floor(centerX + halfWidth);

				float strength = 1.0f - Math.abs(offset) / 6.0f;
				int alpha = (int) (strength * 45);


				int color = (alpha << 24) | (baseColor & 0xFFFFFF);
				context.fill(x1, lineY, x2, lineY + 1, color);
			}
		};
	}


	public static interface RenderSurface {
		void draw(OwoUIDrawContext context, int x, int y, int width, int height);
	}
}
