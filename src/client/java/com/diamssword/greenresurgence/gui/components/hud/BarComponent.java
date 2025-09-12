package com.diamssword.greenresurgence.gui.components.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.util.Identifier;
import org.w3c.dom.Element;

public class BarComponent extends TextureComponent implements IHideableComponent {

	private float fillPercent = 1;
	private float goal = 1;
	private boolean hidden;
	public boolean reversedIndex = false;

	public BarComponent(Identifier texture, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight, boolean reversed) {
		super(texture, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
		this.reversedIndex = reversed;
	}

	public void tick() {

		if (goal > fillPercent) {
			float d = Math.max((goal - fillPercent) / 10f, 0.005f);
			fillPercent = Math.min(goal, fillPercent + d);
		} else if (goal < fillPercent) {
			float d = Math.max((fillPercent - goal) / 10f, 0.005f);
			fillPercent = Math.max(goal, fillPercent - d);
		}
	}

	@Override
	protected int determineHorizontalContentSize(Sizing sizing) {
		var v = verticalSizing.get();
		if (v != null && !v.isContent() && height > 0) {
			return (int) (height * ((float) this.regionWidth / (float) this.regionHeight));
		}
		return this.regionWidth;
	}

	@Override
	protected int determineVerticalContentSize(Sizing sizing) {
		var v = horizontalSizing.get();
		if (v != null && !v.isContent() && width > 0) {
			return (int) (width * ((float) this.regionHeight / (float) this.regionWidth));
		}
		return this.regionHeight;
	}

	@Override
	protected void applySizing() {
		final var horizontalSizing = this.horizontalSizing.get();
		final var verticalSizing = this.verticalSizing.get();
		final var margins = this.margins.get();
		if (verticalSizing.isContent()) {
			this.width = horizontalSizing.inflate(this.space.width() - margins.horizontal(), this::determineHorizontalContentSize);
			this.height = verticalSizing.inflate(this.space.height() - margins.vertical(), this::determineVerticalContentSize);
		} else {
			this.height = verticalSizing.inflate(this.space.height() - margins.vertical(), this::determineVerticalContentSize);
			this.width = horizontalSizing.inflate(this.space.width() - margins.horizontal(), this::determineHorizontalContentSize);
		}
	}

	@Override
	public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
		RenderSystem.enableDepthTest();
		if (hidden)
			return;

		if (this.blend) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
		}

		var matrices = context.getMatrices();
		matrices.push();
		matrices.translate(x, y, 0);
		float invScaleX = 1.0f / 0.01f;
		int w = (int) (this.width * invScaleX);
		int h = (int) (this.height * invScaleX);

		matrices.scale(0.01f, 0.01f, 0);

		int barWidth = Math.round(w * fillPercent);
		int texBarWidth = Math.round(textureWidth * fillPercent);
		if (!reversedIndex) {
			context.drawTexture(this.texture,
					0,
					0,
					w,
					h,
					this.u,
					this.v,
					this.regionWidth,
					this.regionHeight,
					this.textureWidth, this.textureHeight
			);
		}
		context.drawTexture(this.texture,
				0,
				0,
				barWidth,
				h,
				this.u,
				this.v + this.regionHeight,
				texBarWidth,
				this.regionHeight,
				this.textureWidth, this.textureHeight
		);
		if (reversedIndex) {
			context.drawTexture(this.texture,
					0,
					0,
					w,
					h,
					this.u,
					this.v,
					this.regionWidth,
					this.regionHeight,
					this.textureWidth, this.textureHeight
			);
		}
		if (this.blend) {
			RenderSystem.disableBlend();
		}

		matrices.pop();
	}

	public float getFillPercent() {
		return fillPercent;
	}

	public void setFillPercent(float fillPercent) {
		this.fillPercent = fillPercent;
		this.goal = fillPercent;
	}

	public void animateFillPercent(float fillPercent) {
		this.goal = fillPercent;
	}

	public static BarComponent parse(Element element) {
		UIParsing.expectAttributes(element, "texture");
		var textureId = UIParsing.parseIdentifier(element.getAttributeNode("texture"));
		boolean reversed = false;
		int u = 0, v = 0, regionWidth = 0, regionHeight = 0, textureWidth = 256, textureHeight = 256;
		if (element.hasAttribute("u")) {
			u = UIParsing.parseSignedInt(element.getAttributeNode("u"));
		}

		if (element.hasAttribute("v")) {
			v = UIParsing.parseSignedInt(element.getAttributeNode("v"));
		}
		if (element.hasAttribute("reversed")) {
			reversed = UIParsing.parseBool(element.getAttributeNode("reversed"));
		}
		if (element.hasAttribute("region-width")) {
			regionWidth = UIParsing.parseSignedInt(element.getAttributeNode("region-width"));
		}

		if (element.hasAttribute("region-height")) {
			regionHeight = UIParsing.parseSignedInt(element.getAttributeNode("region-height"));
		}

		if (element.hasAttribute("texture-width")) {
			textureWidth = UIParsing.parseSignedInt(element.getAttributeNode("texture-width"));
		}

		if (element.hasAttribute("texture-height")) {
			textureHeight = UIParsing.parseSignedInt(element.getAttributeNode("texture-height"));
		}

		return new BarComponent(new Identifier(textureId.getNamespace(), "textures/gui/" + textureId.getPath()), u, v, regionWidth, regionHeight, textureWidth, textureHeight, reversed);
	}

	@Override
	public void hidden(boolean hidden) {
		this.hidden = hidden;
	}

	@Override
	public boolean isHidden() {
		return this.hidden;
	}
}
