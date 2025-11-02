package com.diamssword.greenresurgence.mixin;

import com.diamssword.greenresurgence.GreenResurgence;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//Disabled Mixin 
//@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

//	@Inject(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
/*	private void onAddStatusEffect(StatusEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir) {
		if(effect.getEffectType() == GreenResurgence.CONQUEST_SLOWNESS) {
			// Block it or replace it
			cir.setReturnValue(false); // prevents application
		}
	}*/
}