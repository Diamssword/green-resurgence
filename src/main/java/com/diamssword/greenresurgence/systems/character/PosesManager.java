package com.diamssword.greenresurgence.systems.character;

import com.diamssword.greenresurgence.systems.character.customPoses.*;
import net.minecraft.entity.player.PlayerEntity;

import java.util.*;
import java.util.function.Function;

public class PosesManager {

	private static final Map<String, Function<PlayerEntity, IPlayerCustomPose>> posesRegister = new HashMap<>();
	public static final List<EmoteDef> emotes = new ArrayList<>();
	public static final String CARRIED = register("carried", BeingCarriedPose::new);
	public static final String TWOHANDWIELD = register("two_hand_wield", TwoHandWield::new);
	public static final String CARRYINGENTITY = register("carrying_entity", CarryingPose::new);
	public static final String PUSHINGCART = register("pushing_cart", PushingCartPose::new);
	public static final String RIDING_BIKE = register("riding_bike", PushingCartPose::new);
	public static final String WAVE_EMOTE = asEmote(registerAnimated("wave_emote", IPlayerCustomPose.PoseType.ARMS));
	public static final String DOUBLE_WAVE_EMOTE = asAltEmote(WAVE_EMOTE, registerAnimated("double_wave_emote", IPlayerCustomPose.PoseType.ARMS));
	public static final String FACEPALM_EMOTE = asEmote(registerAnimated("facepalm_emote", IPlayerCustomPose.PoseType.ARMS));
	public static final String CRY_EMOTE = asEmote(registerAnimated("cry_emote", IPlayerCustomPose.PoseType.ARMS));
	public static final String SIT_EMOTE = asEmote(register("sit_emote", SittingPose::new));
	public static final String LAY_EMOTE = asAltEmote(SIT_EMOTE, register("lay_emote", LayingPose::new));

	private static String register(String id, Function<PlayerEntity, IPlayerCustomPose> factory) {
		posesRegister.put(id, factory);
		return id;
	}

	private static String asAltEmote(String main, String id) {
		for(EmoteDef emote : emotes) {
			if(emote.poseID.equals(main)) {
				emote.setAlt(new EmoteDef(id));
				break;
			}
		}
		return id;
	}

	private static String asEmote(String id) {
		emotes.add(new EmoteDef(id));
		return id;
	}

	private static String registerAnimated(String id, IPlayerCustomPose.PoseType type) {
		posesRegister.put(id, p -> new AnimatedPose(p, type, id));
		return id;
	}

	public static IPlayerCustomPose createPose(String id, PlayerEntity player) {
		if(posesRegister.containsKey(id)) {
			return posesRegister.get(id).apply(player);
		}
		return null;
	}

	public static class EmoteDef {
		public final String poseID;
		private EmoteDef alt;

		public EmoteDef(String poseId) {
			this.poseID = poseId;
		}

		public Optional<EmoteDef> getAlt() {
			return Optional.ofNullable(alt);
		}

		public EmoteDef setAlt(EmoteDef alt) {
			this.alt = alt;
			return this;
		}
	}
}
