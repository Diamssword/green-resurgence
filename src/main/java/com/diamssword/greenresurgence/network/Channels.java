package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.GridContainerSyncer;
import com.diamssword.greenresurgence.systems.clothing.ClothingLoader;
import com.diamssword.greenresurgence.systems.crafting.CraftingResult;
import com.diamssword.greenresurgence.systems.crafting.RecipeLoader;
import com.diamssword.greenresurgence.systems.crafting.SimpleRecipe;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionMember;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionPerm;
import com.diamssword.greenresurgence.systems.lootables.LootablesReloader;
import io.wispforest.owo.network.OwoNetChannel;
import io.wispforest.owo.network.serialization.PacketBufSerializer;
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
		PacketBufSerializer.register(ClothingLoader.class, ClothingLoader::serializer, ClothingLoader::unserializer);
		PacketBufSerializer.register(RecipeLoader.class, RecipeLoader::serializer, RecipeLoader::unserializer);
		PacketBufSerializer.register(CraftingResult.class, CraftingResult::serializer, CraftingResult::unserializer);
		PacketBufSerializer.register(SimpleRecipe.class, SimpleRecipe::serializer, SimpleRecipe::unserializer);
		PacketBufSerializer.register(FactionMember.class, FactionMember::serializer, FactionMember::unserializer);
		PacketBufSerializer.register(FactionPerm.class, FactionPerm::serializer, FactionPerm::unserializer);
		AdventureInteract.init();
		StructureSizePacket.init();
		CurrentZonePacket.init();
		DictionaryPackets.init();
		CosmeticsPackets.init();
		GuiPackets.init();
		CraftPackets.init();
		StatsPackets.init();
		PosesPackets.init();
		EntitiesPackets.init();
		InventoryPackets.init();
		GuildPackets.init();
		ModularArmorPackets.init();
	}
}
