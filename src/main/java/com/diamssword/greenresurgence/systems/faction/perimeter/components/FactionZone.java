package com.diamssword.greenresurgence.systems.faction.perimeter.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class FactionZone {
	private final FactionGuild owner;
	private final BlockBox bounds;
	private final boolean isMainZone = false;

	public FactionZone(FactionGuild owner, BlockPos pos, int size) {
		this.bounds = new BlockBox(pos).expand(size);
		this.owner = owner;

	}

	public FactionZone(FactionGuild owner, NbtCompound tag) {
		this.bounds = BoundFromArray(tag.getIntArray("bounds"));
		this.owner = owner;

	}

	public boolean isIn(Vec3i pos) {
		return bounds.contains(pos);
	}

	public void writeNbt(NbtCompound tag) {
		tag.putIntArray("bounds", this.boundsToArray());
	}

	public BlockBox getBounds() {
		return bounds;
	}

	public int[] boundsToArray() {
		return new int[]{bounds.getMinX(), bounds.getMinY(), bounds.getMinZ(), bounds.getMaxX(), bounds.getMaxY(), bounds.getMaxZ()};
	}

	public FactionGuild getOwner() {
		return owner;
	}

	public static BlockBox BoundFromArray(int[] arr) {
		return new BlockBox(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]);
	}
}
