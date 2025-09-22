package com.diamssword.greenresurgence.utils;

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
}
