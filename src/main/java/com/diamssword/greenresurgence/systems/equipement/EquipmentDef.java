package com.diamssword.greenresurgence.systems.equipement;

import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.items.equipment.EquipmentBlueprintItem;
import com.diamssword.greenresurgence.items.equipment.EquipmentTool;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.item.Item;

public class EquipmentDef implements IEquipmentDef {
	public final String type;
	public final String subtype;
	public final String[] slots;
	private final EquipmentBlueprintItem bp;
	private final EquipmentTool tool;

	public EquipmentDef(String type, String subType, String... slots) {
		this.type = type;
		this.subtype = subType;
		this.slots = slots;
		this.bp = new EquipmentBlueprintItem(type, subType, new OwoItemSettings().maxCount(8).group(MItems.GROUP).tab(1));
		this.tool = new EquipmentTool(type, subType);
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
	public Item getBlueprintItem() {
		return bp;
	}

	@Override
	public Item getEquipmentItem() {
		return tool;
	}

	@Override
	public String[] getSlots() {
		return slots;
	}

}
