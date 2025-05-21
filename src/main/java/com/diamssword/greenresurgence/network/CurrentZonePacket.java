package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.ClientEditableTerrain;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionGuild;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionZone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CurrentZonePacket {
	public static List<Triple<String, BlockPos, BlockBox>> DebugViews = new ArrayList<>();
	public static ClientEditableTerrain currentZone;
	public static MyGuild myGuild;

	public record ZoneResponse(NbtCompound tag) {
	}

	public record DebugZoneList(NbtCompound tag) {
	}

	public record MyGuild(UUID id, String name) {
	}

	public static ZoneResponse from(FactionGuild base, PlayerEntity player) {
		return new ZoneResponse(new ClientEditableTerrain(base, player).toNBT());
	}

	public static void sendDebugZone(World w, @Nullable PlayerEntity player) {
		NbtList list = new NbtList();
		var bases = w.getComponent(Components.BASE_LIST);
		bases.getAll().forEach(v -> {
			v.getAllTerrains().forEach(t -> {
				var tag1 = new NbtCompound();
				tag1.putString("name", v.getName());
				tag1.putLong("pos", t.getBounds().getCenter().asLong());
				tag1.putIntArray("bounds", t.boundsToArray());
				list.add(tag1);
			});
		});
		var nb = new NbtCompound();
		nb.put("list", list);
		if (player != null) {
			Channels.MAIN.serverHandle(player).send(new DebugZoneList(nb));
		} else {
			var pls = w.getServer().getPlayerManager().getPlayerList().stream().filter(v -> v.isCreative());
			Channels.MAIN.serverHandle(pls.toList()).send(new DebugZoneList(nb));
		}
	}

	public static void init() {
		Channels.MAIN.registerClientbound(MyGuild.class, (msg, ctx) -> myGuild = msg);
		Channels.MAIN.registerClientbound(ZoneResponse.class, (msg, ctx) -> CurrentZonePacket.currentZone = new ClientEditableTerrain(msg.tag()));
		Channels.MAIN.registerClientbound(DebugZoneList.class, (message, access) -> {
			DebugViews.clear();
			var list = message.tag.getList("list", NbtElement.COMPOUND_TYPE);
			list.forEach((c) -> {
				if (c instanceof NbtCompound co) {
					var name = co.getString("name");
					var pos = BlockPos.fromLong(co.getLong("pos"));
					var box = FactionZone.BoundFromArray(co.getIntArray("bounds"));
					DebugViews.add(new ImmutableTriple<>(name, pos, box));
				}
			});
		});
	}
}
