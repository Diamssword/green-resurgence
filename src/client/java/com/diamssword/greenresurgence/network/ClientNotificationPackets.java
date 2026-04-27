package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.gui.hud.ResurgenceToast;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayDeque;
import java.util.Queue;

public class ClientNotificationPackets {
	public static Queue<NotificationPackets.NotificationPacket> queue = new ArrayDeque<>();

	public static void init() {
		Channels.MAIN.registerClientbound(NotificationPackets.NotificationPacket.class, (msg, ctx) -> {
			queue.add(msg);
			MinecraftClient.getInstance().getToastManager().add(new ResurgenceToast(msg.size(), msg.time(), msg.title(), msg.desc(), msg.renderer(), msg.renderId(), msg.additionalDatas()));
		});

	}

}
