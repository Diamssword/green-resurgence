package com.diamssword.greenresurgence.gui.components;

import com.diamssword.greenresurgence.GreenResurgence;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.mixin.ui.access.ButtonWidgetAccessor;
import io.wispforest.owo.mixin.ui.access.ClickableWidgetAccessor;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.CursorStyle;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.ui.util.NinePatchTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.render.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Map;
import java.util.function.Consumer;

public class RButtonComponent extends ButtonComponent {

	public static final Identifier ACTIVE_TEXTURE = new Identifier("owo", "button/resurgence_active");
	public static final Identifier HOVERED_TEXTURE = new Identifier("owo", "button/resurgence_hovered");
	public static final Identifier DISABLED_TEXTURE = new Identifier("owo", "button/resurgence_disabled");
	protected boolean textShadow = true;
	protected int textColor = 0xffbb7d25;
	protected int hoveredTextColor = 0xffffffff;
	private boolean activated = false;
	private Identifier icon;
	public static ButtonComponent.Renderer DEFAULT_RENDERER = (context, button, delta) -> {
		RenderSystem.enableDepthTest();

		var texture = button.active ? (button.isHovered() ? HOVERED_TEXTURE : ACTIVE_TEXTURE) : DISABLED_TEXTURE;
		if(button instanceof RButtonComponent bt) {
			texture = bt.active ? bt.hovered || bt.activated ? HOVERED_TEXTURE : ACTIVE_TEXTURE : DISABLED_TEXTURE;
		}
		NinePatchTexture.draw(texture, (OwoUIDrawContext) context, button.getX(), button.getY(), button.width(), button.height());
	};

	public RButtonComponent(Text message, Consumer<RButtonComponent> onPress) {
		super(message, button -> onPress.accept((RButtonComponent) button));
		this.sizing(Sizing.content());
		this.renderer = DEFAULT_RENDERER;
	}

	public RButtonComponent activated(boolean actived) {
		this.activated = actived;
		return this;
	}

	public boolean activated() {
		return activated;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
	}

	@Override
	public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {

		if(renderer != null)
			renderer.draw((OwoUIDrawContext) context, this, delta);

		var textRenderer = MinecraftClient.getInstance().textRenderer;
		int color = this.hovered || this.activated ? hoveredTextColor : this.textColor;
		var text = textRenderer.trimToWidth(this.getMessage(), this.width - 5);
		if(icon != null) {
			var h = (int) (this.height * 0.70f);
			var h1 = (int) (this.height * 0.15f);
			drawTexture(context, icon, this.getX() + h1, this.getY() + h1, (float) 0, 0, h, h, h, h, color);
		}
		if(this.textShadow) {
			context.drawCenteredTextWithShadow(textRenderer, text.getString(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, color);
		} else {

			context.drawText(textRenderer, text.getString(), (int) (this.getX() + this.width / 2f - textRenderer.getWidth(this.getMessage()) / 2f), (int) (this.getY() + (this.height - 8) / 2f), color, false);
		}

		var tooltip = ((ClickableWidgetAccessor) this).owo$getTooltip();
		if(this.hovered && tooltip != null)
			context.drawTooltip(textRenderer, tooltip.getLines(MinecraftClient.getInstance()), HoveredTooltipPositioner.INSTANCE, mouseX, mouseY);
	}

	public RButtonComponent onPress(Consumer<ButtonComponent> onPress) {
		((ButtonWidgetAccessor) this).owo$setOnPress(button -> onPress.accept((RButtonComponent) button));
		return this;
	}

	public RButtonComponent icon(String path) {
		this.icon = GreenResurgence.asRessource("textures/gui/icons/" + path + ".png");
		return this;
	}

	public RButtonComponent icon(Identifier fullPath) {
		this.icon = fullPath;
		return this;
	}

	public RButtonComponent textShadow(boolean textShadow) {
		this.textShadow = textShadow;
		return this;
	}

	public int textColor() {
		return this.textColor;

	}

	public int hoveredTextColor() {
		return this.hoveredTextColor;

	}

	public RButtonComponent hoveredTextColor(int color) {
		this.hoveredTextColor = color;
		return this;
	}

	public RButtonComponent textColor(int color) {
		this.textColor = color;
		return this;
	}

	public boolean textShadow() {
		return this.textShadow;
	}

	public RButtonComponent active(boolean active) {
		this.active = active;
		return this;
	}

	public boolean active() {
		return this.active;
	}

	@Override
	public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
		super.parseProperties(model, element, children);

		UIParsing.apply(children, "text", UIParsing::parseText, this::setMessage);
		UIParsing.apply(children, "icon", Node::getTextContent, this::icon);
		UIParsing.apply(children, "text-color", Color::parseAndPack, this::textColor);
		UIParsing.apply(children, "text-shadow", UIParsing::parseBool, this::textShadow);
	}

	protected CursorStyle owo$preferredCursorStyle() {
		return CursorStyle.HAND;
	}

	static void drawTexturedQuad(DrawContext ctx, Identifier texture, int x1, int x2, int y1, int y2, int z, float u1, float u2, float v1, float v2, float red, float green, float blue, float alpha) {
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
		RenderSystem.enableBlend();
		Matrix4f matrix4f = ctx.getMatrices().peek().getPositionMatrix();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
		bufferBuilder.vertex(matrix4f, (float) x1, (float) y1, (float) z).color(red, green, blue, alpha).texture(u1, v1).next();
		bufferBuilder.vertex(matrix4f, (float) x1, (float) y2, (float) z).color(red, green, blue, alpha).texture(u1, v2).next();
		bufferBuilder.vertex(matrix4f, (float) x2, (float) y2, (float) z).color(red, green, blue, alpha).texture(u2, v2).next();
		bufferBuilder.vertex(matrix4f, (float) x2, (float) y1, (float) z).color(red, green, blue, alpha).texture(u2, v1).next();
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		RenderSystem.disableBlend();
	}

	public static void drawTexture(DrawContext ctx, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, int color) {
		var col = Color.ofArgb(color);
		drawTexturedQuad(ctx, texture, x, x + width, y, y + height, 0, (u + 0.0F) / (float) textureWidth, (u + (float) width) / (float) textureWidth, (v + 0.0F) / (float) textureHeight, (v + (float) height) / (float) textureHeight, col.red(), col.green(), col.blue(), col.alpha());
	}
}
