package com.diamssword.greenresurgence.gui.components;

import com.diamssword.greenresurgence.gui.RessourceGuiHelper;
import com.diamssword.greenresurgence.systems.crafting.*;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

import java.util.Map;

public class RecipDisplayComponent extends BaseComponent {
    private final int slotSize = 18;

    private SimpleRecipe recipe;
    private UniversalResource hovered;
    private CraftingResult result;
    private float time = 0;
    public void setCraftingStatus(CraftingResult status)
    {
        if(recipe !=null )
            result=status;

    }
    public CraftingResult getStatus()
    {
        return this.result;
    }
    public SimpleRecipe getRecipe() {
        return recipe;
    }
    protected RecipDisplayComponent(Sizing size) {
        this.sizing(size);
    }

    public void setRecipe(SimpleRecipe recipe) {
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
        if(recipe!=null) {

            hovered = null;
            drawResource(recipe.result(mc.player), context, 6 + (this.width / 2), 3, true);
            if (mouseX - x >= 4 + (this.width / 2)-(slotSize) && mouseX - x < 4 + (this.width / 2) + (slotSize/2) && mouseY - y >= 3 && mouseY - y <= 3 + slotSize*2)
                hovered = recipe.result(mc.player);
            var ingrs = recipe.ingredients(mc.player);
            var mod = (this.width - 6) / slotSize;
            int h = (int) (slotSize*2)-6;
            for (int i = 0; i < ingrs.size(); i++) {
                var c = i % mod;
                var c1 = i / mod;
                drawResource(ingrs.get(i), context, 3 + (c * slotSize), h + 3 + c1 * slotSize, false);
                if (mouseX - x >= 3 + (c * slotSize) && mouseX - x < (3 + (c * slotSize)) + slotSize && mouseY - y >= h + 3 + c1 * slotSize && mouseY - y <= (h + 3 + c1 * slotSize) + slotSize)
                    hovered = ingrs.get(i);
            }
        }
        RenderSystem.disableBlend();
        matrices.pop();
    }

    protected void drawResource(UniversalResource resource, OwoUIDrawContext context, int x, int y,boolean big) {
        context.getMatrices().push();
        if(big) {
            context.getMatrices().scale(2f, 2f, 2f);
            context.getMatrices().translate(0,0,1);
            x=x/4;
            y=y/4;
        }
        var color=16777215;
        if(result !=null) {
            var b = result.stacks.get(resource);
            if (b != null)
                color = b ? Color.GREEN.argb() : Color.RED.argb();
        }
        RessourceGuiHelper.drawRessource(context,resource,x,y,time);
        RessourceGuiHelper.drawRessourceExtra(context,resource,x,y,time,color);
        context.getMatrices().pop();
    }

    public void drawTooltip(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        super.drawTooltip(context, mouseX, mouseY, partialTicks, delta);
        RessourceGuiHelper.drawTooltip(context,hovered,mouseX,mouseY,time);
    }
    public void drawItemInSlot(OwoUIDrawContext ctx, ItemStack stack, int x, int y,int color, @Nullable String countOverride) {
        var textRenderer=MinecraftClient.getInstance().textRenderer;
        if (!stack.isEmpty()) {
            ctx.getMatrices().push();
            if (stack.getCount() != 0 || countOverride != null) {
                String string = countOverride == null ? String.valueOf(stack.getCount()) : countOverride;
                ctx.getMatrices().translate(0.0F, 0.0F, 200.0F);
                ctx.drawText(textRenderer, string, x + 19 - 2 - textRenderer.getWidth(string), y + 6 + 3, color, true);
            }
            int k;
            int l;
            if (stack.isItemBarVisible()) {
                int i = stack.getItemBarStep();
                int j = stack.getItemBarColor();
                k = x + 2;
                l = y + 13;
                ctx.fill(RenderLayer.getGuiOverlay(), k, l, k + 13, l + 2, -16777216);
                ctx.fill(RenderLayer.getGuiOverlay(), k, l, k + i, l + 1, j | -16777216);
            }

            ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
            float f = clientPlayerEntity == null ? 0.0F : clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(),  MinecraftClient.getInstance().getTickDelta());
            if (f > 0.0F) {
                k = y + MathHelper.floor(16.0F * (1.0F - f));
                l = k + MathHelper.ceil(16.0F * f);
                ctx.fill(RenderLayer.getGuiOverlay(), x, k, x + 16, l, Integer.MAX_VALUE);
            }

            ctx.getMatrices().pop();
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
}
