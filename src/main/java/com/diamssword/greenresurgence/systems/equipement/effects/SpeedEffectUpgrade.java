package com.diamssword.greenresurgence.systems.equipement.effects;

import com.diamssword.greenresurgence.systems.equipement.*;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;

public class SpeedEffectUpgrade implements IEquipmentEffect {

	@Override
	public void getAttributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> map, AdvEquipmentSlot slot, UpgradeActionContext ctx) {
		if(slot == AdvEquipmentSlot.MAINHAND) {
			map.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(EquipmentAttributes.ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", 5, EntityAttributeModifier.Operation.ADDITION));
		}
	}

	@Override
	public void onInteraction(UpgradeActionContext ctx, AdvEquipmentSlot slot, IEquipmentUpgrade.InteractType interaction) {

	}

}
