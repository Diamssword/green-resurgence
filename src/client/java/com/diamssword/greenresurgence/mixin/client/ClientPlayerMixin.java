package com.diamssword.greenresurgence.mixin.client;

import com.diamssword.greenresurgence.systems.Components;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerMixin extends LivingEntity {

	protected ClientPlayerMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "tickMovement", at = @At(value = "TAIL", target = "Lnet/minecraft/entity/Entity;isTouchingWater()Z", ordinal = 0))
	public void tickMovement(CallbackInfo ci) {
		var comp = this.getComponent(Components.PLAYER_DATA);
		if (comp.healthManager.isEnergyBurnout())
			this.setSprinting(false);
	}
}
