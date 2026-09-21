package com.diamssword.greenresurgence.systems.faction.perimeter;

import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionGuild;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionTerrainStorage;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionZone;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.TerrainEnergyStorage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.*;

public class FactionArea {
	public final static int maxDistanceBetweenCenters = 66;
	private BlockBox bounds = new BlockBox(BlockPos.ORIGIN);
	private FactionZone mainZone;
	private final List<FactionZone> terrains = new ArrayList<>();
	private final FactionGuild owner;
	private final FactionTerrainStorage storage;
	private final TerrainEnergyStorage energyStorage = new TerrainEnergyStorage();

	public FactionArea(FactionGuild owner, World world, NbtCompound fromNBT) {
		this.owner = owner;
		this.storage = new FactionTerrainStorage(world);
		NbtList ls = fromNBT.getList("terrains", NbtList.COMPOUND_TYPE);
		ls.forEach(c -> {
			FactionZone b = new FactionZone(owner, (NbtCompound) c).setArea(this);
			this.terrains.add(b);
		});
		if(fromNBT.contains("storage")) {
			storage.fromNBT(fromNBT.getCompound("storage"));
		}
		if(fromNBT.contains("energy")) {
			energyStorage.fromNBT(fromNBT.getCompound("energy"));
		}
		recalculateBounds();
		findAMain();
	}


	private FactionArea(FactionGuild owner, Collection<FactionZone> terrains) {
		this.owner = owner;
		this.storage = new FactionTerrainStorage(owner.getOwner().getWorld());
		this.terrains.addAll(terrains);

		for(FactionZone terrain : this.terrains) {
			terrain.setArea(this);
		}
		recalculateBounds();
		findAMain();
	}

	public FactionArea(FactionGuild owner, FactionZone initial) {
		this.bounds = new BlockBox(initial.getBounds().getCenter()).expand(maxDistanceBetweenCenters);
		this.terrains.add(initial.setArea(this));
		initial.setMainZone(true);
		mainZone = initial;
		this.owner = owner;
		this.storage = new FactionTerrainStorage(owner.getOwner().getWorld());
	}

	public FactionZone getMainZone() {
		return mainZone;
	}

	public void tick(World world) {
		energyStorage.tick();
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
		var t1 = new NbtCompound();
		storage.toNBT(t1);
		tag.put("storage", t1);
		var t2 = new NbtCompound();
		energyStorage.toNBT(t2);
		tag.put("energy", t1);
	}

	public FactionTerrainStorage getStorage() {
		return storage;
	}

	public TerrainEnergyStorage getEnergyStorage() {
		return energyStorage;
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
		//	if(bounds.contains(pos.getX(), pos.getY(), pos.getZ())) {
		for(FactionZone terrain : terrains) {
			if(terrain.getCenter().isWithinDistance(pos, maxDistanceBetweenCenters))
				return true;
		}
		//	}
		return false;
	}

	public void absorb(FactionArea otherArea) {
		terrains.addAll(otherArea.getAllTerrains());
		otherArea.getStorage().getInventories().forEach(storage::addIfMissing);
		getEnergyStorage().absorb(otherArea.getEnergyStorage());

		terrains.forEach(t -> {
			t.setArea(this);
			if(t.isMainZone() && t != mainZone)
				t.setMainZone(false);
		});
		recalculateBounds();
	}

	public List<FactionArea> split() {
		List<FactionZone> remaining = new ArrayList<>(terrains);
		Set<FactionZone> unvisited = new HashSet<>(remaining);
		List<List<FactionZone>> components = new ArrayList<>();

		while(!unvisited.isEmpty()) {
			FactionZone start = unvisited.iterator().next();
			List<FactionZone> component = new ArrayList<>();
			Queue<FactionZone> queue = new ArrayDeque<>();
			queue.add(start);
			unvisited.remove(start);
			while(!queue.isEmpty()) {
				FactionZone current = queue.poll();
				component.add(current);
				Iterator<FactionZone> it = unvisited.iterator();
				while(it.hasNext()) {
					FactionZone candidate = it.next();

					if(current.getCenter().isWithinDistance(
							candidate.getCenter(),
							maxDistanceBetweenCenters
					)) {
						it.remove();
						queue.add(candidate);
					}
				}
			}
			components.add(component);
		}
		if(components.size() == 1) {
			getStorage().refreshContainers(this);
			recalculateBounds();
			return List.of(this);
		}
		terrains.clear();
		terrains.addAll(components.get(0));
		if(!terrains.isEmpty()) {
			getStorage().refreshContainers(this);
			recalculateBounds();
			findAMain();

		}
		for(FactionZone terrain : terrains) {
			terrain.setArea(this);
		}
		List<FactionArea> result = new ArrayList<>();
		result.add(this);

		for(int i = 1; i < components.size(); i++) {
			result.add(new FactionArea(owner, components.get(i)));
		}

		return result;
	}

	private void findAMain() {
		mainZone = null;
		terrains.forEach(t -> {
			if(t.isMainZone()) {
				if(mainZone == null)
					mainZone = t;
				else
					t.setMainZone(false);
			}
		});
		if(mainZone == null) {
			var center = bounds.getCenter();
			FactionZone closest = null;
			double dist = Integer.MAX_VALUE;
			for(FactionZone m : terrains) {
				var d = m.getCenter().getSquaredDistance(center);
				if(closest == null) {
					closest = m;
					dist = d;
				} else if(d < dist) {
					closest = m;
					dist = d;
				}
			}
			if(closest != null) {
				closest.setMainZone(true);
				this.mainZone = closest;
			}
		}
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
			terrain.setArea(this);
			return true;
		}
		return false;
	}

	protected void recalculateBounds() {
		if(terrains.isEmpty()) {
			this.bounds = new BlockBox(BlockPos.ORIGIN);
			return;
		}
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
			if(terrain == mainZone)
				findAMain();
			if(!terrains.isEmpty())
				recalculateBounds();
			getStorage().refreshContainers(this);
			return true;
		}
		return false;
	}

	public BlockBox getBounds() {
		return bounds;
	}
}
