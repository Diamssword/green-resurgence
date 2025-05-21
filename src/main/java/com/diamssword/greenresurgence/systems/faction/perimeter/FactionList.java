package com.diamssword.greenresurgence.systems.faction.perimeter;

import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.CurrentZonePacket;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionGuild;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionMember;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionZone;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.Perms;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.*;

public class FactionList implements ServerTickingComponent {
	private final List<FactionGuild> guilds = new ArrayList<>();
	private final World provider;

	public FactionList(World provider) {
		this.provider = provider;
	}

	public List<FactionGuild> getAll() {
		return guilds;
	}

	public Map<UUID, String> getNames() {
		HashMap<UUID, String> res = new HashMap<>();
		guilds.forEach(v -> res.put(v.getId(), v.getName()));
		return res;
	}

	public Optional<FactionGuild> getAt(Vec3i pos) {
		return guilds.stream().filter(b -> b.isIn(pos)).findFirst();

	}

	public Optional<FactionZone> getTerrainAt(Vec3i pos) {
		for (FactionGuild base : guilds) {
			var b = base.getTerrainAt(pos);
			if (b.isPresent())
				return b;
		}
		return Optional.empty();
	}

	public boolean isAllowedAt(Vec3i pos, FactionMember member, Perms perm) {
		Optional<FactionGuild> base = getAt(pos);
		return base.map(baseInstance -> baseInstance.isAllowed(member, perm)).orElse(false);
	}

	public boolean addGuild(FactionGuild guild) {
		if (guilds.stream().noneMatch(v -> v.getId().equals(guild.getId()))) {
			guilds.add(guild);
			guild.getOwner().asPlayer(this.provider).ifPresent(p -> {
				if (!p.getWorld().isClient) {
					CurrentZonePacket.sendDebugZone(this.provider, null);
					Channels.MAIN.serverHandle(p).send(new CurrentZonePacket.MyGuild(guild.getId(), guild.getName()));
				}
			});
			return true;
		} else
			return false;
	}

	public boolean delete(UUID id) {
		Optional<FactionGuild> t = this.get(id);
		if (t.isPresent()) {
			guilds.remove(t.get());
			return true;
		}
		return false;

	}

	public Optional<FactionGuild> get(UUID id) {
		return guilds.stream().filter(v -> v.getId().equals(id)).findFirst();
	}

	public Optional<FactionGuild> getForPlayer(UUID playerID, boolean ownerOnly) {
		var ls = guilds.stream().filter(v -> v.getOwner().isPlayer() && v.getOwner().getId().equals(playerID));
		var f = ls.findFirst();
		if (f.isEmpty() && !ownerOnly) {
			f = guilds.stream().filter(v -> v.getMembers().contains(playerID)).findFirst();
		}
		return f;
	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		NbtList ls = tag.getList("guilds", NbtList.COMPOUND_TYPE);
		guilds.clear();
		ls.forEach(c -> {
			FactionGuild b = FactionGuild.fromNBT((NbtCompound) c);
			if (b != null)
				guilds.add(b);
		});

	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		NbtList ls = new NbtList();
		guilds.forEach(b -> {
			NbtCompound t = new NbtCompound();
			b.writeNbt(t);
			ls.add(t);
		});
		tag.put("guilds", ls);
	}

	@Override
	public void serverTick() {
		if (provider.getTime() % 40 == 0) {

			guilds.forEach(b -> b.tick((ServerWorld) provider));
		}
	}
}
