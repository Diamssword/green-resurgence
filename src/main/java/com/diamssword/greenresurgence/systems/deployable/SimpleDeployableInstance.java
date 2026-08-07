package com.diamssword.greenresurgence.systems.deployable;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class SimpleDeployableInstance extends AbstractDeployableInstance {
	private final DeployableSubpart mainBox;

	public SimpleDeployableInstance(String id, Box box, Vec3d offset) {
		super(id);
		this.mainBox = new DeployableSubpart(this, box, offset);
	}

	public SimpleDeployableInstance(String id, Box box) {
		this(id, box, Vec3d.ZERO);
	}

	@Override
	public Box getMainBox() {
		return mainBox.getOrientedAndOffsetedBox();
	}

	@Override
	public boolean isSinglePart() {
		return true;
	}

	@Override
	public boolean isCollidable() {
		return true;
	}
}
