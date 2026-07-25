package com.diamssword.greenresurgence.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerMixin {
	@Inject(at = @At("HEAD"), method = "changeGameMode")
	public void onContentChanged(GameMode gameMode, CallbackInfoReturnable<Boolean> cir) {
		((ServerPlayerEntity) (Object) this).playerScreenHandler.updateToClient();
	}
}
