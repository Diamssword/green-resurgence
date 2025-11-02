package com.diamssword.greenresurgence.dynamicLight;

import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;

public class DynamicLightsInitializer implements dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer {
	@Override
	public void onInitializeDynamicLights(DynamicLightsContext context) {
		DynamicLightsManager.INSTANCE = new DynamicLightsManager(context.dynamicLightBehaviorManager());
	}

	@Override
	public void onInitializeDynamicLights() {

	}
}