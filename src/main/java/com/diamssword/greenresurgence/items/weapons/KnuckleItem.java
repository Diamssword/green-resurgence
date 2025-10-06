package com.diamssword.greenresurgence.items.weapons;

import com.diamssword.greenresurgence.systems.character.PosesManager;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

import java.util.UUID;

public class KnuckleItem extends SwordItem implements ICustomPoseWeapon {

	protected static final UUID ATTACK_DAMAGE_MODIFIER_ID = UUID.fromString("ca8d2dbf-20c8-43d0-9a19-7fb38c498c01");
	protected static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.fromString("498d42ad-d6d5-43d1-9d17-4a5977bffcee");
	private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiersOffHand;

	public KnuckleItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
		super(toolMaterial, attackDamage, attackSpeed, settings);
		ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier Offhand", (double) this.getAttackDamage() / 2f, EntityAttributeModifier.Operation.ADDITION));
		builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier Offhand", (double) Math.abs(attackSpeed) / 2f, EntityAttributeModifier.Operation.ADDITION));
		this.attributeModifiersOffHand = builder.build();
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.OFFHAND ? attributeModifiersOffHand : super.getAttributeModifiers(slot);
	}

	@Override
	public boolean shouldRemoveOffHand() {
		return false;
	}

	@Override
	public String customPoseId(ItemStack stack) {
		return PosesManager.KNUCLESHANDWIELD;
	}

	@Override
	public int customPoseMode(ItemStack stack) {
		return 0;
	}
}
