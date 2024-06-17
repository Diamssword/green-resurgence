package com.diamssword.greenresurgence.gui.components;

import com.diamssword.greenresurgence.GreenResurgence;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.text.Text;
import org.w3c.dom.Element;

import java.util.Map;

import net.minecraft.util.Identifier;

public class InventoryComponent extends BaseComponent {
    public static final Identifier SLOT_TEXTURE= GreenResurgence.asRessource("textures/gui/slot.png");
    public final String inventoryId;
    public final String name;
    protected final AnimatableProperty<PositionedRectangle> visibleArea;
    private int regionWidth=18;
    private int regionHeight=18;
    protected boolean blend = false;
    protected InventoryComponent(String inventoryId,int width,int height,String name) {
        this.inventoryId=inventoryId;
        this.name=name;
        this.regionWidth=width*18;
        this.regionHeight=10+height*18;
        this.visibleArea = AnimatableProperty.of(PositionedRectangle.of(0, 0, this.regionWidth, this.regionHeight));

    }
    protected InventoryComponent(String inventoryId,int width,int height) {
        this(inventoryId,width,height,inventoryId);
    }
    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
      return this.regionWidth;
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return this.regionHeight;
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);

        this.visibleArea.update(delta);

    }
    public Text getInventoryName()
    {
        if(this.name.equals("disabled"))
            return null;
        if(this.inventoryId.equals("player"))
            return Text.translatable("container.inventory");
        else if(this.inventoryId.equals("hotbar") || this.name.isEmpty())
            return null;
        return Text.translatable(GreenResurgence.ID+".container."+this.name);
    }
    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        RenderSystem.enableDepthTest();

        if (this.blend) {
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
        Text name=getInventoryName();
        if(name!=null)
            context.drawText(name,visibleArea.x(),visibleArea.y(),0.9f,0xffffff);
        context.drawTexture(SLOT_TEXTURE,
                visibleArea.x()-1,
                visibleArea.y()+9,
                rightEdge - visibleArea.x(),
                bottomEdge - visibleArea.y()-10,
                visibleArea.x(),
                visibleArea.y(),
                rightEdge - visibleArea.x(),
                bottomEdge - visibleArea.y()-10,
                18,18
        );

        if (this.blend) {
            RenderSystem.disableBlend();
        }

        matrices.pop();
    }

    public InventoryComponent visibleArea(PositionedRectangle visibleArea) {
        this.visibleArea.set(visibleArea);
        return this;
    }


    public InventoryComponent blend(boolean blend) {
        this.blend = blend;
        return this;
    }

    public boolean blend() {
        return this.blend;
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        UIParsing.apply(children, "blend", UIParsing::parseBool, this::blend);

        if (children.containsKey("visible-area")) {
            var areaChildren = UIParsing.childElements(children.get("visible-area"));

            int x = 0, y = 0, width = this.regionWidth, height = this.regionHeight;
            if (areaChildren.containsKey("x")) {
                x = UIParsing.parseSignedInt(areaChildren.get("x"));
            }

            if (areaChildren.containsKey("y")) {
                y = UIParsing.parseSignedInt(areaChildren.get("y"));
            }

            if (areaChildren.containsKey("width")) {
                width = UIParsing.parseSignedInt(areaChildren.get("width"));
            }

            if (areaChildren.containsKey("height")) {
                height = UIParsing.parseSignedInt(areaChildren.get("height"));
            }

            this.visibleArea(PositionedRectangle.of(x, y, width, height));
        }
    }
    public static InventoryComponent parse(Element element) {
        UIParsing.expectAttributes(element, "id");
        UIParsing.expectAttributes(element, "width");
        UIParsing.expectAttributes(element, "height");
        var invId =element.getAttributeNode("id").getValue();
        var w=UIParsing.parseUnsignedInt(element.getAttributeNode("width"));
        var h=UIParsing.parseUnsignedInt(element.getAttributeNode("height"));
        if (element.hasAttribute("name")) {
            return new InventoryComponent(invId,w,h,element.getAttributeNode("name").getValue());
        }
        return new InventoryComponent(invId,w,h);
    }
}
