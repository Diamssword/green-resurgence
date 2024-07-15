package com.diamssword.greenresurgence.gui.components;

import com.diamssword.greenresurgence.GreenResurgence;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.mixin.ui.access.ButtonWidgetAccessor;
import io.wispforest.owo.mixin.ui.access.ClickableWidgetAccessor;
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
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Map;
import java.util.function.Consumer;

public class ArrowButtonComponent extends ButtonWidget {

    public static final Identifier TEXTURE = GreenResurgence.asRessource("textures/gui/arrow.png");
    public static final Identifier TEXTURE_D = GreenResurgence.asRessource("textures/gui/arrow_down.png");
    public boolean down=false;
    protected int color=0xffbb7d25;

    public ArrowButtonComponent(Consumer<ArrowButtonComponent> onPress) {
        super(0, 0, 0, 0, Text.empty(), button -> onPress.accept((ArrowButtonComponent) button), ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.sizing(Sizing.fixed(20));
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {

        RenderSystem.enableDepthTest();

        var texture = this.down?TEXTURE_D:TEXTURE;
        context.drawTexture(texture, this.getX(),this.getY(),0, this.hovered?this.height:0,this.width,this.height,this.width,this.height*2);
        var textRenderer = MinecraftClient.getInstance().textRenderer;
        var tooltip = ((ClickableWidgetAccessor) this).owo$getTooltip();
        if (this.hovered && tooltip != null)
            context.drawTooltip(textRenderer, tooltip.getLines(MinecraftClient.getInstance()), HoveredTooltipPositioner.INSTANCE, mouseX, mouseY);
    }

    public ArrowButtonComponent onPress(Consumer<ArrowButtonComponent> onPress) {
        ((ButtonWidgetAccessor) this).owo$setOnPress(button -> onPress.accept((ArrowButtonComponent) button));
        return this;
    }
    public ArrowButtonComponent setDown(boolean down)
    {
        this.down=down;
        return this;
    }

    public boolean active() {
        return this.active;
    }
    protected CursorStyle owo$preferredCursorStyle() {
        return CursorStyle.HAND;
    }
    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
        this.setDown("true".equals(element.getAttribute("down")));
    }

}
