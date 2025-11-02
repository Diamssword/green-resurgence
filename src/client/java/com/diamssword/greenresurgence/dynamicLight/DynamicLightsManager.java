package com.diamssword.greenresurgence.dynamicLight;

import dev.lambdaurora.lambdynlights.api.behavior.DynamicLightBehavior;
import dev.lambdaurora.lambdynlights.api.behavior.DynamicLightBehaviorManager;

public class DynamicLightsManager {

	public static DynamicLightsManager INSTANCE;
	public final DynamicLightBehaviorManager manager;

	public DynamicLightsManager(DynamicLightBehaviorManager manager) {
		this.manager = manager;
	}

	public void addLightSource(DynamicLightBehavior lightSource) {

		manager.add(lightSource);

	}

	public void removeLightSource(DynamicLightBehavior lightSource) {

		manager.remove(lightSource);

	}
}
