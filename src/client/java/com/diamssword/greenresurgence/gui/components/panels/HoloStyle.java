package com.diamssword.greenresurgence.gui.components.panels;

import com.diamssword.greenresurgence.utils.ColorUtils;

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
