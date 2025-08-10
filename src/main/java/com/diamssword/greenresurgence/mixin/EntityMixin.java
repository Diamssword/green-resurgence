package com.diamssword.greenresurgence.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Inject(at = @At("HEAD"), method = "updatePassengerPosition(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity$PositionUpdater;)V", cancellable = true)
	public void updatePassengerPos(Entity passenger, Entity.PositionUpdater positionUpdater, CallbackInfo ci) {
		var ent = (Entity) (Object) this;
		if (ent instanceof PlayerEntity pl && passenger instanceof PlayerEntity) {
			if (pl.hasPassenger(passenger)) {
				double d = ent.getY() + ent.getMountedHeightOffset() + passenger.getHeightOffset();
				double yawRadians = Math.toRadians(MathHelper.lerpAngleDegrees(0, pl.prevBodyYaw, pl.bodyYaw));

				double forwardX = -Math.sin(yawRadians) * 0.2;
				double forwardZ = Math.cos(yawRadians) * 0.2;

				// Calculate side offset (perpendicular to yaw)
				double sideX = Math.cos(yawRadians) * 0.5f;
				double sideZ = Math.sin(yawRadians) * 0.5f;

				// Combine forward and side offsets
				double offsetX = forwardX + sideX;
				double offsetZ = forwardZ + sideZ;
				positionUpdater.accept(passenger, ent.getX() + offsetX, d, ent.getZ() + offsetZ);
				ci.cancel();
			}
		}
	}
}