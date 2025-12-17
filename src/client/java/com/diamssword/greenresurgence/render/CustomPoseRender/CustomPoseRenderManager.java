package com.diamssword.greenresurgence.render.CustomPoseRender;

import com.diamssword.greenresurgence.systems.character.PosesManager;

import java.util.HashMap;
import java.util.Map;

public class CustomPoseRenderManager {
	private static Map<String, ICustomPoseRenderer> renderers = new HashMap<>();

	static {
		renderers.put(PosesManager.CARRIED, new CarriedRenderer());
		renderers.put(PosesManager.TWOHANDWIELD, new TwoHandWieldRenderer());
		renderers.put(PosesManager.CARRYINGENTITY, new CarryingPoseRenderer());
		renderers.put(PosesManager.PUSHINGCART, new PushingCartRenderer());
		renderers.put(PosesManager.RIDING_BIKE, new RidingBikeRenderer());
	}

	public static ICustomPoseRenderer get(String id) {
		return renderers.get(id);
	}
}
