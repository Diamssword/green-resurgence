package com.diamssword.greenresurgence.systems.environment;

import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.utils.Utils;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class EnvironementAreas implements ServerTickingComponent {
	public static final Map<String, Supplier<EffectArea>> FACTORIES = new ConcurrentHashMap<>();

	static {
		FACTORIES.put("fog", FogArea::new);
	}

	private static final Map<PlayerEntity, EffectArea> PlayerAreaMap = new ConcurrentHashMap<>();
	private final List<EffectArea> areas = Collections.synchronizedList(new ArrayList<>());
	private final World world;

	public EnvironementAreas(World provider) {
		this.world = provider;
	}

	public static void onPlayerDisconnect(PlayerEntity player) {
		PlayerAreaMap.remove(player);
	}

	public List<EffectArea> getAreas() {

		return areas;
	}

	public static List<EffectArea> getAreas(World world) {
		return world.getComponent(Components.ENVIRONMENT_AREAS).getAreas();
	}

	@Nullable
	private static EffectArea calculateCurrentFogModifier(Box boundingBox, List<EffectArea> areas) {
		return getClosestBox(boundingBox, areas).orElse(null);
	}

	public static Optional<EffectArea> getAreaFor(PlayerEntity playerEntity) {
		return Optional.ofNullable(PlayerAreaMap.get(playerEntity));
	}

	public static Optional<EffectArea> getClosestBox(Box playerBox, List<EffectArea> elements) {
		EffectArea bestBox = null;
		double bestScore = Double.POSITIVE_INFINITY;
		for(EffectArea el : elements) {
			var box = el.getArea();
			if(!playerBox.intersects(box)) continue;

			Vec3d center = playerBox.getCenter();

			double dx = Math.min(center.x - box.minX, box.maxX - center.x);
			double dy = Math.min(center.y - box.minY, box.maxY - center.y);
			double dz = Math.min(center.z - box.minZ, box.maxZ - center.z);

			double score = Math.min(dx, Math.min(dy, dz));

			if(score < bestScore) {
				bestScore = score;
				bestBox = el;
			}
		}
		return Optional.ofNullable(bestBox);
	}

	@Override
	public void serverTick() {
		if(world.getTime() % 5 == 0) {
			world.getPlayers().forEach(player -> {
				var mod = calculateCurrentFogModifier(player.getBoundingBox(), getAreas(player.getWorld()));
				if(mod == null)
					PlayerAreaMap.remove(player);
				else
					PlayerAreaMap.put(player, mod);
			});
		}

		var areas = getAreas(world);
		for(EffectArea fogArea : areas) {
			var set = PlayerAreaMap.entrySet().stream().filter(v -> v.getValue() == fogArea).map(Map.Entry::getKey);
			fogArea.tick(set.toList(), world);
		}
	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		areas.clear();
		var ls = tag.getList("areas", NbtElement.COMPOUND_TYPE);
		ls.forEach(l -> {
			createArea(((NbtCompound) l).getString("key")).ifPresent(a -> {
				areas.add(loadAreaFromNBT(a, (NbtCompound) l));
			});
		});
	}

	public static EffectArea loadAreaFromNBT(EffectArea area, NbtCompound nbt) {
		area.fromNBT(nbt);
		area.setArea(Utils.boxFromNBT(nbt.getCompound("box")));
		return area;
	}

	public static NbtCompound areaToNBT(EffectArea area) {
		var nbt = area.toNBT();
		nbt.put("box", Utils.boxToNBT(area.getArea()));
		nbt.putString("key", area.key());
		return nbt;
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		var list = new NbtList();
		getAreas().forEach(v -> list.add(areaToNBT(v)));
		tag.put("areas", list);
	}

	public static Optional<EffectArea> createArea(String type) {
		var f = FACTORIES.get(type);
		if(f != null)
			return Optional.of(f.get());
		return Optional.empty();
	}

	public List<EffectArea> getAt(Vec3d pos) {
		return areas.stream().filter(a -> a.getArea().contains(pos)).toList();
	}

	public Optional<EffectArea> getAtFirst(Vec3d centerPos) {
		return getClosestBox(Box.from(centerPos), areas);
	}

	public void remove(EffectArea effectArea) {
		areas.remove(effectArea);
	}

	public void add(EffectArea created) {
		areas.add(created);
	}
}
