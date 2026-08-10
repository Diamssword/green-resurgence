package com.diamssword.greenresurgence.systems.character.customPoses;

import com.diamssword.greenresurgence.systems.Components;
import net.minecraft.entity.player.PlayerEntity;

public class CarryingPose implements IPlayerCustomPose {


	private final PlayerEntity player;

	public CarryingPose(PlayerEntity player) {
		this.player = player;
	}

	@Override
	public boolean shouldExitPose() {

		return !player.getComponent(Components.PLAYER_DATA).isCarryingEntity();
	}

	@Override
	public int priority() {
		return 1000;
	}

	@Override
	public PoseType getPoseType() {
		return PoseType.ARMS;
	}
}
