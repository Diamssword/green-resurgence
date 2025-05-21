package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.network.ClientComesticsPacket;
import com.diamssword.greenresurgence.network.ClientGuiPacket;
import com.diamssword.greenresurgence.network.ClientGuildPackets;

public class ClientNetwork {
	public static void initialize() {
		ClientGuiPacket.init();
		ClientComesticsPacket.init();
		ClientGuildPackets.init();
	}
}
