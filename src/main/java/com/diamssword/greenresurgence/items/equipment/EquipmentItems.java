package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.datagen.LangGenerator;
import com.diamssword.greenresurgence.items.equipment.upgrades.EquipmentBatteryUpgrade;
import com.diamssword.greenresurgence.items.equipment.upgrades.EquipmentSkinItem;
import com.diamssword.greenresurgence.materials.BatteryTiers;
import com.diamssword.greenresurgence.systems.equipement.EquipmentEffects;
import com.diamssword.greenresurgence.systems.equipement.Equipments;
import com.diamssword.greenresurgence.systems.equipement.effects.SweepingEffectUpgrade;
import com.diamssword.greenresurgence.systems.equipement.utils.MapEffectMaker;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;

public class EquipmentItems implements ItemRegistryContainer {

	public static final Item SKIN_MODIFIER = new EquipmentSkinItem();
	public static final Item BATTERY_MODIFIER = new EquipmentBatteryUpgrade("electric/*", BatteryTiers.BATTERY);
	public static final Item BATTERY_MODIFIER_T2 = new EquipmentBatteryUpgrade("electric/*", BatteryTiers.LIPO);
	public static final Item DAMAGE_MODIFIER = new EquipmentUpgradeItem("blade/*,hammer/*", Equipments.P_HEAD, 100, 1).setEffect(MapEffectMaker.create(EquipmentEffects.ATTACK_DAMAGE, 2f));
	public static final Item SPEED_MODIFIER = new EquipmentUpgradeItem("blade/*,hammer/*", Equipments.P_HANDLE, 100, 1).setEffect(MapEffectMaker.create(EquipmentEffects.ATTACK_SPEED, 1.1f));
	public static final Item SWEEP_MODIFIER = new EquipmentUpgradeItem("hammer/*", Equipments.P_BINDING, 100, 1).setEffect(MapEffectMaker.create().add(EquipmentEffects.SWEEPING, 1f, SweepingEffectUpgrade.DAMAGE_BONUS, 20));
	public static final Item HAMMER_MODIFIER = new EquipmentUpgradeItem("*/short,*/medium", Equipments.P_HEAD, 100, 1).setEffect(MapEffectMaker.create(EquipmentEffects.TOOL_HAMMER, 1));

	public static final Item BASIC_BINDING = new EquipmentUpgradeItem("*/*", Equipments.P_BINDING, 200, 2);
	public static final Item BASIC_BLADE = new EquipmentUpgradeItem("spike/*,blade/*", Equipments.P_HEAD, 50, 1).setEffect(MapEffectMaker.create().add(EquipmentEffects.ATTACK_DAMAGE, 4f));
	public static final Item BASIC_HEAD = new EquipmentUpgradeItem("hammer/*", Equipments.P_HEAD, 50, 1).setEffect(MapEffectMaker.create().add(EquipmentEffects.ATTACK_DAMAGE, 3f).add(EquipmentEffects.SWEEPING, 2, SweepingEffectUpgrade.DAMAGE_BONUS, 12.5f));
	public static final Item BASIC_HANDLE = new EquipmentUpgradeItem("*/*", Equipments.P_HANDLE, 80, 1).setEffect(MapEffectMaker.create().add(EquipmentEffects.ATTACK_SPEED, 0.2f));

	@Override
	public void postProcessField(String namespace, Item value, String identifier, Field field) {
		LangGenerator.auto_name.put(new Identifier(namespace, "equipments/" + identifier), identifier);
	}
}
