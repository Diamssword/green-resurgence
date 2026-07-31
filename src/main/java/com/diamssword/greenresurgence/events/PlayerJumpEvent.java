package com.diamssword.greenresurgence.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerJumpEvent {
	Event<PlayerJumpEvent> onJump = EventFactory.createArrayBacked(PlayerJumpEvent.class,
			(listeners) -> (player) -> {
				for(PlayerJumpEvent listener : listeners) {
					listener.onJump(player);
				}
			});

	void onJump(ServerPlayerEntity player);
}
