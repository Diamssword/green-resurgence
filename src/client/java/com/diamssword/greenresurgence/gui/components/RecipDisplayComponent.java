package com.diamssword.greenresurgence.gui.components;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.crafting.Collection;
import com.diamssword.greenresurgence.systems.crafting.IRecipe;
import com.diamssword.greenresurgence.systems.crafting.Recipes;
import com.diamssword.greenresurgence.systems.crafting.UniversalResource;
import com.eliotlash.mclib.math.functions.limit.Min;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.ui.util.UISounds;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RecipDisplayComponent extends BaseComponent {
    private final int slotSize = 18;
    public static final Identifier SLOT_TEXTURE = GreenResurgence.asRessource("textures/gui/highlight.png");
    private EventStream<RecipePicked> onPicked = RecipePicked.newPickStream();
    private IRecipe<UniversalResource> recipe;
    private UniversalResource hovered;
    private float time = 0;
    private ButtonComponent button;

    protected RecipDisplayComponent(Sizing size) {
        this.sizing(size);
    }

    public void setRecipe(IRecipe<UniversalResource> recipe) {
        this.recipe=recipe;
    }


    @Override
    public boolean canFocus(FocusSource source) {
        return source == FocusSource.MOUSE_CLICK;
    }


    @Override
    public void applySizing() {
        super.applySizing();


    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        return super.onMouseDown(mouseX, mouseY, button);
    }

    public EventSource<RecipePicked> onRecipePicked() {
        return this.onPicked.source();
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        if (!Screen.hasControlDown()) {
            this.time += delta;
        }
        var mc=MinecraftClient.getInstance();
        RenderSystem.enableDepthTest();

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        var matrices = context.getMatrices();
        matrices.push();
        matrices.translate(x, y, 0);
        drawResource(recipe.result(mc.player),context,6+(this.width/2), 3,true);
        hovered=null;
        var ingrs=recipe.ingredients(mc.player);
        var mod = (this.width-6)/ slotSize;
        int h= (int) (this.height*0.3f);
        for (int i = 0; i < ingrs.size(); i++) {
            var c=i%mod;
            var c1= i/mod;
            drawResource(ingrs.get(i),context,3+(c*slotSize),h+3+c1*slotSize,false);
            if(mouseX-x>=3+(c*slotSize) && mouseX-x<(3+(c*slotSize))+slotSize && mouseY-y>=h+3+c1*slotSize && mouseY-y<=(h+3+c1*slotSize)+slotSize)
                hovered=ingrs.get(i);
        }

        int i = 0;
        int j = 0;




        RenderSystem.disableBlend();
        matrices.pop();
    }

    protected void drawResource(UniversalResource resource, OwoUIDrawContext context, int x, int y,boolean big) {
        context.getMatrices().push();
        if(big) {
            context.getMatrices().scale(2f, 2f, 2f);
            x=x/4;
            y=y/4;
        }
        var type = resource.getType();
        if (type == UniversalResource.Type.item) {

            context.drawItem(resource.asItem(), x, y);
            context.drawItemInSlot(MinecraftClient.getInstance().textRenderer,resource.asItem(), x, y);
        } else if (type == UniversalResource.Type.itemtag) {
            context.drawItem(resource.getCurrentItem(time), x, y);
            context.drawItemInSlot(MinecraftClient.getInstance().textRenderer,resource.getCurrentItem(time),x,y);
        }
        context.getMatrices().pop();
    }

    public void drawTooltip(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        super.drawTooltip(context, mouseX, mouseY, partialTicks, delta);
        if(hovered!=null)
        {
            if(hovered.getType()== UniversalResource.Type.item ||hovered.getType()== UniversalResource.Type.itemtag)
                context.drawItemTooltip(MinecraftClient.getInstance().textRenderer, hovered.getCurrentItem(time),mouseX,mouseY);
        }
    }

    private boolean isOnTextField(double mouseX, double mouseY) {
        return mouseY < 10 && mouseX < this.width - 10;
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double amount) {
     return super.onMouseScroll(mouseX,mouseY,amount);
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
    }

    public static interface RecipePicked {
        boolean onPicked(IRecipe<UniversalResource> picked, Collection<IRecipe<UniversalResource>, UniversalResource> collection, Identifier collectionID);

        static EventStream<RecipePicked> newPickStream() {
            return new EventStream<>(subscribers -> (IRecipe<UniversalResource> picked, Collection<IRecipe<UniversalResource>, UniversalResource> collection, Identifier collectionID) -> {
                var anyTriggered = false;
                for (var subscriber : subscribers) {
                    anyTriggered |= subscriber.onPicked(picked, collection, collectionID);
                }
                return anyTriggered;
            });
        }
    }

}
