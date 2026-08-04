package com.diamssword.greenresurgence.gui.components;

import com.diamssword.greenresurgence.DrawUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.util.NinePatchTexture;
import net.minecraft.util.Identifier;

public class Panels {
	public static final Identifier PANEL_NINE_PATCH_TEXTURE = new Identifier("owo", "panel/resurgence");
	public static final Identifier OVERLAY_NINE_PATCH_TEXTURE = new Identifier("owo", "panel/r_overlay");
	public static final Identifier CRAFT_NINE_PATCH_TEXTURE = new Identifier("owo", "panel/craft");
	public static Surface PANEL = (context, component) -> {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		NinePatchTexture.draw(PANEL_NINE_PATCH_TEXTURE, context, component.x(), component.y(), component.width(), component.height());
		RenderSystem.disableBlend();
	};

	public static Surface CRAFT(int color, boolean search) {
		return (context, component) -> {
			var thick = component.width() > 32 ? 2 : 1;
			context.fill(component.x(), component.y(), component.x() + component.width(), component.y() + component.height(), color);

			context.fill(component.x(), component.y() + thick, component.x() + thick, component.y() + component.height(), DrawUtils.colorLighten(color, 0.2f));
			context.fill(component.x() + thick, component.y(), component.x() + component.width(), component.y() + thick, DrawUtils.colorLighten(color, 0.2f));

			context.fill(component.x() + component.width() - thick, component.y() + thick, component.x() + component.width(), component.y() + component.height(), DrawUtils.colorDarken(color, 0.2f));
			context.fill(component.x() + thick, component.y() + component.height() - thick, component.x() + component.width(), component.y() + component.height(), DrawUtils.colorDarken(color, 0.2f));

			context.fill(component.x(), component.y(), component.x() + thick, component.y() + thick, DrawUtils.colorLighten(color, 0.4f));
			context.fill(component.x(), component.y() + component.height() - thick, component.x() + thick, component.y() + component.height(), color);
			context.fill(component.x() + component.width() - thick, component.y(), component.x() + component.width(), component.y() + thick, color);
			if(search) {
				int off = 11;
				context.fill(component.x() + thick, component.y() + off, component.x() + component.width(), component.y() + thick + off, DrawUtils.colorLighten(color, 0.2f));
				context.fill(component.x(), component.y() + off, component.x() + thick, component.y() + thick + off, DrawUtils.colorLighten(color, 0.4f));
				context.fill(component.x() + component.width() - thick, component.y() + off, component.x() + component.width(), component.y() + thick + off, color);
			}
		};
	}

	;
	public static Surface PANEL_WHITE = (context, component) -> {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		NinePatchTexture.draw(OVERLAY_NINE_PATCH_TEXTURE, context, component.x(), component.y(), component.width(), component.height());
		RenderSystem.disableBlend();
	};

	public static void drawOverlay(OwoUIDrawContext ctx, int x, int y, int width, int height) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		NinePatchTexture.draw(OVERLAY_NINE_PATCH_TEXTURE, ctx, x, y, width, height);
		RenderSystem.disableBlend();
	}

}
