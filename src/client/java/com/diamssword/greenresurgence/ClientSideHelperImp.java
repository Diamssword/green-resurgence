package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.utils.ClientSideHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;

public class ClientSideHelperImp extends ClientSideHelper {
	@Override
	public boolean isShiftPressed() {
		return Screen.hasShiftDown();
	}

	@Override
	public boolean isClient() {
		return true;
	}

	@Override
	public PlayerEntity getPlayer() {
		return MinecraftClient.getInstance().player;
	}
}