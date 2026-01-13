package com.diamssword.greenresurgence.systems.equipement.utils;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.Nullable;

public interface IFlashLightProvider {
	public boolean isLightOn(@Nullable Entity owner, @Nullable ItemStack stack);

	public Vec2f lightOffset();
}
