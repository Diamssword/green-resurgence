package com.diamssword.greenresurgence.systems.equipement;

import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;

public interface IUpgradableEquipment {
	String getEquipmentType();

	String getEquipmentSubtype();

	String getSkin();

	Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(AdvEquipmentSlot slot, @Nullable PlayerEntity player);

	void onInteraction(PlayerEntity wearer, AdvEquipmentSlot slot, EquipmentUpgrade.InteractType interaction, HitResult context);

	void onTick(Entity parent);

	default IEquipmentDef getEquipment() {
		return Equipments.getEquipment(getEquipmentType(), getEquipmentSubtype()).get();
	}
}
