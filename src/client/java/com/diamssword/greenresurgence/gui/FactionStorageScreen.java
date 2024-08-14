package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blocks.CrafterBlock;
import com.diamssword.greenresurgence.gui.components.ButtonInventoryComponent;
import com.diamssword.greenresurgence.gui.components.RecipDisplayComponent;
import com.diamssword.greenresurgence.network.CraftPackets;
import com.diamssword.greenresurgence.systems.crafting.Recipes;
import com.diamssword.greenresurgence.systems.crafting.UniversalResource;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionTerrainStorage;
import com.mojang.datafixers.util.Pair;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class FactionStorageScreen extends MultiInvHandledScreen<FactionTerrainStorage.ScreenHandler,FlowLayout> {
    public FactionStorageScreen(FactionTerrainStorage.ScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler,FlowLayout.class, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource("faction_storage")));

    }

    @Override
    protected void build(FlowLayout rootComponent) {

    }

    @Override
    protected void drawBackground(DrawContext ctx, float delta, int mouseY, int mouseX) {
        //if(window!=null)
        //   onWindow= this.window.isInBoundingBox(mouseX,mouseY);
    }
    @Override
    protected void drawSlot(DrawContext context, Slot slot, String inventory) {
        Pair pair;
        var pos=getSlotPosition(slot,inventory);
        ItemStack itemStack = slot.getStack();
        boolean bl = false;
        boolean bl2 = slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && !this.touchIsRightClickDrag;
        ItemStack itemStack2 = this.handler.getCursorStack();
        String string = null;
        if (slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && this.touchIsRightClickDrag && !itemStack.isEmpty()) {
            itemStack = itemStack.copyWithCount(itemStack.getCount() / 2);
        } else if (this.cursorDragging && this.cursorDragSlots.contains(slot) && !itemStack2.isEmpty()) {
            if (this.cursorDragSlots.size() == 1) {
                return;
            }
            if (ScreenHandler.canInsertItemIntoSlot((Slot)slot, (ItemStack)itemStack2, (boolean)true) && this.handler.canInsertIntoSlot(slot)) {
                bl = true;
                int k = Math.min(itemStack2.getMaxCount(), slot.getMaxItemCount(itemStack2));
                int l = slot.getStack().isEmpty() ? 0 : slot.getStack().getCount();
                int m = ScreenHandler.calculateStackSize(this.cursorDragSlots, (int)this.heldButtonType, (ItemStack)itemStack2) + l;
                if (m > k) {
                    m = k;
                    string = Formatting.YELLOW.toString() + k;
                }
                itemStack = itemStack2.copyWithCount(m);
            } else {
                this.cursorDragSlots.remove(slot);
                this.calculateOffset();
            }
        }
        context.getMatrices().push();
        context.getMatrices().translate(0.0f, 0.0f, 100.0f);
        if (itemStack.isEmpty() && slot.isEnabled() && (pair = slot.getBackgroundSprite()) != null) {
            Sprite sprite = this.client.getSpriteAtlas((Identifier)pair.getFirst()).apply((Identifier)pair.getSecond());
            context.drawSprite(pos.getFirst(), pos.getSecond(), 0, 16, 16, sprite);
            bl2 = true;
        }
        if (!bl2) {
            if (bl) {
                context.fill(pos.getFirst(), pos.getSecond(), pos.getFirst()+ 16,pos.getSecond() + 16, -2130706433);
            }
            context.drawItem(itemStack, pos.getFirst(), pos.getSecond(), pos.getFirst() + pos.getSecond() * this.backgroundWidth);
            if(string!=null)
                drawItemInSlot(context, itemStack, pos.getFirst(), pos.getSecond(), string);
            else if(!itemStack.isEmpty() && itemStack.getCount()>1)
                RessourceGuiHelper.drawRessourceExtra(context, UniversalResource.fromItemOpti(itemStack),pos.getFirst(),pos.getSecond(),0,16777215);
        }
        context.getMatrices().pop();
    }
    public void drawItemInSlot(DrawContext ctx, ItemStack stack, int x, int y, @Nullable String countOverride) {
        if (!stack.isEmpty()) {
            ctx.getMatrices().push();
            if (stack.getCount() != 1 || countOverride != null) {
                String string = countOverride == null ? RessourceGuiHelper.getCountDisplay(stack.getCount(),false) : countOverride;
                ctx.getMatrices().translate(0.0F, 0.0F, 200.0F);
                ctx.drawText(textRenderer, string, x + 19 - 2 - textRenderer.getWidth(string), y + 6 + 3, 16777215, true);
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

            ClientPlayerEntity clientPlayerEntity = this.client.player;
            float f = clientPlayerEntity == null ? 0.0F : clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), this.client.getTickDelta());
            if (f > 0.0F) {
                k = y + MathHelper.floor(16.0F * (1.0F - f));
                l = k + MathHelper.ceil(16.0F * f);
                ctx.fill(RenderLayer.getGuiOverlay(), x, k, x + 16, l, Integer.MAX_VALUE);
            }

            ctx.getMatrices().pop();
        }
    }
}