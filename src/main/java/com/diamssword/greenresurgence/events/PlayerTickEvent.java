package com.diamssword.greenresurgence.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerTickEvent {
	Event<PlayerTickEvent> onTick = EventFactory.createArrayBacked(PlayerTickEvent.class,
			(listeners) -> (player, tickEnd) -> {
				for (PlayerTickEvent listener : listeners) {
					listener.onTick(player, tickEnd);
				}
			});

	void onTick(ServerPlayerEntity player, boolean tickEnd);
}
