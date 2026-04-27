package com.diamssword.greenresurgence.systems.environment;

import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.character.HealthManager;
import com.diamssword.greenresurgence.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

public class FogArea implements EffectArea {
	public Box box;
	public Vector3f fogColor;
	public Vector3f vignetteColor;
	public boolean bottomHeavy;

	public FogArea() {
		this.box = new Box(0, 0, 0, 0, 0, 0);
	}

	public FogArea(Vector3f fogColor, Vector3f vignetteColor, boolean bottomHeavy, Box box) {
		this.box = box;
		this.fogColor = fogColor;
		this.vignetteColor = vignetteColor;
		this.bottomHeavy = bottomHeavy;
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
		return "fog";
	}

	@Override
	public void tick(List<PlayerEntity> playerInside, World world) {
		playerInside.forEach(pl -> {
			var dt = pl.getComponent(Components.PLAYER_DATA);
			dt.healthManager.addRadiationAmount(HealthManager.radiationHealSpeed);

		});
	}

	@Override
	public NbtCompound toNBT() {
		var tag = new NbtCompound();
		tag.putString("key", key());
		tag.put("box", Utils.boxToNBT(getArea()));
		tag.put("fog", Utils.vecToNBT(fogColor));
		tag.put("vignette", Utils.vecToNBT(vignetteColor));
		tag.putBoolean("bottom", bottomHeavy);
		return tag;
	}

	@Override
	public EffectArea fromNBT(NbtCompound nbt) {
		box = Utils.boxFromNBT(nbt.getCompound("box"));
		fogColor = Utils.vecFromNBT(nbt.getCompound("fog"));
		vignetteColor = Utils.vecFromNBT(nbt.getCompound("vignette"));
		bottomHeavy = nbt.getBoolean("bottom");
		return this;
	}
}
