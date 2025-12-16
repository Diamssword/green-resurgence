package com.diamssword.greenresurgence.systems.equipement.utils;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;

public class ExtraEntityHitResult extends EntityHitResult {
	public final ItemStack weapon;
	public final float damage;

	public ExtraEntityHitResult(Entity entity, ItemStack weapon, float damage) {
		super(entity);
		this.weapon = weapon;
		this.damage = damage;
	}
}
