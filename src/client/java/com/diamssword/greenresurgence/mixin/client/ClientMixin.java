package com.diamssword.greenresurgence.mixin.client;

import com.diamssword.greenresurgence.containers.player.CustomPlayerInventory;
import com.diamssword.greenresurgence.render.CustomFont;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class ClientMixin {

	@Shadow
	public ClientPlayerEntity player;

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;isValidHotbarIndex(I)Z"), method = "doItemPick")
	private boolean doItemPick(int slot) {
		return slot >= 0 && slot < CustomPlayerInventory.getHotbarSlotCount(player);
	}

	@Inject(at = @At("TAIL"), method = "handleInputEvents")
	private void onInputHandle(CallbackInfo ci) {
		var inv = player.getInventory();
		var max = CustomPlayerInventory.getHotbarSlotCount(player);
		if (inv.selectedSlot >= max)
			inv.selectedSlot = max - 1;
	}

	@Final
	@Shadow
	private FontManager fontManager;

	public FontManager getFontManager() {
		return fontManager;
	}

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;initFont(Z)V", shift = At.Shift.AFTER))
	private void createTTFRenderer(RunArgs args, CallbackInfo ci) {
		CustomFont.initTextRenderer();
	}
}
