package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.datagen.LangGenerator;
import com.diamssword.greenresurgence.items.equipment.upgrades.EquipmentBatteryUpgrade;
import com.diamssword.greenresurgence.items.equipment.upgrades.EquipmentHidenSlotUpgrade;
import com.diamssword.greenresurgence.items.equipment.upgrades.EquipmentSkinItem;
import com.diamssword.greenresurgence.items.materials.BatteryTiers;
import com.diamssword.greenresurgence.systems.equipement.EquipmentEffects;
import com.diamssword.greenresurgence.systems.equipement.Equipments;
import com.diamssword.greenresurgence.systems.equipement.effects.ArmorEffectUpgrade;
import com.diamssword.greenresurgence.systems.equipement.effects.SweepingEffectUpgrade;
import com.diamssword.greenresurgence.systems.equipement.utils.MapEffectMaker;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;

public class EquipmentItems implements ItemRegistryContainer {


	public static final Item SKIN_MODIFIER = new EquipmentSkinItem();

	//BINDINGS
	public static final Item GLUE_BINDING = makeBinding(100, 8, -0.5f, 0.3f, 0);
	public static final Item ROPE_BINDING = makeBinding(200, 4, 0, 0, 0);
	public static final Item PRAY_BINDING = makeBinding(50, 6, 0.5f, 0.3f, 0);

	public static final Item SCOTCH_BINDING = makeBinding(300, 6, 0, 0, 0);
	public static final Item PLUS_COLLE_BINDING = makeBinding(150, 6, 0, 0.2f, 0);
	public static final Item CLOUE_BINDING = makeBinding(150, 5, 0.3f, 0, 0);

	public static final Item MOLTEN_IRON_BINDING = makeBinding(600, 4, 0.4f, -0.2f, 0);
	public static final Item STRONG_GLUE_BINDING = makeBinding(250, 6, 0, 0.2f, 0);
	public static final Item SCREW_BINDING = makeBinding(400, 5, 0, 0, 0);

	public static final Item RIVET_BINDING = makeBinding(1200, 8, 0, 0, 0);
	public static final Item WELDED_BINDING = makeBinding(900, 10, 0.1f, 0, 0);

	public static final Item NANO_BINDING = makeBinding(1800, 20, 0, 0, 0);
	public static final Item NEJIRI_ARIGATA_BINDING = makeBinding(1400, 12, 0.1f, 0.1f, 0);

	//EXTRA BINDINGS
	public static final Item RENFORCED_BINDING_EXTRA = new EquipmentUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_BINDING_EXTRA, 300, 4);
	public static final Item RED_PAINT_BINDING_EXTRA = new EquipmentUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_BINDING_EXTRA, 150, 1).setEffect(MapEffectMaker.create(EquipmentEffects.MOVE_SPEED, 0.1f));
	public static final Item LUCKY_CHARM_BINDING_EXTRA = new EquipmentUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_BINDING_EXTRA, 50, 1).setEffect(MapEffectMaker.create(EquipmentEffects.CRITICAL_HIT, 10f));
	public static final Item HAND_GUARD_BINDING_EXTRA = new EquipmentUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_BINDING_EXTRA, 160, 2).setEffect(MapEffectMaker.create(EquipmentEffects.WEAPON_ARMOR_TOUGHNESS, 2f));
	public static final Item MAGNETIC_BINDING_EXTRA = new EquipmentUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_BINDING_EXTRA, 100, 1).setEffect(MapEffectMaker.create(EquipmentEffects.LOYALTY, 1f));
	//TODO public static final Item BANNER_BINDING_EXTRA = new EquipmentUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_BINDING_EXTRA, 80, 1).setEffect(MapEffectMaker.create(EquipmentEffects.WEAPON_ARMOR_TOUGHNESS, 2f));
	public static final Item SECRET_COMPARTIMENT_BINDING_EXTRA = new EquipmentHidenSlotUpgrade("spike/*,blade/*,hammer/*", Equipments.P_BINDING_EXTRA, 80, 1, 2);
	public static final Item EGIDE_SAPLING_BINDING_EXTRA = new EquipmentUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_BINDING_EXTRA, 120, 1).setEffect(MapEffectMaker.create(EquipmentEffects.CONTAMINATION_REDUCTION, 5f));

	//HANDLES
	public static final Item WOOD_HANDLE = makeHandle(25, 1, 0, 0, 0.1f);
	public static final Item BONE_HANDLE = makeHandle(40, 1, 0.1f, -0.1f, 0f);
	public static final Item COPPER_HANDLE = makeHandle(60, 2, -0.1f, -0.1f, -0.1f);

	public static final Item BRONZE_HANDLE = makeHandle(100, 2, 0.1f, 0, 0.1f);
	public static final Item SCRAPPED_IRON_HANDLE = makeHandle(30, 1, 0, 0, 0.3f);
	public static final Item PLASTIC_HANDLE = makeHandle(2, 1, 0, 0.1f, 0.3f);

	public static final Item IRON_HANDLE = makeHandle(50, 1, 0, 0, 0.5f);
	public static final Item STRONG_BONE_HANDLE = makeHandle(40, 1, 0.2f, 0, 0.3f);
	public static final Item LAQUED_WOOD_HANDLE = makeHandle(40, 1, 0, 0, 0.6f);

	public static final Item ALUMINUM_HANDLE = makeHandle(50, 1, 0, 0.1f, 0.6f);
	public static final Item STEEL_HANDLE = makeHandle(90, 1, 0, 0, 0.5f);
	public static final Item EGIDE_BARK_HANDLE = makeHandle(80, 1, 0.2f, 0, 0.4f);

	public static final Item EGIDE_WOOD_HANDLE = makeHandle(110, 1, 0.2f, 0f, 0.5f);
	public static final Item PLASTEEL_HANDLE = makeHandle(90, 1, 0, 0.2f, 0.6f);


	//HEAD
	public static final Item WOOD_HEAD = makeHead(500, 4, -50, 0, 0);
	public static final Item COPPER_HEAD = makeHead(40, 1, 0.2f, -0.2f, -0.1f);
	public static final Item STONE_HEAD = makeHead(25, 1, 0, 0, 0);

	public static final Item BONE_HEAD = makeHead(25, 1, 0.6f, 0, 0);
	public static final Item BRONZE_HEAD = makeHead(50, 1, 0.6f, -0.1f, -0.1f);
	public static final Item SCRAPPED_IRON_HEAD = makeHead(30, 1, 0.5f, 0, 0.1f);

	public static final Item IRON_HEAD = makeHead(50, 1, 0.7f, 0, 0.2f);
	public static final Item STRONG_BONE_HEAD = makeHead(40, 1, 0.8f, 0, 0);
	public static final Item LAQUED_WOOD_HEAD = makeHead(1000, 4, -50, 0.1f, 0.1f);

	public static final Item ALUMINUM_HEAD = makeHead(50, 1, 0.8f, 0.2f, 0.1f);
	public static final Item STEEL_HEAD = makeHead(90, 1, 1, 0, 0.2f);
	public static final Item EGIDE_BARK_HEAD = makeHead(80, 1, 1.2f, 0.1f, 0);

	public static final Item EGIDE_WOOD_HEAD = makeHead(110, 1, 1.6f, 0.1f, 0);
	public static final Item PLASTEEL_HEAD = makeHead(90, 1, 1.2f, 0.4f, 0.1f);

	public static final Item BATTERY_MODIFIER = new EquipmentBatteryUpgrade("electric/*", BatteryTiers.BATTERY, BatteryTiers.LIPO, 2);
	public static final Item BATTERY_MODIFIER_T2 = new EquipmentBatteryUpgrade("electric/*", BatteryTiers.LIPO, BatteryTiers.LIPO, 4);

	public static final Item SWEEP_MODIFIER = new EquipmentUpgradeItem("hammer/*", Equipments.P_BINDING, 100, 1).setEffect(MapEffectMaker.create().add(EquipmentEffects.SWEEPING, 1f, SweepingEffectUpgrade.DAMAGE_BONUS, 20));
	public static final Item HAMMER_MODIFIER = new EquipmentUpgradeItem("*/short,*/medium", Equipments.P_HEAD, 100, 1).setEffect(MapEffectMaker.create(EquipmentEffects.TOOL_HAMMER, 1));


	public static final Item BASIC_PADDING = new EquipmentUpgradeItem("armor/*", Equipments.P_PADDING, 200, 1).setEffect(MapEffectMaker.create().add(EquipmentEffects.ARMOR, 0f, ArmorEffectUpgrade.ARMOR_TOUGHNESS, 2f));
	public static final Item LEATHER_PLATING = new EquipmentUpgradeItem("armor/*", Equipments.P_PLATING, 200, 1).setEffect(MapEffectMaker.create().add(EquipmentEffects.ARMOR, 1f));
	public static final Item IRON_PLATING = new EquipmentUpgradeItem("armor/*", Equipments.P_PLATING, 500, 1).setEffect(MapEffectMaker.create().add(EquipmentEffects.ARMOR, 2f));
	public static final Item DIAMOND_PLATING = new EquipmentUpgradeItem("armor/*", Equipments.P_PLATING, 800, 1).setEffect(MapEffectMaker.create().add(EquipmentEffects.ARMOR, 3f));
	public static final Item BASIC_FRAME = new EquipmentUpgradeItem("armor/*", Equipments.P_FRAME, 300, 2);
	public static final Item TOUGH_FRAME = new EquipmentUpgradeItem("armor/*", Equipments.P_FRAME, 1800, 4);

	@Override
	public void postProcessField(String namespace, Item value, String identifier, Field field) {
		LangGenerator.auto_name.put(new Identifier(namespace, "equipments/" + identifier), identifier);
	}

	private static Item makeBasic(String slot, String whitelist, int dura, float weight, float attack, float speed, float reach) {
		var mp = MapEffectMaker.create();
		if(attack != 0)
			mp.add(EquipmentEffects.ATTACK_DAMAGE, attack);
		if(speed != 0)
			mp.add(EquipmentEffects.ATTACK_SPEED, speed);
		if(reach != 0)
			mp.add(EquipmentEffects.ATTACK_RANGE, reach);
		return new EquipmentUpgradeItem(whitelist, slot, dura, weight).setEffect(mp);
	}

	private static Item makeHandle(int dura, float weight, float attack, float speed, float reach) {
		return makeBasic(Equipments.P_HANDLE, "spike/*,blade/*,hammer/*", dura, weight, attack, speed, reach);
	}

	private static Item makeHead(int dura, float weight, float attack, float speed, float reach) {
		return makeBasic(Equipments.P_HEAD, "spike/*,blade/*,hammer/*", dura, weight, attack, speed, reach);
	}

	private static Item makeBinding(int dura, float weight, float attack, float speed, float reach) {
		return makeBasic(Equipments.P_BINDING, "spike/*,blade/*,hammer/*", dura, weight, attack, speed, reach);
	}
}
