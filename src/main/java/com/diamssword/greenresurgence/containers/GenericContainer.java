package com.diamssword.greenresurgence.containers;

import com.diamssword.greenresurgence.containers.grids.IGridContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;

public class GenericContainer extends MultiInvScreenHandler {
	public GenericContainer(int syncId, PlayerInventory playerInventory) {
		super(syncId, playerInventory);
	}

	public GenericContainer(int syncId, PlayerEntity player, IGridContainer... inventories) {
		super(syncId, player, inventories);
	}

	public GenericContainer(int syncId, PlayerEntity player, boolean empty) {
		super(syncId, player, empty);
	}

	@Override
	public ScreenHandlerType<? extends MultiInvScreenHandler> type() {
		return Containers.GENERIC_CONTAINER;
	}
}
