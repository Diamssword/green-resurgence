package com.diamssword.greenresurgence.systems.equipement.effects;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.effects.ResurgenceEffects;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.equipement.*;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

import static net.minecraft.item.ItemStack.MODIFIER_FORMAT;

public class OpeningEffectUpgrade implements IEquipmentEffect {

	@Override
	public void getAttributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> map, AdvEquipmentSlot slot, UpgradeActionContext ctx) {

	}

	@Override
	public void onInteraction(UpgradeActionContext ctx, AdvEquipmentSlot slot, IEquipmentUpgrade.InteractType interaction) {
		if(interaction == IEquipmentUpgrade.InteractType.POST_ATTACK && slot == AdvEquipmentSlot.MAINHAND) {
			float cooldownProgress = 1f;
			if(ctx.getLivingSource() instanceof PlayerEntity pl) {
				cooldownProgress = pl.getComponent(Components.PLAYER_DATA).lastCooldownProgress;
			}
			if(cooldownProgress > 0.9f) { //only crit if weapon is 90% loaded CANT DO THAT
				var lvl = ctx.getLevel(EquipmentEffects.OPENING).getLevel();
				if(lvl > 0f) {
					ctx.getTarget().addStatusEffect(new StatusEffectInstance(ResurgenceEffects.HAMMER_OPENING, (int) (lvl * 20), 0, false, false));
				}
			}

		}
	}

	@Override
	public void addTooltips(UpgradeActionContext ctx, AdvEquipmentSlot slot, List<Text> tooltip) {
		if(slot == AdvEquipmentSlot.MAINHAND) {
			if(ctx.getLevel(EquipmentEffects.OPENING).getLevel() > 0f)
				tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".tooltip.effect", ResurgenceEffects.HAMMER_OPENING.getName(), Text.translatable("equipment." + GreenResurgence.ID + ".tooltip.time", MODIFIER_FORMAT.format(ctx.getLevel(EquipmentEffects.OPENING).getLevel()))).formatted(Formatting.BLUE));
		}

	}
}
