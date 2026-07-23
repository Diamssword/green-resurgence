package com.diamssword.greenresurgence.systems.equipement.utils;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.items.equipment.EquipmentTwoHanded;
import com.diamssword.greenresurgence.systems.equipement.AdvEquipmentSlot;
import com.diamssword.greenresurgence.systems.equipement.UpgradeActionContext;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class TooltipHelper {
	public static void appendUpgradeHeader(Item item, AdvEquipmentSlot slot, UpgradeActionContext.ItemContext context, List<Text> tooltip) {

		if(context == UpgradeActionContext.ItemContext.UPGRADE) {
			tooltip.add(ScreenTexts.EMPTY);
			tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".tooltip.title.upgrade").formatted(Formatting.GRAY));
		} else if(slot == AdvEquipmentSlot.MAINHAND || slot == AdvEquipmentSlot.DISPLAY) {
			tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".tooltip.equipment_type." + (item instanceof EquipmentTwoHanded ? "two_hand" : "dual_wield")).formatted(Formatting.GRAY));
			tooltip.add(ScreenTexts.EMPTY);
		}
		if(context == UpgradeActionContext.ItemContext.BLUEPRINT) {
			tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".tooltip.title.base_stats").formatted(Formatting.GRAY));
		}

	}

	public static List<Text> formatUpgradesList(List<Text> ls) {
		var res = new ArrayList<Text>();
		ls.forEach(l -> {
			res.add(Text.literal("  | ").append(l));
		});
		return res;
	}

	public static MutableText tooltipEffectWithExtra(String translate_base, boolean needExtra, Object basicExtra, Object... extraArgs) {
		var txt = Text.translatable("equipment." + GreenResurgence.ID + ".upgrade.tooltip." + translate_base, basicExtra);
		if(needExtra)
			txt = txt.append(Text.literal(": ").append(Text.translatable("equipment." + GreenResurgence.ID + ".upgrade.tooltip.extra." + translate_base, extraArgs)));
		return txt;
	}

	public static MutableText tooltipEffectWithExtra(MutableText text, Text extra, boolean needExtra) {
		if(needExtra)
			text = text.append(Text.literal(": ").append(extra));
		return text;
	}

	public static String formatlevel(float level) {
		BigDecimal bd = BigDecimal.valueOf(level)
				.setScale(2, RoundingMode.DOWN) // truncate to 2 decimals
				.stripTrailingZeros();          // remove .0 or .00
		return bd.toPlainString();
	}
}
