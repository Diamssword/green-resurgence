package com.diamssword.greenresurgence.render;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.mixin.client.ClientAccessor;
import com.diamssword.greenresurgence.mixin.client.FontManagerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class CustomFont {

	private static final MinecraftClient mc = MinecraftClient.getInstance();
	public static final Identifier LILITA_ONE = new Identifier("lilita_one");
	public static final Identifier LILITA_ONE_TITLE = new Identifier("lilita_one_title");
	private static final List<Identifier> toLoad = List.of(LILITA_ONE, LILITA_ONE_TITLE);
	private static final Map<Identifier, TextRenderer> fonts = new HashMap<>();

	private static Pair<TextRenderer, Boolean> getTextRenderer(Identifier identifier) {
		FontManagerAccessor fma = ((FontManagerAccessor) ((ClientAccessor) mc).getFontManager());
		AtomicBoolean d = new AtomicBoolean(false);
		TextRenderer tr = new TextRenderer(id -> {
			FontStorage storage = fma.getFontStorages().getOrDefault(identifier, fma.getFontStorages().getOrDefault(new Identifier("default"), fma.getMissingStorage()));
			if (storage == fma.getFontStorages().get(new Identifier("default"))) {
				d.set(true);
			}
			return storage;
		}, true);
		return new Pair<>(tr, d.get());
	}

	public static TextRenderer getFont(Identifier font) {
		return fonts.getOrDefault(font, mc.textRenderer);
	}

	private static TextRenderer loadFont(Identifier id) {
		Pair<TextRenderer, Boolean> textRendererAndDefault = getTextRenderer(id);
		var renderer = textRendererAndDefault.getLeft();
		if (textRendererAndDefault.getRight()) {
			GreenResurgence.LOGGER.error("Error initializing TTF renderer for " + id + ", defaulting to minecraft font");
			return mc.textRenderer;
		}
		return renderer;
	}

	public static void initTextRenderer() {
		fonts.clear();
		for (var i : toLoad) {
			fonts.put(i, loadFont(i));
		}
	}
}



