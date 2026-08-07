package com.diamssword.greenresurgence.entities.deployable;

import com.diamssword.greenresurgence.MEntities;
import com.diamssword.greenresurgence.systems.deployable.DeployableSubpart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DeployableSubEntity extends Entity {
	@Nullable
	public final DeployableSubpart part;

	public DeployableSubEntity(EntityType<?> type, World world) {
		super(type, world);
		this.part = null;
	}

	public DeployableSubEntity(DeployableSubpart part) {
		super(MEntities.DEPLOYABLE_SUB_BOX, part.controller.getWorld());
		this.part = part;
		var p = part.getWorldPos();
		this.setPos(p.x, p.y, p.z);
		this.setBoundingBox(part.getOrientedBox());
	}

	@Override
	protected void initDataTracker() {

	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {

	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {

	}
}
