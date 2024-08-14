package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blocks.CrafterBlock;
import com.diamssword.greenresurgence.blocks.ItemBlock;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import com.diamssword.greenresurgence.gui.components.ButtonInventoryComponent;
import com.diamssword.greenresurgence.gui.components.RecipDisplayComponent;
import com.diamssword.greenresurgence.items.BlockVariantItem;
import com.diamssword.greenresurgence.network.CraftPackets;
import com.diamssword.greenresurgence.systems.crafting.*;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Comparator;

public class CrafterScreen extends MultiInvHandledScreen<CrafterBlock.ScreenHandler,FlowLayout> {
    public CrafterScreen(CrafterBlock.ScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler,FlowLayout.class, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource("crafter")));

    }

    @Override
    protected void build(FlowLayout rootComponent) {
        var ls=rootComponent.childById(ButtonInventoryComponent.class,"list");
        var disp=rootComponent.childById(RecipDisplayComponent.class,"display");
        rootComponent.childById(ButtonComponent.class,"craft").onPress(v->{
            var r=disp.getRecipe();
            if(r!=null)
            {
                if(disp.getStatus() ==null || disp.getStatus().canCraft)
                    CraftPackets.sendCraftRequest(r, this.handler.getPos());
            }
        });
        ls.onRecipePicked().subscribe((v,a,b)->{
            disp.setRecipe(v);
            CraftPackets.requestStatus(v,this.handler.getPos(), disp::setCraftingStatus);
            return true;
        });
        this.handler.onReady(un->{
            Recipes.get(ls.collectionID).ifPresent(v->{
                var ls1=v.getRecipes(this.client.player);
                if(!ls1.isEmpty())
                {
                var r=ls1.get(0);
                    disp.setRecipe(r);
                    CraftPackets.requestStatus(r,this.handler.getPos(), disp::setCraftingStatus);
                }
            });
        });


    }

    @Override
    protected void drawBackground(DrawContext ctx, float delta, int mouseY, int mouseX) {
        //if(window!=null)
        //   onWindow= this.window.isInBoundingBox(mouseX,mouseY);
    }

}