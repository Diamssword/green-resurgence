package com.diamssword.greenresurgence.systems.faction.perimeter.components;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class FactionMember {

	private PlayerEntity player;
	private final String name;
	private final UUID id;
	private final boolean isGuild;
	private FactionGuild guild;

	public World getWorld() {
		if (player != null)
			return player.getWorld();
		else if (guild != null)
			return guild.getOwner().getWorld();
		return null;
	}

	public Optional<PlayerEntity> asPlayer(@Nullable World w) {
		if (isGuild)
			return Optional.empty();
		if (player != null)
			return Optional.of(player);
		if (w == null)
			return Optional.empty();
		return Optional.ofNullable(w.getPlayerByUuid(id));
	}

	public boolean isPlayer() {
		return !isGuild;
	}

	public boolean isGuild() {
		return isGuild;
	}

	public FactionMember(FactionGuild guild) {
		this.id = guild.getId();
		this.guild = guild;
		this.isGuild = true;
		this.name = guild.getName();
	}

	public FactionMember(UUID id, String name, boolean isGuild) {
		this.id = id;
		this.isGuild = isGuild;
		this.name = name;
	}

	public FactionMember(PlayerEntity player) {
		this.id = player.getUuid();
		this.player = player;
		this.isGuild = false;
		this.name = player.getName().getString();
	}

	public FactionMember(NbtCompound tag) {
		this.id = tag.getUuid("id");
		this.isGuild = tag.getBoolean("isGuild");
		this.name = tag.getString("name");
	}

	public UUID getId() {
		return id;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FactionMember that = (FactionMember) o;
		return isGuild == that.isGuild && Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, isGuild);
	}

	public void writeNbt(NbtCompound tag) {
		tag.putUuid("id", id);
		tag.putString("name", name);
		tag.putBoolean("isGuild", isGuild);
	}

	public String getName() {
		return this.name;
	}

	public static void serializer(PacketByteBuf write, FactionMember val) {
		write.writeUuid(val.getId());
		write.writeString(val.getName());
		write.writeBoolean(val.isGuild);

	}

	public static FactionMember unserializer(PacketByteBuf read) {
		return new FactionMember(read.readUuid(), read.readString(), read.readBoolean());
	}
}
