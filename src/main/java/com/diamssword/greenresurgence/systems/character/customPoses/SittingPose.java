package com.diamssword.greenresurgence.systems.character.customPoses;

import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.PosesPackets;
import com.diamssword.greenresurgence.systems.character.PosesManager;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class SittingPose implements IPlayerCustomPose {
	public final PlayerEntity player;

	public SittingPose(PlayerEntity player) {
		this.player = player;
	}

	@Override
	public void tick() {
		if(player.getWorld().isClient) {
			if(shouldExitPose()) {
				Channels.MAIN.clientHandle().send(new PosesPackets.EmoteRequest(PosesManager.SIT_EMOTE, true));
			}
		}
	}

	@Override
	public EntityDimensions changeHitBox(EntityDimensions baseDimension) {
		return EntityDimensions.fixed(baseDimension.width, baseDimension.height / 2f);
	}

	@Override
	public boolean shouldExitPose() {
		Vec3d velocity = player.getVelocity();
		double horizontalSpeed = Math.sqrt(
				velocity.x * velocity.x +
						velocity.z * velocity.z
		);
		return horizontalSpeed > 0.2f * 0.2f;

	}

	@Override
	public PoseType getPoseType() {
		return PoseType.LEGS;
	}
}
