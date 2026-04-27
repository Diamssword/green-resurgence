package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.network.*;

public class ClientNetwork {
	public static void initialize() {
		ClientGuiPacket.init();
		ClientComesticsPacket.init();
		ClientGuildPackets.init();
		ClientNotificationPackets.init();
		EnvironmentPacketClient.init();
	}
}
