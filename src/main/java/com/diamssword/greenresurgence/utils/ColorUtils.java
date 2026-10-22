package com.diamssword.greenresurgence.utils;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ColorUtils {
	public static int GREEN = 0x3F5427;
	public static int GRAY_GREEN = 0x384239;
	public static int ORANGE = 0xbb7d25;
	public static int WHITE = 0xe5e5e5;

	public static int lighten(int argb, float amount) {
		amount = Math.max(0f, Math.min(1f, amount));

		int a = (argb >>> 24) & 0xFF;
		int r = (argb >>> 16) & 0xFF;
		int g = (argb >>> 8) & 0xFF;
		int b = argb & 0xFF;

		r = Math.round(r + (255 - r) * amount);
		g = Math.round(g + (255 - g) * amount);
		b = Math.round(b + (255 - b) * amount);

		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	public static int whithAlpha(int rgb, int alpha) {
		return (alpha << 24) | rgb;
	}

	public static MutableText whiteText(String text) {
		return Text.literal(text).styled(s -> s.withColor(WHITE));
	}

	public static MutableText whiteTextTranslated(String text, Object... args) {
		return Text.translatable(text, args).styled(s -> s.withColor(WHITE));
	}

	public static MutableText textTranslated(String text, int color, Object... args) {
		return Text.translatable(text, args).styled(s -> s.withColor(color));
	}

}
