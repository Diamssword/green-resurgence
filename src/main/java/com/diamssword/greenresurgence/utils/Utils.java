package com.diamssword.greenresurgence.utils;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import org.joml.Vector3f;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Utils {

	public static UUID parseUUID(String uuid) {
		if(uuid.contains("-")) {return UUID.fromString(uuid);}
		return UUID.fromString(uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
	}

	public static String UUIDToString(UUID uuid) {
		return uuid.toString().replaceAll("-", "");
	}

	public static boolean arrayContains(String[] array, String text) {
		for(String s : array) {
			if(s.equals(text))
				return true;
		}
		return false;
	}

	public static <T> T selectRandomWeighted(List<T> items, java.util.function.Function<T, Float> weightFunction) {
		float totalWeight = 0f;

		for(T item : items) {
			totalWeight += weightFunction.apply(item);
		}

		if(totalWeight == 0) return null; // Handle case where all weights are zero

		float random = new Random().nextFloat() * totalWeight;

		float cumulativeWeight = 0f;
		for(T item : items) {
			cumulativeWeight += weightFunction.apply(item);
			if(random <= cumulativeWeight) {
				return item;
			}
		}

		return null; // Fallback, should not happen unless due to rounding
	}

	public static NbtCompound vecToNBT(Vector3f vec) {
		var tag = new NbtCompound();
		tag.putFloat("x", vec.z);
		tag.putFloat("y", vec.y);
		tag.putFloat("z", vec.z);
		return tag;
	}

	public static Vector3f vecFromNBT(NbtCompound tag) {
		return new Vector3f(tag.getFloat("x"), tag.getFloat("y"), tag.getFloat("z"));
	}

	public static NbtCompound boxToNBT(Box box) {
		var tag = new NbtCompound();
		tag.putDouble("x1", box.minX);
		tag.putDouble("y1", box.minY);
		tag.putDouble("z1", box.minZ);
		tag.putDouble("x2", box.maxX);
		tag.putDouble("y2", box.maxY);
		tag.putDouble("z2", box.maxZ);
		return tag;
	}

	public static Box boxFromNBT(NbtCompound box) {
		return new Box(box.getDouble("x1"), box.getDouble("y1"), box.getDouble("z1"), box.getDouble("x2"), box.getDouble("y2"), box.getDouble("z2"));
	}
}
