package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.equipement.IEquipmentBlueprint;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

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

}
