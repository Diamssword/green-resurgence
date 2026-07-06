package com.diamssword.greenresurgence.render.environment;

import com.diamssword.greenresurgence.utils.Utils;
import net.minecraft.nbt.NbtCompound;

public class NoEffectArea extends BaseEnvironmentArea {

	public NoEffectArea(String type, NbtCompound tag) {
		super(Utils.boxFromNBT(tag.getCompound("box")), type);
	}

	@Override
	public void insideZoneUpdate(long time, boolean isActiveZone) {

	}

	@Override
	public void outsideZoneUpdate(long time) {

	}

	@Override
	public void onDestroy() {

	}
}
