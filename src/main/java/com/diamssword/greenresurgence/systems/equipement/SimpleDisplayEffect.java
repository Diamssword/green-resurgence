package com.diamssword.greenresurgence.systems.equipement;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.equipement.utils.TooltipHelper;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

import static net.minecraft.item.ItemStack.MODIFIER_FORMAT;

public class SimpleDisplayEffect implements IEquipmentEffect {
	public final String id;
	private final boolean addPlus;

	public SimpleDisplayEffect(String id) {
		this.id = id;
		this.addPlus = false;
	}

	public SimpleDisplayEffect(String id, boolean addPlus) {
		this.id = id;
		this.addPlus = addPlus;
	}

	@Override
	public void getAttributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> map, AdvEquipmentSlot slot, UpgradeActionContext ctx) {

	}

	@Override
	public void onInteraction(UpgradeActionContext ctx, AdvEquipmentSlot slot, IEquipmentUpgrade.InteractType interaction) {

	}

	@Override
	public void addTooltips(UpgradeActionContext ctx, AdvEquipmentSlot slot, List<Text> tooltip) {
		var lvl = ctx.getLevel(id).getLevel();
		if(lvl > 0) {

			var txt = TooltipHelper.tooltipEffectWithExtra(id, ctx.needShowExtra(), MODIFIER_FORMAT.format(lvl), MODIFIER_FORMAT.format(lvl));
			;
			Text.translatable("attribute.modifier." + (ctx.context == UpgradeActionContext.ItemContext.UPGRADE ? "plus" : "equals") + ".0", MODIFIER_FORMAT.format(lvl), Text.translatable("equipment." + GreenResurgence.ID + ".upgrade.tooltip." + id));
			if(addPlus)
				txt = TooltipHelper.plusIfUpgrade(txt, ctx.context);
			tooltip.add(txt.formatted(Formatting.BLUE));
		}
	}
}
