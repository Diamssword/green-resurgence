package com.diamssword.greenresurgence.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.mixin.ui.access.ButtonWidgetAccessor;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.ui.util.NinePatchTexture;
import io.wispforest.owo.ui.util.UISounds;
import org.w3c.dom.Element;

import java.util.Map;
import java.util.function.Consumer;

public class ClickableLayoutComponent extends FlowLayout {
    private Surface surface2;
    private Consumer<ClickableLayoutComponent> onPress=v->{};
    protected ClickableLayoutComponent(Sizing horizontalSizing, Sizing verticalSizing, Algorithm algorithm) {
        super(horizontalSizing, verticalSizing, algorithm);
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
        UIParsing.apply(children, "surface2", Surface::parse, this::surface2);

    }

    @Override
    protected void updateLayout() {
        super.updateLayout();
        this.children.forEach(v-> v.cursorStyle(this.cursorStyle));
    }

    public ClickableLayoutComponent onPress(Consumer<ClickableLayoutComponent> onPress) {
        this.onPress=onPress;
        return this;
    }
    public ParentComponent surface2(Surface surface) {
        this.surface2 = surface;
        return this;
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        if(mouseX >=this.x && mouseY >=this.y && mouseX<= this.x+width && mouseY<= this.y+height &&  this.surface2 !=null)
            this.surface2.draw(context, this);
        else
            this.surface.draw(context,this);
        this.drawChildren(context, mouseX, mouseY, partialTicks, delta, this.children);
    }
    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        super.onMouseDown(mouseX, mouseY, button);
        UISounds.playButtonSound();
       if(onPress !=null)
           onPress.accept(this);
        return true;
    }
    @Override
    public boolean canFocus(FocusSource source) {
        return source == FocusSource.MOUSE_CLICK;
    }
    public static FlowLayout parse(Element element) {
        UIParsing.expectAttributes(element, "direction");

        return switch (element.getAttribute("direction")) {
            case "horizontal" -> new ClickableLayoutComponent(Sizing.content(),Sizing.content(),Algorithm.HORIZONTAL );
            case "ltr-text-flow" -> new ClickableLayoutComponent(Sizing.content(),Sizing.content(),Algorithm.LTR_TEXT );
            default -> new ClickableLayoutComponent(Sizing.content(),Sizing.content(),Algorithm.VERTICAL );
        };
    }
}
