package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.equipement.*;
import com.diamssword.greenresurgence.systems.equipement.utils.TooltipHelper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EquipmentBlueprintItem extends Item implements IEquipmentBlueprint {
	private final String subtype;
	private final String type;

	public EquipmentBlueprintItem(String bpType, String bpSubtype, Settings settings) {
		super(settings);
		this.type = bpType;
		this.subtype = bpSubtype;
	}

	@Override
	public String getEquipmentType() {
		return type;
	}

	@Override
	public String getEquipmentSubtype() {
		return subtype;
	}

	@Override
	public Text getName(ItemStack stack) {
		return Text.translatable("item." + GreenResurgence.ID + ".equipments.blueprint").append(Text.literal(" (")).append(Text.translatable("item." + GreenResurgence.ID + ".equipments." + type + "_" + subtype)).append(Text.literal(")"));
	}

	protected void tooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, EquipmentTool equipment) {

		var lvls = equipment.getBaseUpgrades();
		var ctx = new UpgradeActionContext(null, null, UpgradeActionContext.ItemContext.BLUEPRINT, false).setLevels(lvls);
		if(GreenResurgence.clientHelper.isShiftPressed())
			ctx.setShowExtra();
		//for(AdvEquipmentSlot value : AdvEquipmentSlot.values()) {
		List<Text> subList = new ArrayList<>();
		lvls.forEach((k, v) -> {
			var eff = EquipmentEffects.get(k);

			eff.ifPresent(p -> {
				p.addTooltips(ctx, AdvEquipmentSlot.DISPLAY, subList);
			});
		});
		if(!subList.isEmpty()) {
			TooltipHelper.appendUpgradeHeader(this, AdvEquipmentSlot.DISPLAY, ctx.context, tooltip);
			tooltip.addAll(TooltipHelper.formatUpgradesList(subList));
		}
		//}
		if(!GreenResurgence.clientHelper.isShiftPressed()) {
			tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".tooltip.press_shift").formatted(Formatting.GRAY, Formatting.ITALIC));
		}

	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		Equipments.getEquipment(getEquipmentType(), getEquipmentSubtype()).ifPresent(c -> {
			if(c.getEquipmentItem() instanceof EquipmentTool eq)
				tooltip(stack, world, tooltip, eq);
		});

	}

}
