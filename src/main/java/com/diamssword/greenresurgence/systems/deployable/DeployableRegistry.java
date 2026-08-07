package com.diamssword.greenresurgence.systems.deployable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class DeployableRegistry {
	private static Map<String, BiFunction<String, ItemStack, AbstractDeployableInstance>> registry = new HashMap<>();

	public static String register(String id, BiFunction<String, ItemStack, AbstractDeployableInstance> factory) {
		registry.put(id, factory);
		return id;
	}

	public static Optional<BiFunction<String, ItemStack, AbstractDeployableInstance>> get(String id) {
		return Optional.ofNullable(registry.get(id));
	}

	public static Optional<AbstractDeployableInstance> instantiate(String id, ItemStack stack) {
		return get(id).map(d -> d.apply(id, stack));
	}

	public static final String CAMPFIRE = register("campfire", (a, b) -> new SimpleDeployableInstance(a, new Box(-0.5, 0, -0.5, 0.5, 1, 0.5)));

}
