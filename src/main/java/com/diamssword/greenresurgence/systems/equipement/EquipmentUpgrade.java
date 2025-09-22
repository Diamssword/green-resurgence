package com.diamssword.greenresurgence.systems.equipement;

import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;

public interface EquipmentUpgrade {
	boolean canBeApplied(IEquipmentDef equipment, ItemStack stack);

	String slot(IEquipmentDef equipment);

	Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(AdvEquipmentSlot slot, @Nullable PlayerEntity player);

	void onInteraction(PlayerEntity wearer, AdvEquipmentSlot slot, InteractType interaction, HitResult context);

	/**
	 * The chance for this upgrade to take damage instead of others
	 */
	float damageWheight();

	void onTick(Entity parent);

	enum InteractType {
		ATTACK,
		ATTACKED,
		BREAK,
		INTERACT
	}
}
