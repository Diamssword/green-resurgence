package com.diamssword.greenresurgence.render.environment;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class EnvironementAreas {
	public static List<FogModifier> fogAreas = new LinkedList<>();
	private static FogModifier currentFog;

	public static void init() {
		ClientTickEvents.END_WORLD_TICK.register(EnvironementAreas::onTick);
	}

	public static void onTick(ClientWorld world) {
		currentFog = calculateCurrentFogModifier();
		var pos = MinecraftClient.getInstance().player.getPos();
		var time = world.getTime();
		for(FogModifier fogArea : fogAreas) {
			if(fogArea == currentFog) {
				fogArea.insideZoneUpdate(pos.distanceTo(fogArea.getBox().getCenter()), time);
			} else {
				var box = fogArea.getBox();
				double dx = Math.min(pos.x - box.minX, box.maxX - pos.x);
				double dy = Math.min(pos.y - box.minY, box.maxY - pos.y);
				double dz = Math.min(pos.z - box.minZ, box.maxZ - pos.z);
				double distance = Math.min(dx, Math.min(dy, dz));
				fogArea.outsideZoneUpdate(Math.abs(distance), time);
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

	public static List<FogModifier> getFogModifiers(World world) {
		return fogAreas;
	}

}
