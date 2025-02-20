package com.diamssword.greenresurgence.mixin;

import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.PosesPackets;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.character.stats.StatsModifiers;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Inject(at=@At("HEAD"),method = "updatePassengerPosition(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity$PositionUpdater;)V",cancellable = true)
	public void updatePassengerPos(Entity passenger, Entity.PositionUpdater positionUpdater, CallbackInfo ci)
	{
		var ent=(Entity)(Object) this;
		if(ent instanceof  PlayerEntity pl && passenger instanceof  PlayerEntity)
		{
			if (pl.hasPassenger(passenger)) {
				double d = ent.getY() + ent.getMountedHeightOffset() + passenger.getHeightOffset();
				double yawRadians = Math.toRadians(MathHelper.lerpAngleDegrees(0, (float)pl.prevBodyYaw, pl.bodyYaw));

				double forwardX = -Math.sin(yawRadians) *0.2;
				double forwardZ = Math.cos(yawRadians) *  0.2;

				// Calculate side offset (perpendicular to yaw)
				double sideX = Math.cos(yawRadians) *  0.5f;
				double sideZ = Math.sin(yawRadians) *  0.5f;

				// Combine forward and side offsets
				double offsetX = forwardX + sideX;
				double offsetZ = forwardZ + sideZ;
				positionUpdater.accept(passenger, ent.getX()+offsetX , d, ent.getZ()+offsetZ);
				ci.cancel();
			}
		}
	}
}