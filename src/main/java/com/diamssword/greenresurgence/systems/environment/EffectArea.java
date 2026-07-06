package com.diamssword.greenresurgence.systems.environment;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public interface EffectArea {
	public Box getArea();

	public void setArea(Box box);

	public String key();

	public void tick(List<PlayerEntity> playerInside, World world);

	public NbtCompound toNBT();

	public EffectArea fromNBT(NbtCompound nbt);

	public float contaminationPerTick(Vec3d pos, World world);
}
