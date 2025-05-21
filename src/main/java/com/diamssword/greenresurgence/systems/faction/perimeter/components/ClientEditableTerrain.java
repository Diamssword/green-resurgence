package com.diamssword.greenresurgence.systems.faction.perimeter.components;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.List;

public class ClientEditableTerrain {
	public List<FactionZone> zones = new ArrayList<>();
	public FactionPerm perms;

	public ClientEditableTerrain(FactionGuild guild, PlayerEntity player) {
		zones = guild.getAllTerrains();
		perms = guild.getPermsOf(new FactionMember(player));
	}

	public ClientEditableTerrain(NbtCompound tag) {

		var guild = new FactionGuild();
		NbtList ls = tag.getList("terrains", NbtList.COMPOUND_TYPE);
		ls.forEach(c -> {
			FactionZone b = new FactionZone(guild, (NbtCompound) c);
			zones.add(b);
		});
		perms = FactionPerm.fromNBT(tag.getCompound("perms"));
	}

	public NbtCompound toNBT() {
		NbtCompound tag = new NbtCompound();
		NbtList zones = new NbtList();
		this.zones.forEach(b -> {
			var tg = new NbtCompound();
			b.writeNbt(tg);
			zones.add(tg);
		});
		tag.put("terrains", zones);
		tag.put("perms", perms.toNBT());
		return tag;
	}
}
