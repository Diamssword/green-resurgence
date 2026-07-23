package com.diamssword.greenresurgence.mixin;

import com.diamssword.greenresurgence.systems.attributs.Attributes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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


}