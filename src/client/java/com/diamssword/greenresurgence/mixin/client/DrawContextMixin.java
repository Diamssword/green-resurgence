package com.diamssword.greenresurgence.mixin.client;

import com.diamssword.greenresurgence.items.helpers.ISecondaryDurabilityBar;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
	@Inject(at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/DrawContext;fill(Lnet/minecraft/client/render/RenderLayer;IIIII)V",
			ordinal = 1,
			shift = At.Shift.AFTER
	), method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
	public void updatePassengerPos(TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci) {
		if(stack.getItem() instanceof ISecondaryDurabilityBar sb) {
			if(sb.isSecondItemBarVisible(stack)) {
				int i = sb.getSecondItemBarStep(stack);
				int j = sb.getSecondItemBarColor(stack);
				int k = x + 2;
				int l = y + 11;
				((DrawContext) (Object) this).fill(RenderLayer.getGuiOverlay(), k, l, k + 13, l + 2, -16777216);
				((DrawContext) (Object) this).fill(RenderLayer.getGuiOverlay(), k, l, k + i, l + 1, j | 0xFF000000);
			}
		}

	}
}