package com.diamssword.greenresurgence.systems.equipement;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class UpgradeActionContext {
	public static enum ItemContext {
		TOOL,
		UPGRADE,
		BLUEPRINT
	}

	protected LivingEntity target;
	protected LivingEntity source;
	protected ItemStack weapon = ItemStack.EMPTY;
	/**
	 * Used mostly to pass and  modify attack damages
	 */
	protected float contextValue = 0f;
	protected float returnValue = 0f;
	public final ItemContext context;
	protected Map<String, EffectLevel> levels = new HashMap<>();

	public UpgradeActionContext(LivingEntity source, LivingEntity target, ItemContext context) {
		this.source = source;
		this.target = target;
		this.context = context;
	}

	public boolean isClient() {
		return source.getWorld().isClient;
	}

	public UpgradeActionContext setLevels(Map<String, EffectLevel> levels) {
		this.levels = levels;
		return this;
	}

	public UpgradeActionContext setContextValue(float value) {
		this.contextValue = value;
		return this;
	}

	public float getContextValue() {
		return contextValue;
	}

	public ItemStack getWeapon() {
		return weapon;
	}

	public float getReturnValue() {
		return returnValue;
	}

	public UpgradeActionContext setReturnValue(float value) {
		this.returnValue = value;
		return this;
	}

	public UpgradeActionContext setWeapon(ItemStack weapon) {
		this.weapon = weapon;
		return this;
	}

	public UpgradeActionContext setLevel(String id, EffectLevel level) {
		levels.put(id, level);
		return this;
	}

	public EffectLevel getLevel(String id) {
		return levels.getOrDefault(id, new EffectLevel(0f));
	}

	public LivingEntity getTarget() {
		return target;
	}

	public PlayerEntity getPlayerSource() {
		if(source instanceof PlayerEntity pl)
			return pl;
		return null;
	}


	public LivingEntity getLivingSource() {
		return source;
	}

	public Entity getSource() {
		return source;
	}

	public Map<String, EffectLevel> getLevels() {
		return levels;
	}
}
