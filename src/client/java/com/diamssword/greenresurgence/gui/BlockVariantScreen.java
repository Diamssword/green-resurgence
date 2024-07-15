package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blocks.ItemBlock;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import com.diamssword.greenresurgence.gui.components.ButtonInventoryComponent;
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

public class BlockVariantScreen extends MultiInvHandledScreen<BlockVariantItem.Container,FlowLayout> {
    private ButtonInventoryComponent window;
    private boolean onWindow=false;
    private BlockVariantItem parent;
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

    @Override
    protected void build(FlowLayout rootComponent) {
        if(this.parent !=null)
        {
            var comp=rootComponent.childById(ButtonInventoryComponent.class,"main");
            var coll=new Collection<SimpleRecipe,ItemResource>();
            parent.getVariants().stream().sorted(Comparator.comparing(Identifier::getPath)).forEach(v->{
                coll.add(new SimpleRecipe(v));
            });
            this.window=comp;
            comp.onRecipePicked().subscribe((v,v1,v2)-> onPick(v));
            comp.setCollection((Collection)coll,GreenResurgence.asRessource("air"));
        }
    }
    @Override
    protected void drawBackground(DrawContext ctx, float delta, int mouseY, int mouseX) {
        //if(window!=null)
        //   onWindow= this.window.isInBoundingBox(mouseX,mouseY);
    }

    private boolean onPick(IRecipe<IResource> re)
    {
        var st=re.result(client.player);
        if(st instanceof ItemResource re1) {
            this.handler.setCursorStack(re1.asItem());
            onWindow=true;
          //  this.client.interactionManager.clickCreativeStack(re1.asItem(), -999);
        }
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
    public static class ScreenHandler extends MultiInvScreenHandler {

        public ScreenHandler() {
            super(0, MinecraftClient.getInstance().player.getInventory());
        }

        @Override
        public ScreenHandlerType<ItemBlock.ScreenHandler> type() {
            return null;
        }

    }
}