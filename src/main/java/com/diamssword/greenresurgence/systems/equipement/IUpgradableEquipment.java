package com.diamssword.greenresurgence.systems.equipement;

import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IUpgradableEquipment {
	String getEquipmentType();

	String getEquipmentSubtype();

	String getSkin();

	Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(AdvEquipmentSlot slot, @Nullable LivingEntity player);

	float onInteraction(LivingEntity wearer, AdvEquipmentSlot slot, IEquipmentUpgrade.InteractType interaction, HitResult context);

	void onTick(Entity parent, AdvEquipmentSlot slot);

	default IEquipmentDef getEquipment() {
		return Equipments.getEquipment(getEquipmentType(), getEquipmentSubtype()).get();
	}

	List<TagKey<Item>> getTags();
}
