package com.diamssword.greenresurgence.items.weapons;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ActivatedSword extends SwordItem implements FabricItem {
	private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiersPowered;

	public ActivatedSword(ToolMaterial toolMaterial, int offDamage, float offSpeed, float onDamage, float onSpeed, Settings settings) {
		super(toolMaterial, offDamage, offSpeed, settings);
		ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", (double) onDamage + toolMaterial.getAttackDamage(), EntityAttributeModifier.Operation.ADDITION));
		builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", onSpeed, EntityAttributeModifier.Operation.ADDITION));
		this.attributeModifiersPowered = builder.build();
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
		if(slot == EquipmentSlot.MAINHAND && stack.getOrCreateNbt().getBoolean("activated"))
			return attributeModifiersPowered;

		return super.getAttributeModifiers(slot);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		var st = user.getStackInHand(hand);
		var comp = st.getOrCreateNbt();
		comp.putBoolean("activated", !comp.getBoolean("activated"));
		st.setNbt(comp);
		user.getItemCooldownManager().set(this, 20);
		return TypedActionResult.consume(st);
	}

}
