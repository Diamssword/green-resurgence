package com.diamssword.greenresurgence.containers.player;

import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import com.diamssword.greenresurgence.containers.grids.IGridContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;

public class VanillaPlayerInvMokup extends MultiInvScreenHandler {
	public VanillaPlayerInvMokup(int syncId, PlayerInventory playerInventory) {
		super(syncId, playerInventory);
	}

	public VanillaPlayerInvMokup(int syncId, PlayerEntity player, IGridContainer... inventories) {
		super(syncId, player, inventories);
	}

	public VanillaPlayerInvMokup(int syncId, PlayerEntity player, boolean empty) {
		super(syncId, player, empty);

	}

	@Override
	public ScreenHandlerType<? extends MultiInvScreenHandler> type() {
		return Containers.PLAYER;
	}
}
