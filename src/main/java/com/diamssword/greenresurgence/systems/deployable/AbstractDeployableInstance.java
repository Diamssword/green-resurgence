package com.diamssword.greenresurgence.systems.deployable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class AbstractDeployableInstance {
	private Entity entity;
	public final String id;

	public AbstractDeployableInstance(String id) {
		this.id = id;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public boolean isReady() {
		return entity != null && entity.isAlive();
	}

	public Vec3d getPosition() {
		return getEntity().getPos();
	}

	public float getRotation() {
		return getEntity().getYaw();
	}

	public Entity getEntity() {
		return entity;
	}

	public World getWorld() {
		return getEntity().getWorld();
	}

	public boolean onPlayerInteractSubpart(String partName, PlayerEntity user, Hand hand) {
		return false;
	}

	public Direction getDirection() {
		return Direction.fromRotation(getRotation());
	}

	public abstract Box getMainBox();

	public abstract boolean isSinglePart();

	public abstract boolean isCollidable();
}
