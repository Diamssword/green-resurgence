package com.diamssword.greenresurgence.systems.equipement.effects;

import com.diamssword.greenresurgence.systems.equipement.*;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;

public class AttackSpeedEffectUpgrade implements IEquipmentEffect {

	@Override
	public void getAttributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> map, AdvEquipmentSlot slot, UpgradeActionContext ctx) {
		if(slot == AdvEquipmentSlot.MAINHAND) {
			var eff = ctx.getLevel(EquipmentEffects.ATTACK_SPEED);
			if(eff.getLevel() != 0)
				map.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(EquipmentValues.ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", eff.getLevel(), EntityAttributeModifier.Operation.ADDITION));
		}
	}

	@Override
	public void onInteraction(UpgradeActionContext ctx, AdvEquipmentSlot slot, IEquipmentUpgrade.InteractType interaction) {

	}

}
