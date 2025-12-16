package com.diamssword.greenresurgence.systems.equipement;

import java.util.HashMap;
import java.util.Map;

public class EffectLevel {
	public static final String main = "main";
	protected Map<String, Float> map = new HashMap<>();

	public EffectLevel(Float level) {
		this.map.put(main, level);
	}

	public EffectLevel put(String name, float level) {
		this.map.put(name, level);
		return this;
	}

	public EffectLevel add(EffectLevel level) {
		level.getAll().forEach((k, v) -> {
			if(this.map.containsKey(k))
				this.map.put(k, this.map.get(k) + v);
			else
				this.map.put(k, v);
		});
		return this;
	}

	public EffectLevel copy() {
		var eff = new EffectLevel(this.getLevel());
		eff.map.putAll(this.map);
		return eff;
	}

	public Map<String, Float> getAll() {

		return map;
	}

	public float getLevel(String name) {
		return map.getOrDefault(name, 0f);
	}

	public float getLevel(String name, float defaultValue) {
		return map.getOrDefault(name, defaultValue);
	}

	public float getLevel() {
		return map.getOrDefault(main, 0f);
	}
}
