package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.network.GuiPackets;
import net.minecraft.server.network.ServerPlayerEntity;

public interface IGuiPacketReceiver {
	public void receiveGuiPacket(ServerPlayerEntity player, GuiPackets.GuiTileValue msg);
}
