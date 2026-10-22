package com.diamssword.greenresurgence.gui.components.hud;

import com.diamssword.greenresurgence.DrawUtils;
import com.diamssword.greenresurgence.render.images.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.util.Identifier;
import org.w3c.dom.Element;

public class IconComponent extends TextureComponent implements IHideableComponent {
	private boolean hidden;
	private int color = 0xffffffff;

	public IconComponent(Identifier texture, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
		super(texture, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
	}

	public IconComponent color(int argb) {
		this.color = argb;
		return this;
	}

	public int color() {
		return color;
	}

	@Override
	public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
		if(this.hidden)
			return;
		RenderSystem.enableDepthTest();

		if(this.blend) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
		}

		var matrices = context.getMatrices();
		matrices.push();
		matrices.translate(x, y, 0);
		matrices.scale(this.width / (float) this.regionWidth, this.height / (float) this.regionHeight, 0);

		var visibleArea = this.visibleArea.get();

		int bottomEdge = Math.min(visibleArea.y() + visibleArea.height(), regionHeight);
		int rightEdge = Math.min(visibleArea.x() + visibleArea.width(), regionWidth);
		DrawUtils.drawTexturedQuadWithColor(context, this.texture,
				visibleArea.x(),
				visibleArea.y(),
				rightEdge - visibleArea.x(),
				bottomEdge - visibleArea.y(),
				this.u + visibleArea.x(),
				this.v + visibleArea.y(),
				rightEdge - visibleArea.x(),
				bottomEdge - visibleArea.y(),
				this.textureWidth, this.textureHeight, RenderUtils.getRed(color), RenderUtils.getGreen(color), RenderUtils.getBlue(color), RenderUtils.getAlpha(color));


		if(this.blend) {
			RenderSystem.disableBlend();
		}

		matrices.pop();
	}

	public static TextureComponent parse(Element element) {
		UIParsing.expectAttributes(element, "texture");
		var textureId = UIParsing.parseIdentifier(element.getAttributeNode("texture"));

		int u = 0, v = 0, regionWidth = 0, regionHeight = 0, textureWidth = 256, textureHeight = 256;
		if(element.hasAttribute("u")) {
			u = UIParsing.parseSignedInt(element.getAttributeNode("u"));
		}

		if(element.hasAttribute("v")) {
			v = UIParsing.parseSignedInt(element.getAttributeNode("v"));
		}

		if(element.hasAttribute("region-width")) {
			regionWidth = UIParsing.parseSignedInt(element.getAttributeNode("region-width"));
		}

		if(element.hasAttribute("region-height")) {
			regionHeight = UIParsing.parseSignedInt(element.getAttributeNode("region-height"));
		}

		if(element.hasAttribute("texture-width")) {
			textureWidth = UIParsing.parseSignedInt(element.getAttributeNode("texture-width"));
		}

		if(element.hasAttribute("texture-height")) {
			textureHeight = UIParsing.parseSignedInt(element.getAttributeNode("texture-height"));
		}

		return new IconComponent(new Identifier(textureId.getNamespace(), "textures/gui/" + textureId.getPath()), u, v, regionWidth, regionHeight, textureWidth, textureHeight);
	}

	@Override
	public void hidden(boolean hidden) {
		this.hidden = hidden;
	}

	@Override
	public boolean isHidden() {
		return hidden;
	}
}
