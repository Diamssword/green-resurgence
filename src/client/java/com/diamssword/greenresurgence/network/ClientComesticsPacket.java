package com.diamssword.greenresurgence.network;

public class ClientComesticsPacket {
	public static void init() {
		Channels.MAIN.registerClientbound(PosesPackets.LiftOtherPlayer.class, (msg, ctx) -> {
			var player = ctx.runtime().world.getPlayerByUuid(msg.player());
			if (msg.carried())
				ctx.player().startRiding(player);
			else
				player.startRiding(ctx.player(), true);
		});
		Channels.MAIN.registerClientbound(PosesPackets.DismountedPlayerNotify.class, (msg, ctx) -> {
			var player = ctx.runtime().world.getPlayerByUuid(msg.player());
			player.dismountVehicle();
		});

	}

}
