package com.diamssword.greenresurgence.systems.character.customPoses;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.player.PlayerEntity;

public class BeingCarriedPose implements IPlayerCustomPose {

	private final PlayerEntity player;

	public BeingCarriedPose(PlayerEntity player) {
		this.player = player;
	}

	@Override
	public void tick() {

		var vh = player.getVehicle();
		if(vh instanceof PlayerEntity pl1) {

			player.bodyYaw = pl1.headYaw;
			player.prevYaw = pl1.prevYaw;
			player.setYaw(pl1.getYaw());
			player.prevBodyYaw = pl1.prevHeadYaw;

		}
	}

	@Override
	public EntityDimensions changeHitBox(EntityDimensions baseDimension) {

		return EntityDimensions.fixed(0.1f, 0.1f);
	}


	@Override
	public boolean shouldExitPose() {

		return player.getVehicle() == null;
	}

	@Override
	public int priority() {
		return 10000;
	}

	@Override
	public PoseType getPoseType() {
		return PoseType.BODY;
	}
}
