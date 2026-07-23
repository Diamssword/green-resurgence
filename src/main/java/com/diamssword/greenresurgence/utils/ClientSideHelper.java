package com.diamssword.greenresurgence.utils;

import com.diamssword.greenresurgence.GreenResurgence;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

public class ClientSideHelper {
	private Boolean isDatagen = null;

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

	public boolean isDatagen() {
		if(isDatagen == null)
			isDatagen = GreenResurgence.ID.equals(System.getProperty("fabric-api.datagen.modid"));
		return isDatagen;
	}
}
