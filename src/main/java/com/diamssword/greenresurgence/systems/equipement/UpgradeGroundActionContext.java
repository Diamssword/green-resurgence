package com.diamssword.greenresurgence.systems.equipement;

import net.minecraft.entity.Entity;

public class UpgradeGroundActionContext extends UpgradeActionContext {
	protected Entity source;

	public UpgradeGroundActionContext(Entity source, ItemContext context) {
		super(null, null, context, false);
		this.source = source;
	}

	public Entity getSource() {
		return source;
	}
}
