package com.diamssword.greenresurgence.containers;

import net.minecraft.entity.RideableInventory;
import net.minecraft.entity.player.PlayerEntity;

public interface IOptionalInventory extends RideableInventory {
	public boolean hasInventory(PlayerEntity player);
}
