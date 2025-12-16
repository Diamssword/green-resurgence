package com.diamssword.greenresurgence.utils;

import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

public class ClientSideHelper {
	public boolean isShiftPressed() {
		return false;
	}

	public boolean isClient() {
		return false;
	}

	@Nullable
	public PlayerEntity getPlayer() {
		return null;
	}
}
