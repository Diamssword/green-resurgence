package com.diamssword.greenresurgence.systems.faction.perimeter;

import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionGuild;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionZone;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FactionArea {
	public final static int maxDistanceBetweenCenters = 64;
	private BlockBox bounds = new BlockBox(BlockPos.ORIGIN);
	private final List<FactionZone> terrains = new ArrayList<>();
	private final FactionGuild owner;

	public FactionArea(FactionGuild owner, NbtCompound fromNBT) {
		this.owner = owner;
		NbtList ls = fromNBT.getList("terrains", NbtList.COMPOUND_TYPE);
		ls.forEach(c -> {
			FactionZone b = new FactionZone(owner, (NbtCompound) c).setArea(this);
			this.terrains.add(b);
		});
		recalculateBounds();
	}

	public FactionArea(FactionGuild owner, FactionZone initial) {
		this.bounds = new BlockBox(initial.getBounds().getCenter()).expand(maxDistanceBetweenCenters);
		this.terrains.add(initial.setArea(this));
		this.owner = owner;
	}

	public FactionGuild getOwner() {
		return owner;
	}

	public void writeNbt(NbtCompound tag) {
		NbtList zones = new NbtList();
		this.terrains.forEach(b -> {
			var tg = new NbtCompound();
			b.writeNbt(tg);
			zones.add(tg);
		});
		tag.put("terrains", zones);
	}

	public boolean isIn(Vec3i pos) {

		return terrainAt(pos).isPresent();
	}

	public Optional<FactionZone> terrainAt(Vec3i pos) {
		if(bounds.contains(pos)) {
			for(FactionZone b : terrains) {
				if(b.isIn(pos))
					return Optional.of(b);
			}
		}
		return Optional.empty();
	}

	public boolean isEmpty() {
		return terrains.isEmpty();
	}

	public List<FactionZone> getTerrainsAt(Vec3i pos, List<FactionZone> collector) {

		if(bounds.contains(pos)) {
			terrains.forEach(f -> {
				if(f.getBounds().contains(pos))
					collector.add(f);
			});
		}
		return collector;
	}

	public boolean isPositionValid(BlockPos pos) {
		if(bounds.contains(pos.getX(), pos.getY(), pos.getZ())) {
			for(FactionZone terrain : terrains) {
				if(terrain.getCenter().isWithinDistance(pos, maxDistanceBetweenCenters))
					return true;
			}
		}
		return false;
	}

	public boolean addIfValid(FactionZone terrain) {
		int x1 = bounds.getMinX(), y1 = bounds.getMinY(), z1 = bounds.getMinZ(), x2 = bounds.getMaxX(), y2 = bounds.getMaxY(), z2 = bounds.getMaxZ();
		if(isPositionValid(terrain.getCenter())) {
			terrains.add(terrain);
			var b1 = terrain.getBounds();
			if(b1.getMinX() < x1)
				x1 = b1.getMinX();
			if(b1.getMinZ() < z1)
				z1 = b1.getMinZ();
			if(b1.getMinY() < y1)
				y1 = b1.getMinY();
			if(b1.getMaxX() > x2)
				x2 = b1.getMaxX();
			if(b1.getMaxY() > y2)
				y2 = b1.getMaxY();
			if(b1.getMaxZ() > z2)
				z2 = b1.getMaxZ();
			bounds = new BlockBox(x1, y1, z1, x2, y2, z2);
			return true;
		}
		return false;
	}

	protected void recalculateBounds() {
		int x1 = Integer.MAX_VALUE, y1 = Integer.MAX_VALUE, z1 = Integer.MAX_VALUE, x2 = Integer.MIN_VALUE, y2 = Integer.MIN_VALUE, z2 = Integer.MIN_VALUE;
		for(FactionZone terrain : terrains) {
			var b1 = terrain.getBounds();
			if(b1.getMinX() < x1)
				x1 = b1.getMinX();
			if(b1.getMinZ() < z1)
				z1 = b1.getMinZ();
			if(b1.getMinY() < y1)
				y1 = b1.getMinY();
			if(b1.getMaxX() > x2)
				x2 = b1.getMaxX();
			if(b1.getMaxY() > y2)
				y2 = b1.getMaxY();
			if(b1.getMaxZ() > z2)
				z2 = b1.getMaxZ();
		}
		this.bounds = new BlockBox(x1, y1, z1, x2, y2, z2);
	}

	public List<FactionZone> getAllTerrains() {
		return terrains;
	}

	public boolean remove(FactionZone terrain) {
		if(terrains.remove(terrain)) {
			recalculateBounds();
			return true;
		}
		return false;
	}

	public BlockBox getBounds() {
		return bounds;
	}
}
