package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blocks.ItemBlock;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import com.diamssword.greenresurgence.gui.components.ButtonInventoryComponent;
import com.diamssword.greenresurgence.items.BlockVariantItem;
import com.diamssword.greenresurgence.systems.crafting.*;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;

public class BlockVariantScreen extends MultiInvHandledScreen<BlockVariantItem.Container,FlowLayout> {
    private boolean onWindow=false;
    private BlockVariantItem parent;
    private ButtonInventoryComponent comp;

    public BlockVariantScreen(BlockVariantItem.Container handler, PlayerInventory inv, Text title) {
        super(handler,FlowLayout.class, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource("block_variant")));
        for (ItemStack handItem : MinecraftClient.getInstance().player.getHandItems()) {
            if(handItem.getItem() instanceof BlockVariantItem it)
            {
                this.parent=it;
                break;
            }
        }
    }
        private static  final String[] colors= GenericBlocks.allColors("");
    @Override
    protected void build(FlowLayout rootComponent) {
        if(this.parent !=null)
        {
            comp=rootComponent.childById(ButtonInventoryComponent.class,"main");
            var coll=new RecipeCollection(new Identifier("minecraft:void"));
            parent.getVariants().stream().sorted((c1, c2) -> {
                String cl1=null;
                String cl2=null;
                for (String color : colors) {
                    if (c1.getPath().contains(color)) {
                        cl1=color;
                        break;
                    }
                }
                for (String color : colors) {
                    if (c2.getPath().contains(color)) {
                       cl2=color;
                       break;
                    }
                }
                if(cl1 !=null && cl2 !=null) {
                    var d = cl1.compareTo(cl2);
                    if(d==0)
                        return c1.getPath().compareTo(c2.getPath());
                    return d;
                }
             return c1.getPath().compareTo(c2.getPath());
            }).forEach(v-> coll.add(new SimpleRecipe(v)));

            comp.onRecipePicked().subscribe((v,v1,v2)-> onPick(v));
            comp.setCollection(coll,GreenResurgence.asRessource("air"));
        }
    }
    @Override
    protected void drawBackground(DrawContext ctx, float delta, int mouseY, int mouseX) {

    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode ==69) //desactive le 'e' qui ferme le gui
            return false;
        this.mouseClicked(comp.x(),comp.y(),1);
        return comp.onKeyPress(keyCode,scanCode,modifiers)||super.keyPressed(keyCode,scanCode,modifiers);
    }
    private boolean onPick(SimpleRecipe re)
    {
        var st=re.result(client.player);
        if((InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT)))
        {
            int i=this.handler.getPlayerInventory().getEmptySlot();
            if(i>-1)
            {
                this.handler.getPlayerInventory().setStack(i,st.asItem().copyWithCount(64));
                this.client.interactionManager.clickCreativeStack(st.asItem().copyWithCount(64), i);
            }

        }
        else {
            this.handler.setCursorStack(st.asItem());
        }
        onWindow = true;
        return true;
    }
    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        if (slot != null) {
            slotId = slot.id;
        }
        if(slotId>-1 && !this.handler.getCursorStack().isEmpty()) {
            var d=this.handler.getCursorStack();
            this.handler.setCursorStack(slot.getStack());
            this.client.interactionManager.clickCreativeStack(d, slotId + 9);

        }
        else if(slotId>-1) {
            var d=slot.getStack();
            slot.setStack(this.handler.getCursorStack());
            this.handler.setCursorStack(d);

        }
        else if(!onWindow) {
            super.onMouseClick(slot,slotId,button,actionType);
        }
        onWindow=false;
     //   this.client.interactionManager.clickSlot(((net.minecraft.screen.ScreenHandler)this.handler).syncId, slotId, button, actionType, this.client.player);
    }
}