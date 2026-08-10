package com.diamssword.greenresurgence.systems.character.customPoses;

import net.minecraft.entity.player.PlayerEntity;

public class PushingCartPose implements IPlayerCustomPose {

	private PlayerEntity player;

	public PushingCartPose(PlayerEntity player) {
		this.player = player;
	}

	@Override
	public boolean shouldExitPose() {

		return player.getVehicle() == null;
	}

	@Override
	public int priority() {
		return 1001;
	}

	@Override
	public PoseType getPoseType() {
		return PoseType.BODY;
	}
}
