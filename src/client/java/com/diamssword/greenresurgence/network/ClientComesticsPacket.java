package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.render.cosmetics.SkinsLoader;
import com.diamssword.greenresurgence.systems.character.SkinServerCache;

public class ClientComesticsPacket {
	public static void init() {

		Channels.MAIN.registerClientbound(CosmeticsPackets.RefreshSkin.class, (message, access) -> {
			SkinsLoader.clientSkinCache.removeFromCache(message.player());
			SkinsLoader.instance.markReload(message.player(), true);
		});
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
		Channels.MAIN.registerClientbound(SkinServerCache.SendPlayerInfos.class, (msg, ctx) -> {
			SkinsLoader.clientSkinCache.addToCache(msg.player(), msg.skin(), msg.skinHead(), msg.slim());
			SkinsLoader.instance.markReload(msg.player(), true);
		});
	}

}
