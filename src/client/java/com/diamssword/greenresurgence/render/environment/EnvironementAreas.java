package com.diamssword.greenresurgence.render.environment;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class EnvironementAreas {
	public static List<BaseEnvironmentArea> fogAreas = new LinkedList<>();
	private static BaseEnvironmentArea currentZone;
	private static FogModifier currentFog;

	public static void init() {
		ClientTickEvents.END_WORLD_TICK.register(EnvironementAreas::onTick);
	}

	public static void onTick(ClientWorld world) {
		var pair = getTwoClosestBox(MinecraftClient.getInstance().player.getBoundingBox(), fogAreas, BaseEnvironmentArea::getBox);
		currentZone = pair.getLeft().orElse(null);
		if(currentZone instanceof FogModifier fo) {
			if(pair.getRight().orElse(null) instanceof FogModifier fo1)
				fo.setSecondFog(fo1);
			else
				fo.setSecondFog(null);
			currentFog = fo;
		} else
			currentFog = null;
		var pos = MinecraftClient.getInstance().player.getPos();
		var time = world.getTime();
		for(BaseEnvironmentArea fogArea : fogAreas) {
			/*var box = fogArea.getBox();
			double dx = Math.min(pos.x - box.minX, box.maxX - pos.x);
			double dy = Math.min(pos.y - box.minY, box.maxY - pos.y);
			double dz = Math.min(pos.z - box.minZ, box.maxZ - pos.z);
			double distance = Math.min(dx, Math.min(dy, dz));

			 */
			if(fogArea.getBox().contains(pos)) {
				fogArea.insideZoneUpdate(time, fogArea == currentFog);
			} else {
				fogArea.outsideZoneUpdate(time);
			}

		}
	}

	public static Optional<FogModifier> getCurrentFogModifier() {
		return Optional.ofNullable(currentFog);
	}

	public static Optional<BaseEnvironmentArea> getCurrentZone() {
		return Optional.ofNullable(currentZone);
	}

	public static <T> Pair<Optional<T>, Optional<T>> getTwoClosestBox(Box playerBox, List<T> elements, Function<T, Box> boxGetter) {
		T bestBox = null;
		T secondBestBox = null;
		double bestScore = Double.POSITIVE_INFINITY;
		for(T el : elements) {
			var box = boxGetter.apply(el);
			if(!playerBox.intersects(box)) continue;

			Vec3d center = playerBox.getCenter();

			double dx = Math.min(center.x - box.minX, box.maxX - center.x);
			double dy = Math.min(center.y - box.minY, box.maxY - center.y);
			double dz = Math.min(center.z - box.minZ, box.maxZ - center.z);

			double score = Math.min(dx, Math.min(dy, dz));

			if(score < bestScore) {
				bestScore = score;
				secondBestBox = bestBox;
				bestBox = el;

			}
		}
		return new Pair<>(Optional.ofNullable(bestBox), Optional.ofNullable(secondBestBox));
	}

	public static <T> Optional<T> getClosestBox(Box playerBox, List<T> elements, Function<T, Box> boxGetter) {
		T bestBox = null;
		double bestScore = Double.POSITIVE_INFINITY;
		for(T el : elements) {
			var box = boxGetter.apply(el);
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

}
