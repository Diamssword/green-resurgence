package com.diamssword.greenresurgence.mixin.client;

import com.diamssword.greenresurgence.event.AttackBlockCallback;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class UserAttackBlockMixin {
	@Inject(at = @At("HEAD"), method = "attackBlock")
	private void attackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		AttackBlockCallback.EVENT.invoker().interact(pos,direction);
	}
}