package com.diamssword.greenresurgence.gui.components.hud;

import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ItemTooltipComponent extends BaseComponent implements IHideableComponent {

    private int heldItemTooltipFade;
    private ItemStack currentStack= ItemStack.EMPTY;
    private boolean hidden;

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        if(hidden)
            return;
        renderHeldItemTooltip(context);
    }
    @Override
    public void hidden(boolean hidden) {
        this.hidden=hidden;
    }

    @Override
    public boolean isHidden() {
        return this.hidden;
    }
    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return super.determineHorizontalContentSize(sizing);
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return MinecraftClient.getInstance().textRenderer.fontHeight+4;
    }

    public void renderHeldItemTooltip(OwoUIDrawContext context) {
        MinecraftClient client=MinecraftClient.getInstance();
        client.getProfiler().push("selectedItemName");
        if (this.heldItemTooltipFade > 0 && !this.currentStack.isEmpty()) {
            MutableText mutableText = Text.empty().append(this.currentStack.getName()).formatted(this.currentStack.getRarity().formatting);
            if (this.currentStack.hasCustomName()) {
                mutableText.formatted(Formatting.ITALIC);
            }

            int i = client.textRenderer.getWidth(mutableText);
            int l = (int)((float)this.heldItemTooltipFade * 256.0F / 10.0F);
            if (l > 255) {
                l = 255;
            }
            var center=this.x+(this.width/2);
            if (l > 0) {
                context.fill(center-(i/2)-2, this.y, center + (i/2) + 2, this.y + client.textRenderer.fontHeight + 2, client.options.getTextBackgroundColor(0));
                context.drawCenteredTextWithShadow(client.textRenderer, mutableText, center, this.y+2, 16777215 + (l << 24));
            }
        }

        client.getProfiler().pop();
    }

    public void tick()
    {
        MinecraftClient client=MinecraftClient.getInstance();
        if (client.player != null) {
            ItemStack itemStack = client.player.getInventory().getMainHandStack();
            if (itemStack.isEmpty()) {
                heldItemTooltipFade = 0;
            } else if (this.currentStack.isEmpty() || !itemStack.isOf(this.currentStack.getItem()) || !itemStack.getName().equals(this.currentStack.getName())) {
                this.heldItemTooltipFade = (int)(40.0 * client.options.getNotificationDisplayTime().getValue());
            } else if (this.heldItemTooltipFade > 0) {
                this.heldItemTooltipFade--;
            }

            this.currentStack = itemStack;
        }
    }
}
