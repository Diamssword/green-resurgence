package com.diamssword.greenresurgence.systems.equipement;

import java.util.HashMap;
import java.util.Map;

public class EffectLevel {
	public static final String main = "main";
	protected Map<String, Integer> map = new HashMap<>();

	public EffectLevel(Integer level) {
		this.map.put(main, level);
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

	public Map<String, Integer> getAll() {

		return map;
	}

	public int getLevel() {
		return map.getOrDefault(main, 0);
	}
}
