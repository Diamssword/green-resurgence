package com.diamssword.greenresurgence.containers.player;

import com.diamssword.greenresurgence.containers.IGridContainer;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;

public class VanillaPlayerInvMokup extends MultiInvScreenHandler {
    public VanillaPlayerInvMokup(int syncId, PlayerInventory playerInventory) {
        super(syncId, playerInventory);
    }

    public VanillaPlayerInvMokup(int syncId, PlayerInventory playerInventory, IGridContainer... inventories) {
        super(syncId, playerInventory, inventories);
    }

    @Override
    public ScreenHandlerType<? extends MultiInvScreenHandler> type() {
        return null;
    }
}
