package com.diamssword.greenresurgence.dynamicLight;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

public class CreateEventHandler {

	public static void register() {
		ClientEntityEvents.ENTITY_LOAD.register(CreateEventHandler::onClientEntityLoad);
		ClientEntityEvents.ENTITY_UNLOAD.register(CreateEventHandler::onClientEntityUnload);
		ClientTickEvents.END_WORLD_TICK.register(CreateEventHandler::onTick);
	}

	private static void onClientEntityLoad(Entity entity, ClientWorld clientLevel) {
		if(entity instanceof AbstractContraptionEntity contraptionEntity) {
			ContraptionEntityEventHandler.onContraptionEntityJoin(contraptionEntity);
		}
	}

	private static void onClientEntityUnload(Entity entity, ClientWorld clientLevel) {
		if(entity instanceof AbstractContraptionEntity contraptionEntity) {
			ContraptionEntityEventHandler.onContraptionEntityLeave(contraptionEntity);
		}
	}

	private static void onTick(ClientWorld clientLevel) {
		ContraptionEntityEventHandler.onTick(clientLevel);
	}
}
