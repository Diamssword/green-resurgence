package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.systems.environment.EnvironementAreas;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class EnvironmentPacket {

	public record AreaList(NbtCompound areas) {
	}

	public static void init() {
		Channels.MAIN.registerClientboundDeferred(AreaList.class);
	}

	public static void sendListFor(PlayerEntity player, World world) {
		var list = new NbtList();
		EnvironementAreas.getAreas(world).forEach(v -> {
			list.add(v.toNBT());
		});
		var comp = new NbtCompound();
		comp.put("list", list);
		Channels.MAIN.serverHandle(player).send(new AreaList(comp));
	}

	public static void sendListForAll(ServerWorld world) {
		var list = new NbtList();
		EnvironementAreas.getAreas(world).forEach(v -> {
			list.add(v.toNBT());
		});
		var comp = new NbtCompound();
		comp.put("list", list);
		Channels.MAIN.serverHandle(world.getPlayers()).send(new AreaList(comp));
	}
}
