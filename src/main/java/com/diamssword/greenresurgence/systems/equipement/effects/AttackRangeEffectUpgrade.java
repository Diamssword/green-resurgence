package com.diamssword.greenresurgence.systems.equipement.effects;

import com.diamssword.greenresurgence.systems.equipement.*;
import com.google.common.collect.Multimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;

public class AttackRangeEffectUpgrade implements IEquipmentEffect {

	@Override
	public void getAttributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> map, AdvEquipmentSlot slot, UpgradeActionContext ctx) {
		if(slot == AdvEquipmentSlot.MAINHAND) {
			var eff = ctx.getLevel(EquipmentEffects.ATTACK_RANGE);
			if(eff.getLevel() != 0) {

				map.put(ReachEntityAttributes.ATTACK_RANGE, new EntityAttributeModifier(EquipmentValues.ATTACK_RANGE_MODIFIER_ID, "Weapon modifier", eff.getLevel(), EntityAttributeModifier.Operation.ADDITION));
				//attack range higher than block reach is ignored, so we add reach only if needed
				if(eff.getLevel() > 1.5)
					map.put(ReachEntityAttributes.REACH, new EntityAttributeModifier(EquipmentValues.REACH_RANGE_MODIFIER_ID, "Weapon modifier", eff.getLevel(), EntityAttributeModifier.Operation.ADDITION));
			}
		}
	}

	@Override
	public void onInteraction(UpgradeActionContext ctx, AdvEquipmentSlot slot, IEquipmentUpgrade.InteractType interaction) {

	}

}
