package com.diamssword.greenresurgence.systems.character;

import com.diamssword.greenresurgence.network.Channels;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class SkinServerCache {

	public record PlayerInfos(String skin, String head, boolean slim) {
	}

	public static final SkinServerCache serverCache = new SkinServerCache();
	private final Map<UUID, PlayerInfos> skinCache = new HashMap<>();

	public record SendPlayerInfos(UUID player, String skin, String skinHead, boolean slim) {
	}

	public record RequestPlayerInfos(UUID player) {
	}

	public SkinServerCache() {
	}

	public Optional<PlayerInfos> getSkin(UUID user) {
		return Optional.ofNullable(skinCache.get(user));
	}

	public void addToCache(UUID id, String skin, String head, boolean slim) {
		skinCache.put(id, new PlayerInfos(skin, head, slim));
	}

	public void removeFromCache(UUID uuid) {
		skinCache.remove(uuid);
	}

	public static void init() {
		Channels.MAIN.registerClientboundDeferred(SendPlayerInfos.class);
		Channels.MAIN.registerServerbound(RequestPlayerInfos.class, (msg, ctx) -> {
			var skin = serverCache.skinCache.get(msg.player);
			if (skin != null)
				Channels.MAIN.serverHandle(ctx.player()).send(new SendPlayerInfos(msg.player, skin.skin, skin.head, skin.slim));
		});
	}
}
