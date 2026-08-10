package com.diamssword.greenresurgence.systems.character.customPoses;

import net.minecraft.entity.EntityDimensions;

public interface IPlayerCustomPose {
	default void tick() {

	}

	default public EntityDimensions changeHitBox(EntityDimensions baseDimension) {
		return baseDimension;
	}

	public boolean shouldExitPose();

	default public boolean canBeStoppedByClient() {
		return false;
	}

	default public int priority() {
		return 0;
	}

	default public boolean canStillPlayWith(IPlayerCustomPose other) {
		if(this.getPoseType() == PoseType.BODY)
			return false;
		if(other.getPoseType() == PoseType.ARMS)
			return getPoseType() == PoseType.LEGS;
		if(other.getPoseType() == PoseType.LEGS)
			return getPoseType() == PoseType.ARMS;
		return false;
	}

	public PoseType getPoseType();

	public static enum PoseType {
		ARMS,
		LEGS,
		BODY
	}

}
