package com.diamssword.greenresurgence.systems.equipement.effects;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.equipement.AdvEquipmentSlot;
import com.diamssword.greenresurgence.systems.equipement.IEquipmentEffect;
import com.diamssword.greenresurgence.systems.equipement.IEquipmentUpgrade;
import com.diamssword.greenresurgence.systems.equipement.UpgradeActionContext;
import com.diamssword.greenresurgence.systems.equipement.utils.TooltipHelper;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

import static net.minecraft.item.ItemStack.MODIFIER_FORMAT;

public class FireEffectUpgrade implements IEquipmentEffect {
	private final String id;

	public FireEffectUpgrade(String id) {this.id = id;}

	@Override
	public void getAttributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> map, AdvEquipmentSlot slot, UpgradeActionContext ctx) {

	}

	@Override
	public void onInteraction(UpgradeActionContext ctx, AdvEquipmentSlot slot, IEquipmentUpgrade.InteractType interaction) {
		if(interaction == IEquipmentUpgrade.InteractType.POST_ATTACK && slot == AdvEquipmentSlot.MAINHAND && !ctx.isClient()) {
			float cooldownProgress = 1f;
			if(ctx.getLivingSource() instanceof PlayerEntity pl) {
				cooldownProgress = pl.getComponent(Components.PLAYER_DATA).lastCooldownProgress;
			}
			if(cooldownProgress > 0.9f) { //only crit if weapon is 90% loaded
				if(ctx.getTarget() != null)
					ctx.getTarget().setOnFireFor((int) ctx.getLevel(id).getLevel());
			}
		}
	}


	@Override
	public void addTooltips(UpgradeActionContext ctx, AdvEquipmentSlot slot, List<Text> tooltip) {
		if(slot == AdvEquipmentSlot.MAINHAND || slot == AdvEquipmentSlot.DISPLAY) {
			if(ctx.getLevel(id).getLevel() > 0f) {

				tooltip.add(TooltipHelper.tooltipEffectWithExtra(Text.translatable("equipment." + GreenResurgence.ID + ".tooltip.effect.flame", Text.translatable("equipment." + GreenResurgence.ID + ".tooltip.time", MODIFIER_FORMAT.format(ctx.getLevel(id).getLevel()))).formatted(Formatting.LIGHT_PURPLE),
						Text.translatable("equipment." + GreenResurgence.ID + ".upgrade.tooltip.extra.potionApplier.flame", MODIFIER_FORMAT.format(ctx.getLevel(id).getLevel())), ctx.needShowExtra()));
			}
		}
	}
}
