package com.diamssword.greenresurgence.systems.equipement;

import com.diamssword.greenresurgence.GreenResurgence;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class TooltipHelper {
	public static void appendUpgradeHeader(AdvEquipmentSlot slot, boolean isUpgrade, List<Text> tooltip) {
		tooltip.add(ScreenTexts.EMPTY);
		if(isUpgrade)
			tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".upgrade.modifiers." + slot.getName()).formatted(Formatting.GRAY));
		else
			tooltip.add(Text.translatable("item.modifiers." + slot.getName()).formatted(Formatting.GRAY));
	}
}
