package com.diamssword.greenresurgence.containers.player;

import com.diamssword.greenresurgence.utils.ColorUtils;
import net.minecraft.nbt.NbtCompound;

public class HoloStyle {
	private final int bgAlpha;
	private final int baseAlpha;
	private final int mainColor;
	private final int secondaryColor;
	private final int highlightColor;
	private final int textColor;

	public HoloStyle(int mainColor, int secondaryColor, int highlightColor, int textColor, int baseAlpha, int bgAlpha) {
		this.baseAlpha = baseAlpha;
		this.bgAlpha = bgAlpha;
		this.mainColor = mainColor;
		this.secondaryColor = secondaryColor;
		this.highlightColor = highlightColor;
		this.textColor = textColor;
	}

	public NbtCompound toNBT() {
		var t = new NbtCompound();
		t.putInt("bgAlpha", bgAlpha);
		t.putInt("baseAlpha", baseAlpha);
		t.putInt("mainColor", mainColor);
		t.putInt("secondaryColor", secondaryColor);
		t.putInt("highlightColor", highlightColor);
		t.putInt("textColor", textColor);
		return t;
	}

	private static int getNbtColorOr(NbtCompound tag, String key, int defaut) {
		if(tag.contains(key)) {
			var v = tag.getInt(key);
			if(v > 0)
				return v;
		}
		return defaut;
	}

	public static HoloStyle fromNBT(NbtCompound tag) {
		var bgAlpha = getNbtColorOr(tag, "bgAlpha", 0x70);
		var baseAlpha = getNbtColorOr(tag, "baseAlpha", 0xA0);
		var mainColor = getNbtColorOr(tag, "mainColor", ColorUtils.GREEN);
		var secondaryColor = getNbtColorOr(tag, "secondaryColor", ColorUtils.GRAY_GREEN);
		var highlightColor = getNbtColorOr(tag, "highlightColor", ColorUtils.ORANGE);
		var textColor = getNbtColorOr(tag, "textColor", ColorUtils.WHITE);
		return new HoloStyle(mainColor, secondaryColor, highlightColor, textColor, baseAlpha, bgAlpha);
	}

	public int getBaseAlphaAlpha() {
		return baseAlpha;
	}

	public int getBackgroundAlpha() {
		return bgAlpha;
	}

	public HoloColor getSecondaryColor() {
		return new HoloColor(this, secondaryColor);
	}

	public HoloColor getHightlightColor() {
		return new HoloColor(this, highlightColor);
	}

	public HoloColor getTextColor() {
		return new HoloColor(this, textColor);
	}

	public HoloColor getMainColor() {
		return new HoloColor(this, mainColor);
	}

	public static class HoloColor {
		private final int solid;
		private final int border;
		private final int bg;

		public HoloColor(HoloStyle style, int color) {
			this.solid = ColorUtils.whithAlpha(color, 0xFF);
			this.bg = ColorUtils.whithAlpha(color, style.bgAlpha);
			this.border = ColorUtils.whithAlpha(color, style.baseAlpha);
		}

		public int border() {
			return border;
		}

		public int solid() {
			return solid;
		}

		public int background() {
			return bg;
		}

	}

}
