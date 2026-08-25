package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.structure.StructureInfos;
import com.diamssword.greenresurgence.utils.ClientSideHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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

	@Override
	public @Nullable World getMainWorld() {
		return MinecraftClient.getInstance().world;
	}

	@Override
	public void clearCaches() {
		StructureInfos.clearCache();
	}
}