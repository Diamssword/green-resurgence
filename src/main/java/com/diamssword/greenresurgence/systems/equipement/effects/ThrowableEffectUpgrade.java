package com.diamssword.greenresurgence.systems.equipement.effects;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.equipement.*;
import com.diamssword.greenresurgence.systems.equipement.utils.TooltipHelper;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class ThrowableEffectUpgrade implements IEquipmentEffect {

	public static final String MAGNETISM = "throwable_magnetic";

	@Override
	public void getAttributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> map, AdvEquipmentSlot slot, UpgradeActionContext ctx) {

	}

	@Override
	public void onInteraction(UpgradeActionContext ctx, AdvEquipmentSlot slot, IEquipmentUpgrade.InteractType interaction) {

	}

	@Override
	public void addTooltips(UpgradeActionContext ctx, AdvEquipmentSlot slot, List<Text> tooltip) {
		if(slot == AdvEquipmentSlot.MAINHAND || slot == AdvEquipmentSlot.DISPLAY) {

			var l = ctx.getLevel(EquipmentEffects.THROWABLE);

			if(l.getLevel() > 0)
				tooltip.add(TooltipHelper.tooltipEffectWithExtra("throwable", ctx.needShowExtra(), null).formatted(Formatting.LIGHT_PURPLE));
			if(l.getLevel(MAGNETISM, 0) > 0) {
				tooltip.add(TooltipHelper.tooltipEffectWithExtra("magnetism", ctx.needShowExtra(), TooltipHelper.formatlevel(l.getLevel(MAGNETISM))).formatted(Formatting.LIGHT_PURPLE));
				if(l.getLevel() <= 0)
					tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".upgrade.tooltip.throwable.needed").formatted(Formatting.DARK_RED));
			}

		}

	}
}
