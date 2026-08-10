package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.systems.Components;

import java.util.UUID;

public class PosesPackets {
	public record EmoteRequest(String emote, boolean stop) {}

	public record LiftOtherPlayer(UUID player, boolean carried) {
	}

	public record DismountedPlayerNotify(UUID player) {
	}

	public static void init() {
		Channels.MAIN.registerServerbound(EmoteRequest.class, (msg, ctx) -> {
			var comp = Components.PLAYER_DATA.get(ctx.player());
			if(msg.stop)
				comp.removeCustomPose(msg.emote);
			else
				comp.addCustomPose(msg.emote, true);
		});
		Channels.MAIN.registerClientboundDeferred(LiftOtherPlayer.class);
		Channels.MAIN.registerClientboundDeferred(DismountedPlayerNotify.class);
	}
}
