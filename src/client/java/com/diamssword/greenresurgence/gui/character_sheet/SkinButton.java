package com.diamssword.greenresurgence.gui.character_sheet;

import com.diamssword.greenresurgence.utils.ColorUtils;
import io.wispforest.owo.mixin.ui.access.ClickableWidgetAccessor;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class SkinButton extends ButtonComponent {
	private Identifier icon;
	private boolean isSelected;

	protected SkinButton(Text message, Consumer<ButtonComponent> onPress) {
		super(message, onPress);
	}

	public Identifier icon() {
		return icon;
	}

	public SkinButton icon(Identifier icon) {
		this.icon = icon;
		return this;
	}

	public SkinButton selected(boolean isSelected) {
		this.isSelected = isSelected;
		return this;
	}

	public boolean selected() {
		return this.isSelected;
	}

	@Override
	public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
		var bl = false;
		if(this.isSelected && !this.hovered) {
			this.hovered = true;
			bl = true;
		}
		this.renderer.draw((OwoUIDrawContext) context, this, delta);
		context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		var border = 6;
		var m = Math.min(this.width, this.height);
		var x1 = (this.getX() + (this.width / 2)) - ((m - border) / 2);
		var y1 = (this.getY() + (this.height / 2)) - ((m - border) / 2);
		context.drawTexture(this.icon, x1, y1, m - border, m - border, 0, 0, m, m, m, m);
		var textRenderer = MinecraftClient.getInstance().textRenderer;
		context.scale(0.5f, 0.5f, 0.5f);
		context.drawText(textRenderer, this.getMessage(), (int) (this.getX() + this.width / 2f - textRenderer.getWidth(this.getMessage()) / 4f) * 2, (this.getY() + this.height - 5) * 2, ColorUtils.ORANGE, false);
		context.scale(2, 2, 2);
		var tooltip = ((ClickableWidgetAccessor) this).owo$getTooltip();
		if(this.hovered && tooltip != null)
			context.drawTooltip(textRenderer, tooltip.getLines(MinecraftClient.getInstance()), HoveredTooltipPositioner.INSTANCE, mouseX, mouseY);
		if(bl)
			this.hovered = false;
	}
}
