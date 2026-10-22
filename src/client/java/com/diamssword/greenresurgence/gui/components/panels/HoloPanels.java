package com.diamssword.greenresurgence.gui.components.panels;

import com.diamssword.greenresurgence.gui.components.RButtonComponent;
import com.diamssword.greenresurgence.utils.ColorUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Surface;

import java.util.Random;

public class HoloPanels {
	public static int defaultColor = ColorUtils.whithAlpha(ColorUtils.GREEN, 0xff);
	public static int secondaryColor = ColorUtils.whithAlpha(ColorUtils.GRAY_GREEN, 0xff);
	public static int highlightColor = ColorUtils.whithAlpha(ColorUtils.ORANGE, 0xff);
	public static int textColor = ColorUtils.whithAlpha(ColorUtils.WHITE, 0xff);
	public static int defaultOpacity = 0x70;

	public static Surface outline() {
		return Surface.outline(ColorUtils.whithAlpha(defaultColor, defaultOpacity));

	}

	public static Surface panel(boolean secondary, boolean opaque) {

		return Surface.flat(ColorUtils.whithAlpha(secondary ? secondaryColor : defaultColor, opaque ? 0xFF : defaultOpacity));
	}

	public static <T extends ButtonComponent> T button(T button) {
		button.renderer(HoloPanels.button());
		if(button instanceof RButtonComponent rb)
			rb.textColor(HoloPanels.textColor).hoveredTextColor(HoloPanels.textColor);
		else
			button.setMessage(button.getMessage().getWithStyle(button.getMessage().getStyle().withColor(HoloPanels.textColor)).get(0));
		return button;
	}

	public static ButtonComponent.Renderer button() {
		int color = ColorUtils.whithAlpha(highlightColor, defaultOpacity);
		int hoveredColor = ColorUtils.whithAlpha(textColor, defaultOpacity);
		int disabledColor = ColorUtils.whithAlpha(secondaryColor, defaultOpacity / 2);
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

	public static Surface crtLines(int margin, int opacity) {
		long startTime = System.currentTimeMillis();
		Random random = new Random();
		int[] glitchLine = {-1, -1, -1};
		long[] glitchEnd = {0};
		long[] nextGlitch = {startTime + 1000};
		return (context, component) -> {
			int baseColor = defaultColor;
			long now = System.currentTimeMillis();
			double elapsed = now - startTime;

			int x = component.x() + margin;
			int y = component.y() + margin;
			int width = component.width() - margin;
			int height = component.height() - margin;

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

				int color = (alpha << 24) | (defaultColor & 0x00FFFFFF);

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

		return (context, component) -> {
			int baseColor = defaultColor;
			long now = System.currentTimeMillis();
			double elapsed = now - startTime;

			int centerX = component.x() + component.width() / 2;
			int centerY = component.y() + component.height() / 2;
			double r = (component.width() / 2f) - margin;
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
		void draw(OwoUIDrawContext context, Component component);
	}
}
