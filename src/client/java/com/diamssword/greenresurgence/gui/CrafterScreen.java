package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blocks.CrafterBlock;
import com.diamssword.greenresurgence.blocks.ItemBlock;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import com.diamssword.greenresurgence.gui.components.ButtonInventoryComponent;
import com.diamssword.greenresurgence.gui.components.RecipDisplayComponent;
import com.diamssword.greenresurgence.items.BlockVariantItem;
import com.diamssword.greenresurgence.systems.crafting.*;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Comparator;

public class CrafterScreen extends MultiInvHandledScreen<CrafterBlock.ScreenHandler,FlowLayout> {
    public CrafterScreen(CrafterBlock.ScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler,FlowLayout.class, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource("crafter")));

    }

    @Override
    protected void build(FlowLayout rootComponent) {
        var ls=rootComponent.childById(ButtonInventoryComponent.class,"list");
        var disp=rootComponent.childById(RecipDisplayComponent.class,"display");
        ls.onRecipePicked().subscribe((v,a,b)->{
            disp.setRecipe(v);
            return true;
        });
        Recipes.get(ls.collectionID).ifPresent(v->{
            disp.setRecipe(v.getRecipes(this.client.player).get(0));
        });

    }
    @Override
    protected void drawBackground(DrawContext ctx, float delta, int mouseY, int mouseX) {
        //if(window!=null)
        //   onWindow= this.window.isInBoundingBox(mouseX,mouseY);
    }

    private boolean onPick(IRecipe<UniversalResource> re)
    {
        var st=re.result(client.player);
            this.handler.setCursorStack(st.asItem());

        return true;
    }
}