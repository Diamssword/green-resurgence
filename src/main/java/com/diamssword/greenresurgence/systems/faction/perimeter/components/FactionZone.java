package com.diamssword.greenresurgence.systems.faction.perimeter.components;

import com.diamssword.greenresurgence.systems.faction.perimeter.FactionArea;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

public class FactionZone {
	private final FactionGuild owner;
	private final BlockBox bounds;
	private final boolean isMainZone = false;
	private @Nullable FactionArea area;

	public FactionZone(FactionGuild owner, BlockBox bounds) {
		this.bounds = bounds;
		this.owner = owner;

	}

	public FactionZone(FactionGuild owner, BlockPos pos, int size) {
		this.bounds = new BlockBox(pos).expand(size);
		this.owner = owner;

	}

	public FactionZone(FactionGuild owner, NbtCompound tag) {
		this.bounds = BoundFromArray(tag.getIntArray("bounds"));
		this.owner = owner;

	}

	public FactionZone setArea(FactionArea area) {
		this.area = area;
		return this;
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

	public BlockPos getCenter() {
		return bounds.getCenter();
	}

	public boolean fullyContainsBox(BlockBox other) {
		var b = getBounds();
		return b.getMinX() <= other.getMinX() && b.getMinY() <= other.getMinY() && b.getMinZ() <= other.getMinZ() &&
				b.getMaxX() >= other.getMaxX() && b.getMaxY() >= other.getMaxY() && b.getMaxZ() >= other.getMaxZ();
	}

	public int[] boundsToArray() {
		return new int[]{bounds.getMinX(), bounds.getMinY(), bounds.getMinZ(), bounds.getMaxX(), bounds.getMaxY(), bounds.getMaxZ()};
	}

	public FactionGuild getOwner() {
		return getArea().getOwner();
	}

	@Nullable
	public FactionArea getArea() {
		return area;
	}

	public static BlockBox BoundFromArray(int[] arr) {
		return new BlockBox(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]);
	}
}
