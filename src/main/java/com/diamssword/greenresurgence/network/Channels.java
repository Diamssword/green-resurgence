package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.grids.GridContainerSyncer;
import com.diamssword.greenresurgence.systems.armor.ArmorLoader;
import com.diamssword.greenresurgence.systems.crafting.CraftingResult;
import com.diamssword.greenresurgence.systems.crafting.RecipeLoader;
import com.diamssword.greenresurgence.systems.crafting.SimpleRecipe;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionMember;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionPerm;
import com.diamssword.greenresurgence.systems.lootables.LootablesReloader;
import io.wispforest.owo.network.OwoNetChannel;
import io.wispforest.owo.network.serialization.PacketBufSerializer;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;

public class Channels {
	public static final OwoNetChannel MAIN = OwoNetChannel.create(new Identifier(GreenResurgence.ID, "main"));

	public static void initialize() {
		PacketBufSerializer.register(Vec3i.class, (write, val) -> {
			write.writeInt(val.getX());
			write.writeInt(val.getY());
			write.writeInt(val.getZ());
		}, (read) -> {
			int x = read.readInt();
			int y = read.readInt();
			int z = read.readInt();
			return new Vec3i(x, y, z);
		});
		PacketBufSerializer.register(GridContainerSyncer.class, GridContainerSyncer::serializer, GridContainerSyncer::unserializer);
		PacketBufSerializer.register(LootablesReloader.class, LootablesReloader::serializer, LootablesReloader::unserializer);
		PacketBufSerializer.register(ArmorLoader.class, ArmorLoader::serializer, ArmorLoader::unserializer);
		PacketBufSerializer.register(RecipeLoader.class, RecipeLoader::serializer, RecipeLoader::unserializer);
		PacketBufSerializer.register(CraftingResult.class, CraftingResult::serializer, CraftingResult::unserializer);
		PacketBufSerializer.register(SimpleRecipe.class, SimpleRecipe::serializer, SimpleRecipe::unserializer);
		PacketBufSerializer.register(FactionMember.class, FactionMember::serializer, FactionMember::unserializer);
		PacketBufSerializer.register(FactionPerm.class, FactionPerm::serializer, FactionPerm::unserializer);
		AdventureInteract.init();
		StructureSizePacket.init();
		CurrentZonePacket.init();
		DictionaryPackets.init();
		GuiPackets.init();
		CraftPackets.init();
		StatsPackets.init();
		PosesPackets.init();
		EntitiesPackets.init();
		InventoryPackets.init();
		GuildPackets.init();
		ModularArmorPackets.init();
	}

	public static boolean isSelfHost(MinecraftServer server, PlayerEntity player) {
		return server.isHost(player.getGameProfile());
	}

	/**
	 * IntegratedServer safe version, doesn't send packet to the player hosting;
	 *
	 * @param server
	 * @return
	 */
	public static OwoNetChannel.ServerHandle serverHandle(MinecraftServer server) {
		return MAIN.serverHandle(PlayerLookup.all(server).stream().filter(v -> !Channels.isSelfHost(server, v)).toList());
	}

	/**
	 * IntegratedServer safe version, doesn't send packet to the player if he his hosting;
	 *
	 * @param player
	 * @return
	 */
	public static <R extends Record> void sendToNonHost(PlayerEntity player, R... messages) {
		if(!isSelfHost(player.getServer(), player)) {
			MAIN.serverHandle(player).send(messages);
		}
	}
}
