package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.datagen.LangGenerator;
import com.diamssword.greenresurgence.items.equipment.upgrades.EquipmentBatteryUpgrade;
import com.diamssword.greenresurgence.items.equipment.upgrades.EquipmentSkinItem;
import com.diamssword.greenresurgence.materials.BatteryTiers;
import com.diamssword.greenresurgence.systems.equipement.Equipments;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;

public class EquipmentItems implements ItemRegistryContainer {

	public static final Item SKIN_MODIFIER = new EquipmentSkinItem();
	public static final Item BATTERY_MODIFIER = new EquipmentBatteryUpgrade("electric/*", BatteryTiers.BATTERY);
	public static final Item BATTERY_MODIFIER_T2 = new EquipmentBatteryUpgrade("electric/*", BatteryTiers.LIPO);
	public static final Item DAMAGE_MODIFIER = new EquipmentUpgradeItem("blade/*,hammer/*", Equipments.P_BLADE, 100, 1, "damage");
	public static final Item SPEED_MODIFIER = new EquipmentUpgradeItem("blade/*,hammer/*", Equipments.P_HANDLE, 100, 1, "speed");
	public static final Item SWEEP_MODIFIER = new EquipmentUpgradeItem("hammer/*", Equipments.P_HEAD, 100, 1, "sweeping");
	public static final Item HAMMER_MODIFIER = new EquipmentUpgradeItem("hammer/*", Equipments.P_HEAD, 100, 1, "hammer");

	@Override
	public void postProcessField(String namespace, Item value, String identifier, Field field) {
		LangGenerator.auto_name.put(new Identifier(namespace, "equipments/" + identifier), identifier);
	}
}
