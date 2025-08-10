package com.diamssword.greenresurgence.systems;

import com.diamssword.greenresurgence.network.SkinServerCache;
import com.diamssword.greenresurgence.systems.character.PlayerCharacters;
import com.diamssword.greenresurgence.systems.character.PlayerData;
import com.diamssword.greenresurgence.systems.character.PlayerInventoryData;
import com.diamssword.greenresurgence.systems.faction.perimeter.FactionList;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.level.LevelComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.level.LevelComponentInitializer;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.minecraft.util.Identifier;

public class Components implements EntityComponentInitializer, WorldComponentInitializer, LevelComponentInitializer {
	public static final ComponentKey<FactionList> BASE_LIST = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("green_resurgence:base_list"), FactionList.class);
	public static final ComponentKey<PlayerData> PLAYER_DATA = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("green_resurgence:player_data"), PlayerData.class);
	public static final ComponentKey<PlayerCharacters> PLAYER_CHARACTERS = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("green_resurgence:player_characters"), PlayerCharacters.class);
	public static final ComponentKey<PlayerInventoryData> PLAYER_INVENTORY = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("green_resurgence:player_inventory"), PlayerInventoryData.class);
	public static final ComponentKey<SkinServerCache> SERVER_PLAYER_CACHE = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("green_resurgence:player_cache"), SkinServerCache.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerForPlayers(PLAYER_DATA, PlayerData::new, RespawnCopyStrategy.ALWAYS_COPY);
		registry.registerForPlayers(PLAYER_CHARACTERS, PlayerCharacters::new, RespawnCopyStrategy.ALWAYS_COPY);
		registry.registerForPlayers(PLAYER_INVENTORY, PlayerInventoryData::new, RespawnCopyStrategy.ALWAYS_COPY);

	}

	@Override
	public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
		registry.register(BASE_LIST, FactionList::new);
	}

	@Override
	public void registerLevelComponentFactories(LevelComponentFactoryRegistry registry) {
		registry.register(SERVER_PLAYER_CACHE, SkinServerCache::new);
	}
}
