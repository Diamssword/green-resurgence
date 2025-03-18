package com.diamssword.greenresurgence.gui.components;

import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class ItemCooldownComponent extends ItemComponent {

    private float cooldown =0f;
    public ItemCooldownComponent(ItemStack stack) {
        super(stack);
    }

    public float getCooldown() {
        return cooldown;
    }

    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        super.draw(context, mouseX, mouseY, partialTicks, delta);
        if (cooldown > 0.0F) {
            int k = y + MathHelper.floor(16.0F * (1.0F - cooldown));
            int l = k + MathHelper.ceil(16.0F * cooldown);
            context.fill(RenderLayer.getGuiOverlay(), x, k, x + 16, l, Integer.MAX_VALUE);
        }
    }



}
