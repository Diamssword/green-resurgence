package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.network.GuiPackets;
import net.minecraft.entity.player.PlayerEntity;

public interface IGuiPacketReceiver {
	public void receiveGuiPacket(PlayerEntity player, GuiPackets.GuiTileValue msg);
}
