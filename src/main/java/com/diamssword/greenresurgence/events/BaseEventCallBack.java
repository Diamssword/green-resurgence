package com.diamssword.greenresurgence.events;

import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionGuild;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public interface BaseEventCallBack {
	Event<BaseEventCallBack> ENTER = EventFactory.createArrayBacked(BaseEventCallBack.class,
			(listeners) -> (player, base) -> {
				for(BaseEventCallBack listener : listeners) {
					listener.enterOrLeave(player, base);
				}
			});
	/**
	 * Append every 40 ticks
	 */
	Event<BaseEventTickCallBack> PLAYER_TICK = EventFactory.createArrayBacked(BaseEventTickCallBack.class,
			(listeners) -> (player, base) -> {
				for(BaseEventTickCallBack listener : listeners) {
					listener.tick(player, base);
				}
			});
	Event<BaseEventCallBack> LEAVE = EventFactory.createArrayBacked(BaseEventCallBack.class,
			(listeners) -> (player, base) -> {
				for(BaseEventCallBack listener : listeners) {
					listener.enterOrLeave(player, base);
				}
			});

	void enterOrLeave(ServerPlayerEntity player, FactionGuild base);

	public interface BaseEventTickCallBack {
		void tick(ServerPlayerEntity player, FactionGuild base);
	}
}
