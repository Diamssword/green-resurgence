package com.diamssword.greenresurgence.systems.character.customPoses;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.player.PlayerEntity;

public interface IPlayerCustomPose {
	default void tick(PlayerEntity player) {

	}

	default public EntityDimensions changeHitBox(PlayerEntity player, EntityDimensions baseDimension) {
		return baseDimension;
	}

	public boolean shouldExitPose(PlayerEntity player);

	default public int priority() {
		return 0;
	}

}
