package com.diamssword.greenresurgence.mixin;

import com.diamssword.greenresurgence.systems.Components;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin {
	@Final
	@Shadow
	PlayerEntity owner;
	@Inject(at=@At("HEAD"),method = "onContentChanged")
	public void onContentChanged(Inventory inventory, CallbackInfo ci)
	{
		owner.playerScreenHandler.updateToClient();
	}
}