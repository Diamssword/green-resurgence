package com.diamssword.greenresurgence.systems.equipement;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class UpgradeActionContext {
	public static enum ItemContext {
		TOOL,
		UPGRADE
	}

	protected LivingEntity target;
	protected PlayerEntity source;
	public final ItemContext context;
	protected Map<String, EffectLevel> levels = new HashMap<>();

	public UpgradeActionContext(PlayerEntity source, LivingEntity target, ItemContext context) {
		this.source = source;
		this.target = target;
		this.context = context;
	}

	public UpgradeActionContext setLevels(Map<String, EffectLevel> levels) {
		this.levels = levels;
		return this;
	}

	public UpgradeActionContext setLevel(String id, EffectLevel level) {
		levels.put(id, level);
		return this;
	}

	public EffectLevel getLevel(String id) {
		return levels.getOrDefault(id, new EffectLevel(0));
	}

	public LivingEntity getTarget() {
		return target;
	}

	public PlayerEntity getPlayerSource() {
		return source;
	}

	public Entity getSource() {
		return source;
	}

	public Map<String, EffectLevel> getLevels() {
		return levels;
	}
}
