package com.diamssword.greenresurgence.gui.components.panels;

import com.diamssword.greenresurgence.gui.components.RButtonComponent;
import com.diamssword.greenresurgence.utils.ColorUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.Surface;

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

	public static Surface crtLinesPanel(int margin, int opacity) {

		var fn = HoloRenders.crtLines(margin, opacity);
		return (context, component) -> {
			fn.draw(context, component.x(), component.y(), component.width(), component.height());
		};
	}

}
