package com.diamssword.greenresurgence.systems.character.customPoses;

import com.diamssword.greenresurgence.items.equipment.ICustomPoseWeapon;
import net.minecraft.entity.player.PlayerEntity;

public class TwoHandWield implements IPlayerCustomPose {

	private final PlayerEntity player;

	public TwoHandWield(PlayerEntity player) {
		this.player = player;
	}

	@Override
	public int priority() {
		return -1;
	}

	@Override
	public boolean shouldExitPose() {
		return !(player.getMainHandStack().getItem() instanceof ICustomPoseWeapon);
	}

	@Override
	public PoseType getPoseType() {
		return PoseType.ARMS;
	}
}
