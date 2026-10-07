package com.diamssword.greenresurgence.gui.components.panels;

import com.diamssword.greenresurgence.utils.ColorUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.Surface;

import java.util.Random;

public class HoloPanels {
	public static int defaultColor = ColorUtils.GREEN;
	public static int secondaryColor = ColorUtils.GRAY_GREEN;
	public static int highlightColor = ColorUtils.ORANGE;
	public static int textColor = ColorUtils.WHITE;
	public static int defaultOpacity = 0x70;

	public static Surface outline() {
		return Surface.outline(ColorUtils.whithAlpha(defaultColor, defaultOpacity));

	}

	public static Surface panel(boolean secondary, boolean opaque) {

		return Surface.flat(ColorUtils.whithAlpha(secondary ? secondaryColor : defaultColor, opaque ? 0xFF : defaultOpacity));
	}

	public static ButtonComponent.Renderer button() {
		int color = ColorUtils.whithAlpha(highlightColor, defaultOpacity);
		int hoveredColor = ColorUtils.whithAlpha(textColor, defaultOpacity);
		int disabledColor = ColorUtils.whithAlpha(secondaryColor, defaultOpacity / 2);
		return (context, button, delta) -> {
			RenderSystem.enableDepthTest();

			if(button.active) {
				if(button.isHovered()) {
					context.drawRectOutline(button.x(), button.y(), button.width(), button.height(), hoveredColor);
				} else {
					context.drawRectOutline(button.x(), button.y(), button.width(), button.height(), color);
				}
			} else {
				context.drawRectOutline(button.x(), button.y(), button.width(), button.height(), disabledColor);
			}
		};
	}

	public static Surface crtLines(int margin) {
		long startTime = System.currentTimeMillis();
		Random random = new Random();
		int[] glitchLine = {-1, -1, -1};
		long[] glitchEnd = {0};
		long[] nextGlitch = {startTime + 1000};
		return (context, component) -> {
			int baseColor = ColorUtils.whithAlpha(defaultColor, defaultOpacity);
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

				int alpha = 18;

				if(glitchActive && (lineIndex == glitchLine[0] || lineIndex == glitchLine[1] || lineIndex == glitchLine[2])) {
					alpha = 50;
				}

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
				int alpha = (int) (strength * 45);

				int color = (alpha << 24) | (baseColor & 0xFFFFFF);

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
}
