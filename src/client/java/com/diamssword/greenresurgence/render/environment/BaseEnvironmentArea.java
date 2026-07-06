package com.diamssword.greenresurgence.render.environment;

import net.minecraft.util.math.Box;

public abstract class BaseEnvironmentArea {
	private final Box box;
	private final String type;

	public BaseEnvironmentArea(Box box, String type) {
		this.box = box;
		this.type = type;
	}

	public Box getBox() {
		return box;
	}

	public String getType() {
		return type;
	}


	public abstract void insideZoneUpdate(long time, boolean isActiveZone);

	public abstract void outsideZoneUpdate(long time);

	public abstract void onDestroy();
}
