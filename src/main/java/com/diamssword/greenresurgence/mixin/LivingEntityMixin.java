package com.diamssword.greenresurgence.mixin;

import com.diamssword.greenresurgence.events.PlayerJumpEvent;
import com.diamssword.greenresurgence.systems.attributs.Attributes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {


	@Inject(method = "modifyAppliedDamage", at = @At(value = "RETURN"), cancellable = true)
	private void modifyDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
		if(source.isIn(DamageTypeTags.IS_FALL) && (Object) this instanceof PlayerEntity) {
			var val = ((LivingEntity) (Object) this).getAttributeValue(Attributes.FALL_DAMAGE_REDUCTION) / 100d;
			cir.setReturnValue(cir.getReturnValueF() * (1f - (float) val));
		}


	}

	@Inject(method = "jump", at = @At("HEAD"))
	private void onJump(CallbackInfo ci) {
		LivingEntity entity = (LivingEntity) (Object) this;

		if(entity instanceof ServerPlayerEntity player) {
			PlayerJumpEvent.onJump.invoker().onJump(player);
		}
	}

}