package com.diamssword.greenresurgence.render.environment;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class EnvironementAreas {
	public static List<FogModifier> fogAreas = new LinkedList<>();
	private static FogModifier currentFog;
	private static FogModifier secondCurrentFog;

	public static void init() {
		ClientTickEvents.END_WORLD_TICK.register(EnvironementAreas::onTick);
	}

	public static void onTick(ClientWorld world) {
		var pair = getTwoClosestBox(MinecraftClient.getInstance().player.getBoundingBox(), fogAreas, FogModifier::getBox);
		currentFog = pair.getLeft().orElse(null);
		if(currentFog != null)
			currentFog.setSecondFog(pair.getRight().orElse(null));
		var pos = MinecraftClient.getInstance().player.getPos();
		var time = world.getTime();
		for(FogModifier fogArea : fogAreas) {
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

	@Nullable
	private static FogModifier calculateCurrentFogModifier() {
		return getClosestBox(MinecraftClient.getInstance().player.getBoundingBox(), fogAreas, FogModifier::getBox).orElse(null);
	}

	public static Optional<FogModifier> getCurrentFogModifier() {
		return Optional.ofNullable(currentFog);
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
