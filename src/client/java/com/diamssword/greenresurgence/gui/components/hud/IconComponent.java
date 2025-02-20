package com.diamssword.greenresurgence.gui.components.hud;

import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.PositionedRectangle;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.util.Identifier;
import org.w3c.dom.Element;

import java.util.Map;

public class IconComponent extends TextureComponent implements IHideableComponent{
    private boolean hidden;

    protected IconComponent(Identifier texture, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        super(texture, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
    }
    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        if(this.hidden)
            return;
        super.draw(context,mouseX,mouseY,partialTicks,delta);
    }
    public static TextureComponent parse(Element element) {
        UIParsing.expectAttributes(element, "texture");
        var textureId = UIParsing.parseIdentifier(element.getAttributeNode("texture"));

        int u = 0, v = 0, regionWidth = 0, regionHeight = 0, textureWidth = 256, textureHeight = 256;
        if (element.hasAttribute("u")) {
            u = UIParsing.parseSignedInt(element.getAttributeNode("u"));
        }

        if (element.hasAttribute("v")) {
            v = UIParsing.parseSignedInt(element.getAttributeNode("v"));
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

        return new IconComponent(new Identifier(textureId.getNamespace(),"textures/gui/"+textureId.getPath()), u, v, regionWidth, regionHeight, textureWidth, textureHeight);
    }

    @Override
    public void hidden(boolean hidden) {
        this.hidden=hidden;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }
}
