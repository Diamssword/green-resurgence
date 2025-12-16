package com.diamssword.greenresurgence.systems.equipement.utils;

import com.diamssword.greenresurgence.systems.equipement.EffectLevel;

import java.util.HashMap;
import java.util.Map;

public class MapEffectMaker {
	Map<String, EffectLevel> map = new HashMap<>();

	protected MapEffectMaker() {

	}

	public MapEffectMaker add(String effect, float level) {
		this.map.put(effect, new EffectLevel(level));
		return this;
	}

	public MapEffectMaker add(String effect, float mainValue, String secondLevel, float secondValue) {
		this.map.put(effect, new EffectLevel(mainValue).put(secondLevel, secondValue));
		return this;
	}

	public MapEffectMaker add(String effect, float mainValue, String secondLevel, float secondValue, String thridLevel, float thridValue) {
		this.map.put(effect, new EffectLevel(mainValue).put(secondLevel, secondValue).put(thridLevel, thridValue));
		return this;
	}

	public static MapEffectMaker create(String effect, float level) {
		return new MapEffectMaker().add(effect, level);
	}

	public static MapEffectMaker create() {
		return new MapEffectMaker();
	}

	public Map<String, EffectLevel> get() {
		return map;
	}
}
