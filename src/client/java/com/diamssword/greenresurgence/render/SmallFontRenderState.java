package com.diamssword.greenresurgence.render;

public final class SmallFontRenderState {

	private static final ThreadLocal<Float> SCALE =
			ThreadLocal.withInitial(() -> 1.0f);

	private SmallFontRenderState() {
	}

	public static void set(float scale) {
		SCALE.set(scale);
	}

	public static void reset() {
		SCALE.set(1.0f);
	}

	public static float get() {
		return SCALE.get();
	}

}