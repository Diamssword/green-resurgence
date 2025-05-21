package com.diamssword.greenresurgence.systems.faction.perimeter.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class FactionPerm {

	public static final FactionPerm ALL = new FactionPerm("ALL");
	public static final FactionPerm NONE = new FactionPerm("NONE");

	static {
		ALL.allowed.addAll(Arrays.asList(Perms.values()));
	}

	private String name;
	private final List<Perms> allowed = new ArrayList<>();

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public boolean needSurvival() {
		for (var d : allowed) {
			if (d.needSurvival)
				return true;
		}
		return false;
	}

	public boolean isAllowed(Perms perm) {
		return allowed.contains(perm);
	}

	public boolean isAllowed(Perms... perm) {
		return new HashSet<>(allowed).containsAll(Arrays.stream(perm).toList());
	}

	public NbtCompound toNBT() {
		var tag = new NbtCompound();
		tag.putString("name", name);
		tag.putIntArray("perms", toIntArray());
		return tag;
	}

	public static FactionPerm fromNBT(NbtCompound tag) {
		var name = tag.getString("name");
		if (name != null) {
			var f = new FactionPerm(name);
			f.fromIntArray(tag.getIntArray("perms"));
			return f;
		}
		return null;
	}

	public void setPerm(Perms perm, boolean allowed) {
		if (!allowed)
			this.allowed.remove(perm);
		else if (!this.allowed.contains(perm))
			this.allowed.add(perm);
	}

	public FactionPerm(String name) {
		this.name = name;
	}

	public int[] toIntArray() {
		var res = new int[allowed.size()];
		for (int i = 0; i < res.length; i++) {
			res[i] = allowed.get(i).id;
		}
		return res;
	}

	public void fromIntArray(int[] perms) {
		this.allowed.clear();
		for (int perm : perms) {
			var p = Perms.getByID(perm);
			if (p != null)
				this.allowed.add(p);
		}

	}

	public static void serializer(PacketByteBuf write, FactionPerm val) {
		write.writeString(val.name);
		write.writeIntArray(val.toIntArray());

	}

	public static FactionPerm unserializer(PacketByteBuf read) {
		var p = new FactionPerm(read.readString());
		p.fromIntArray(read.readIntArray());
		return p;
	}
}
