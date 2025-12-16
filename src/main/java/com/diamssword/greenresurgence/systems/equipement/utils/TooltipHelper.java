package com.diamssword.greenresurgence.systems.equipement.utils;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.items.equipment.EquipmentTwoHanded;
import com.diamssword.greenresurgence.systems.equipement.AdvEquipmentSlot;
import com.diamssword.greenresurgence.systems.equipement.UpgradeActionContext;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class TooltipHelper {
	public static void appendUpgradeHeader(Item item, AdvEquipmentSlot slot, UpgradeActionContext.ItemContext context, List<Text> tooltip) {

		if(context == UpgradeActionContext.ItemContext.UPGRADE) {
			tooltip.add(ScreenTexts.EMPTY);
			tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".tooltip.title.upgrade").formatted(Formatting.GRAY));
		} else if(slot == AdvEquipmentSlot.MAINHAND) {
			tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".tooltip.equipment_type." + (item instanceof EquipmentTwoHanded ? "two_hand" : "dual_wield")).formatted(Formatting.GRAY));
			tooltip.add(ScreenTexts.EMPTY);
		}
		if(context == UpgradeActionContext.ItemContext.BLUEPRINT) {
			tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".tooltip.title.base_stats").formatted(Formatting.GRAY));
		}

	}
}
