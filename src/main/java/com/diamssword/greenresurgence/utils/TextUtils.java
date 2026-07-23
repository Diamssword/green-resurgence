package com.diamssword.greenresurgence.utils;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TextUtils {
	public static final Identifier LILITA_ONE = new Identifier("lilita_one");
	public static final Identifier LILITA_ONE_TITLE = new Identifier("lilita_one_title");

	public static int GREEN = 0x3F5427;
	public static int GRAY_GREEN = 0x384239;
	public static int ORANGE = 0xbb7d25;
	public static int WHITE = 0xe5e5e5;

	public static int whithAlpha(int rgb, int alpha) {
		return (alpha << 24) | rgb;
	}

	public static MutableText whiteText(String text) {
		return Text.literal(text).styled(s -> s.withColor(WHITE).withFont(LILITA_ONE));
	}

	public static MutableText whiteTextTranslated(String text, Object... args) {
		return Text.translatable(text, args).styled(s -> s.withColor(WHITE).withFont(LILITA_ONE));
	}

	public static MutableText textTranslated(String text, int color, Object... args) {
		return Text.translatable(text, args).styled(s -> s.withColor(color).withFont(LILITA_ONE));
	}

	public static MutableText whiteTitle(String text) {
		return Text.literal(text).styled(s -> s.withColor(WHITE).withFont(LILITA_ONE_TITLE));
	}
}
