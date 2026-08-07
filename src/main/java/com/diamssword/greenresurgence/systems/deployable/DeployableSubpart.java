package com.diamssword.greenresurgence.systems.deployable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class DeployableSubpart {
	private Box box;
	private Vec3d pos;
	private String interactName;
	public final AbstractDeployableInstance controller;

	public DeployableSubpart(AbstractDeployableInstance controller, Box box, Vec3d pos) {
		this.controller = controller;
		this.pos = pos;
		this.box = box;
	}

	public DeployableSubpart setInteractible(String interactName) {
		this.interactName = interactName;
		return this;
	}

	public Vec3d getWorldPos() {
		return controller.getPosition().add(pos.rotateY((float) Math.toRadians(-controller.getRotation()))
		);
	}

	public boolean onPlayerInteract(PlayerEntity user, Hand hand) {
		if(interactName != null)
			return controller.onPlayerInteractSubpart(interactName, user, hand);
		return false;
	}

	public Box getOrientedAndOffsetedBox() {
		return getOrientedBox().offset(pos);
	}

	public Box getOrientedBox() {
		return switch(controller.getDirection()) {
			case SOUTH -> box;

			case NORTH -> new Box(
					-box.maxX, box.minY, -box.maxZ,
					-box.minX, box.maxY, -box.minZ
			);

			case EAST -> new Box(
					-box.maxZ, box.minY, box.minX,
					-box.minZ, box.maxY, box.maxX
			);

			case WEST -> new Box(
					box.minZ, box.minY, -box.maxX,
					box.maxZ, box.maxY, -box.minX
			);

			default -> throw new IllegalArgumentException(
					"Direction must be horizontal"
			);
		};
	}
}
