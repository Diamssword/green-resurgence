package com.diamssword.greenresurgence.systems.environment;

import com.diamssword.greenresurgence.systems.Components;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class SimpleZoneArea implements EffectArea {
	public Box box;
	public float contamination;

	public SimpleZoneArea() {
		this.box = new Box(0, 0, 0, 0, 0, 0);
	}

	public SimpleZoneArea(Box box) {
		this.box = box;
	}

	@Override
	public Box getArea() {
		return box;
	}

	@Override
	public void setArea(Box box) {
		this.box = box;
	}

	@Override
	public String key() {
		return "zone";
	}

	@Override
	public void tick(List<PlayerEntity> playerInside, World world) {
		playerInside.forEach(pl -> {
			var cont = contaminationPerTick(pl.getPos(), world);
			if(cont != 0) {
				if(pl instanceof ServerPlayerEntity pl1 && pl1.interactionManager.getGameMode().isSurvivalLike()) {
					var dt = pl.getComponent(Components.PLAYER_DATA);
					dt.healthManager.addContaminationMitigated(cont);
				}
			}

		});
	}

	@Override
	public NbtCompound toNBT() {
		var tag = new NbtCompound();
		tag.putFloat("contamination", contamination);
		return tag;
	}

	@Override
	public EffectArea fromNBT(NbtCompound nbt) {
		contamination = nbt.getFloat("contamination");
		return this;
	}

	@Override
	public float contaminationPerTick(Vec3d pos, World world) {
		return contamination / 20f;
	}
}
