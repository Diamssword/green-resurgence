package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.systems.equipement.IEquipmentBlueprint;
import net.minecraft.item.Item;

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
}
