package com.diamssword.greenresurgence.systems.equipement;

import net.minecraft.item.Item;

public interface IEquipmentDef {
	String getEquipmentType();

	String getEquipmentSubtype();

	Item getBlueprintItem();

	Item getEquipmentItem();

	String[] getSlots();

}
