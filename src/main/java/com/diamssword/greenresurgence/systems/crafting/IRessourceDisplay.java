package com.diamssword.greenresurgence.systems.crafting;

import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface IRessourceDisplay {
	public UniversalResource getDisplay(@Nullable PlayerEntity player);
}
