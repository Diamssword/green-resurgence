package com.diamssword.greenresurgence.render.customPose;

import com.diamssword.greenresurgence.systems.character.PosesManager;
import com.diamssword.greenresurgence.systems.character.customPoses.AnimatedPose;
import com.diamssword.greenresurgence.systems.character.customPoses.IPlayerCustomPose;

import java.util.HashMap;
import java.util.Map;

public class CustomPoseRenderManager {
	private static Map<String, ICustomPoseRenderer> renderers = new HashMap<>();
	private static AnimatedPosRenderer animatedPosRendererInstance = new AnimatedPosRenderer();

	static {
		renderers.put(PosesManager.CARRIED, new CarriedRenderer());
		renderers.put(PosesManager.TWOHANDWIELD, new TwoHandWieldRenderer());
		renderers.put(PosesManager.CARRYINGENTITY, new CarryingPoseRenderer());
		renderers.put(PosesManager.PUSHINGCART, new PushingCartRenderer());
		renderers.put(PosesManager.RIDING_BIKE, new RidingBikeRenderer());
		renderers.put(PosesManager.SIT_EMOTE, new SittingRenderer());
		renderers.put(PosesManager.LAY_EMOTE, new LayingRenderer());
	}

	public static ICustomPoseRenderer get(String id, IPlayerCustomPose re) {
		var r = renderers.get(id);
		if(r != null)
			return r;
		if(re instanceof AnimatedPose an) {
			return animatedPosRendererInstance;
		}
		return null;
	}
}
