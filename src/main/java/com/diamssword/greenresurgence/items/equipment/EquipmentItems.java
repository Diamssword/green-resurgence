package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.datagen.LangGenerator;
import com.diamssword.greenresurgence.items.equipment.upgrades.EquipmentSkinItem;
import com.diamssword.greenresurgence.systems.equipement.AdvEquipmentSlot;
import com.diamssword.greenresurgence.systems.equipement.Equipments;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class EquipmentItems implements ItemRegistryContainer {

	public static final Item SKIN_MODIFIER = new EquipmentSkinItem();
	public static final Item DAMAGE_MODIFIER = new EquipmentUpgradeItem("blade/*", Equipments.P_BLADE, 100, 1) {
		@Override
		public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(AdvEquipmentSlot slot, @Nullable PlayerEntity player) {
			if(slot == AdvEquipmentSlot.MAINHAND) {
				ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
				return builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", 5, EntityAttributeModifier.Operation.ADDITION)).build();
			}
			return null;
		}

		@Override
		public void onInteraction(PlayerEntity wearer, AdvEquipmentSlot slot, InteractType interaction, HitResult context) {

		}

		@Override
		public void onTick(Entity parent) {

		}
	};
	public static final Item SPEED_MODIFIER = new EquipmentUpgradeItem("blade/short,hammer/*", Equipments.P_HANDLE, 30, 1) {
		@Override
		public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(AdvEquipmentSlot slot, @Nullable PlayerEntity player) {
			if(slot == AdvEquipmentSlot.MAINHAND) {
				ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
				return builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", 5, EntityAttributeModifier.Operation.ADDITION)).build();
			}
			return null;
		}

		@Override
		public void onInteraction(PlayerEntity wearer, AdvEquipmentSlot slot, InteractType interaction, HitResult context) {

		}

		@Override
		public void onTick(Entity parent) {

		}
	};

	@Override
	public void postProcessField(String namespace, Item value, String identifier, Field field) {
		LangGenerator.auto_name.put(new Identifier(namespace, "equipments/" + identifier), identifier);
	}
}
