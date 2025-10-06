package com.diamssword.greenresurgence.systems.equipement;

import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.items.equipment.EquipmentBlueprintItem;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;

public class EquipmentDef implements IEquipmentDef {
	public final String type;
	public final String subtype;
	public final String[] slots;
	public final String[] requiredSlots;
	private final EquipmentBlueprintItem bp;
	public final Item tool;

	public EquipmentDef(String type, String subType, BiFunction<String, String, Item> toolConstructor, String[] requiredSlots, String... extraSlots) {
		this.type = type;
		this.subtype = subType;
		var ls = new ArrayList<>(Arrays.asList(requiredSlots));
		ls.addAll(Arrays.asList(extraSlots));
		this.slots = ls.toArray(new String[0]);
		this.requiredSlots = requiredSlots;
		this.bp = new EquipmentBlueprintItem(type, subType, new OwoItemSettings().maxCount(8).group(MItems.GROUP).tab(1));
		this.tool = toolConstructor.apply(type, subType);
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

	@Override
	public String[] getRequiredSlots() {
		return requiredSlots;
	}
}
