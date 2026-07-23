package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.datagen.LangGenerator;
import com.diamssword.greenresurgence.datagen.ModelGenerator;
import com.diamssword.greenresurgence.items.equipment.upgrades.*;
import com.diamssword.greenresurgence.items.materials.BatteryTiers;
import com.diamssword.greenresurgence.systems.equipement.EquipmentEffects;
import com.diamssword.greenresurgence.systems.equipement.Equipments;
import com.diamssword.greenresurgence.systems.equipement.effects.ArmorEffectUpgrade;
import com.diamssword.greenresurgence.systems.equipement.effects.CriticalHitEffectUpgrade;
import com.diamssword.greenresurgence.systems.equipement.effects.SweepingEffectUpgrade;
import com.diamssword.greenresurgence.systems.equipement.effects.ThrowableEffectUpgrade;
import com.diamssword.greenresurgence.systems.equipement.utils.MapEffectMaker;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class EquipmentItems implements ItemRegistryContainer {


	public static final Item SKIN_MODIFIER = new EquipmentSkinItem();

	//BINDINGS
	@Tier(tier = "1")
	public static final Item GLUE_BINDING = makeBinding(100, 8, -0.5f, 0.3f, 0);
	@Tier(tier = "1")
	public static final Item ROPE_BINDING = makeBinding(200, 4, 0, 0, 0);
	@Tier(tier = "1")
	public static final Item PRAY_BINDING = makeBinding(50, 6, 0.5f, 0.3f, 0);

	@Tier(tier = "2")
	public static final Item SCOTCH_BINDING = makeBinding(300, 6, 0, 0, 0);
	@Tier(tier = "2")
	public static final Item PLUS_COLLE_BINDING = makeBinding(150, 6, 0, 0.2f, 0);
	@Tier(tier = "2")
	public static final Item CLOUE_BINDING = makeBinding(150, 5, 0.3f, 0, 0);

	@Tier(tier = "3")
	public static final Item MOLTEN_IRON_BINDING = makeBinding(600, 4, 0.4f, -0.2f, 0);
	@Tier(tier = "3")
	public static final Item STRONG_GLUE_BINDING = makeBinding(250, 6, 0, 0.2f, 0);
	@Tier(tier = "3")
	public static final Item SCREW_BINDING = makeBinding(400, 5, 0, 0, 0);

	@Tier(tier = "4")
	public static final Item RIVET_BINDING = makeBinding(1200, 8, 0, 0, 0);
	@Tier(tier = "4")
	public static final Item WELDED_BINDING = makeBinding(900, 10, 0.1f, 0, 0);

	@Tier(tier = "5")
	public static final Item NANO_BINDING = makeBinding(1800, 20, 0, 0, 0);
	@Tier(tier = "5")
	public static final Item NEJIRI_ARIGATA_BINDING = makeBinding(1400, 12, 0.1f, 0.1f, 0);

	//EXTRA BINDINGS
	@Tier(tier = "1")
	public static final Item REINFORCED_BINDING_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_BINDING, 300, 4);
	@Tier(tier = "1")
	public static final Item RED_PAINT_BINDING_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_BINDING, 150, 1).setEffect(MapEffectMaker.create(EquipmentEffects.MOVE_SPEED, 0.1f));
	@Tier(tier = "1")
	public static final Item LUCKY_CHARM_BINDING_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_BINDING, 50, 1).setEffect(MapEffectMaker.create(EquipmentEffects.CRITICAL_HIT, 10f));
	@Tier(tier = "2")
	public static final Item HAND_GUARD_BINDING_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_BINDING, 160, 2).setEffect(MapEffectMaker.create(EquipmentEffects.WEAPON_ARMOR_TOUGHNESS, 2f));
	@Tier(tier = "2")
	public static final Item MAGNETIC_BINDING_EXTRA = new EquipmentExtraUpgradeItem("spike/short,spike/medium,blade/short,blade/medium,hammer/short,hammer/medium", Equipments.P_BINDING, 100, 1).setEffect(MapEffectMaker.create().add(EquipmentEffects.THROWABLE, 0f, ThrowableEffectUpgrade.MAGNETISM, 2f));
	@Tier(tier = "3")
	public static final Item BANNER_BINDING_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_BINDING, 80, 1).setEffect(MapEffectMaker.create(EquipmentEffects.COMMANDER_BANNER, 1f));
	@Tier(tier = "4")
	public static final Item SECRET_COMPARTIMENT_BINDING_EXTRA = new EquipmentHidenSlotUpgrade("spike/*,hammer/*,blade/short,blade/medium,blade/long", Equipments.P_BINDING_EXTRA, 80, 1, 2);
	@Tier(tier = "5")
	public static final Item EGIDE_SAPLING_BINDING_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_BINDING, 120, 1).setEffect(MapEffectMaker.create(EquipmentEffects.CONTAMINATION_REDUCTION, 5f));
	//HANDLES
	@Tier(tier = "1")
	public static final Item WOOD_HANDLE = makeHandle(25, 1, 0, 0, 0.1f);
	@Tier(tier = "1")
	public static final Item BONE_HANDLE = makeHandle(40, 1, 0.1f, -0.1f, 0f);
	@Tier(tier = "1")
	public static final Item COPPER_HANDLE = makeHandle(60, 2, -0.1f, -0.1f, -0.1f);

	@Tier(tier = "2")
	public static final Item BRONZE_HANDLE = makeHandle(100, 2, 0.1f, 0, 0.1f);
	@Tier(tier = "2")
	public static final Item SCRAPPED_IRON_HANDLE = makeHandle(30, 1, 0, 0, 0.3f);
	@Tier(tier = "2")
	public static final Item PLASTIC_HANDLE = makeHandle(2, 1, 0, 0.1f, 0.3f);

	@Tier(tier = "3")
	public static final Item IRON_HANDLE = makeHandle(50, 1, 0, 0, 0.5f);
	@Tier(tier = "3")
	public static final Item STRONG_BONE_HANDLE = makeHandle(40, 1, 0.2f, 0, 0.3f);
	@Tier(tier = "3")
	public static final Item LAQUED_WOOD_HANDLE = makeHandle(40, 1, 0, 0, 0.6f);

	@Tier(tier = "4")
	public static final Item ALUMINUM_HANDLE = makeHandle(50, 1, 0, 0.1f, 0.6f);
	@Tier(tier = "4")
	public static final Item STEEL_HANDLE = makeHandle(90, 1, 0, 0, 0.5f);
	@Tier(tier = "4")
	public static final Item EGIDE_BARK_HANDLE = makeHandle(80, 1, 0.2f, 0, 0.4f);

	@Tier(tier = "5")
	public static final Item EGIDE_WOOD_HANDLE = makeHandle(110, 1, 0.2f, 0f, 0.5f);
	@Tier(tier = "5")
	public static final Item PLASTEEL_HANDLE = makeHandle(90, 1, 0, 0.2f, 0.6f);

	//EXTRA HANDLE
	@Tier(tier = "1")
	public static final Item ERGONOMIC_HANDLE_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_HANDLE, 100, 1).setEffect(MapEffectMaker.create(EquipmentEffects.ATTACK_SPEED, 0.2f));
	@Tier(tier = "1")
	public static final Item EXTENDED_HANDLE_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_HANDLE, 80, 1).setEffect(MapEffectMaker.create(EquipmentEffects.ATTACK_SPEED, 0.1f).add(EquipmentEffects.ATTACK_RANGE, 0.2f));
	@Tier(tier = "1")
	public static final Item REINFORCED_HANDLE_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_HANDLE, 300, 4);
	@Tier(tier = "2")
	public static final Item BALANCED_HANDLE_EXTRA = new EquipmentExtraUpgradeItem("spike/short,spike/medium,blade/short,blade/medium,hammer/short,hammer/medium", Equipments.P_HANDLE, 50, 1).setEffect(MapEffectMaker.create(EquipmentEffects.THROWABLE, 1f));
	@Tier(tier = "3")
	public static final Item GUARD_BREAKER_HANDLE_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*", Equipments.P_HANDLE, 110, 1).setEffect(MapEffectMaker.create(EquipmentEffects.OPENING, 1f));
	@Tier(tier = "4")
	public static final Item TAILORED_HANDLE_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_HANDLE, 200, 1).setEffect(MapEffectMaker.create(EquipmentEffects.ATTACK_SPEED, 0.1f).add(EquipmentEffects.ATTACK_DAMAGE, 0.1f).add(EquipmentEffects.ATTACK_RANGE, 0.1f));
	@Tier(tier = "4")
	public static final Item NANOMACHINE_HANDLE_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_HANDLE, 500, 2).setEffect(MapEffectMaker.create(EquipmentEffects.SHIELD_BOOST, 5f));
	@Tier(tier = "5")
	public static final Item SHOCK_ABSORBER_HANDLE_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_HANDLE, 200, 1).setEffect(MapEffectMaker.create(EquipmentEffects.KNOCKBACK_RESISTANCE, 0.4f).add(EquipmentEffects.FALL_RESISTANCE, 10f));

	//HEAD
	@Tier(tier = "1")
	public static final Item WOOD_HEAD = makeHead(500, 4, -50, 0, 0);
	@Tier(tier = "1")
	public static final Item COPPER_HEAD = makeHead(40, 1, 0.2f, -0.2f, -0.1f);
	@Tier(tier = "1")
	public static final Item STONE_HEAD = makeHead(25, 1, 0, 0, 0);

	@Tier(tier = "2")
	public static final Item BONE_HEAD = makeHead(25, 1, 0.6f, 0, 0);
	@Tier(tier = "2")
	public static final Item BRONZE_HEAD = makeHead(50, 1, 0.6f, -0.1f, -0.1f);
	@Tier(tier = "2")
	public static final Item SCRAPPED_IRON_HEAD = makeHead(30, 1, 0.5f, 0, 0.1f);

	@Tier(tier = "3")
	public static final Item IRON_HEAD = makeHead(50, 1, 0.7f, 0, 0.2f);
	@Tier(tier = "3")
	public static final Item STRONG_BONE_HEAD = makeHead(40, 1, 0.8f, 0, 0);
	@Tier(tier = "3")
	public static final Item LAQUED_WOOD_HEAD = makeHead(1000, 4, -50, 0.1f, 0.1f);

	@Tier(tier = "4")
	public static final Item ALUMINUM_HEAD = makeHead(50, 1, 0.8f, 0.2f, 0.1f);
	@Tier(tier = "4")
	public static final Item STEEL_HEAD = makeHead(90, 1, 1, 0, 0.2f);
	@Tier(tier = "4")
	public static final Item EGIDE_BARK_HEAD = makeHead(80, 1, 1.2f, 0.1f, 0);

	@Tier(tier = "5")
	public static final Item EGIDE_WOOD_HEAD = makeHead(110, 1, 1.6f, 0.1f, 0);
	@Tier(tier = "5")
	public static final Item PLASTEEL_HEAD = makeHead(90, 1, 1.2f, 0.4f, 0.1f);
	//EXTRA HEAD
	@Tier(tier = "1")
	public static final Item SHARPENED_HEAD_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_HEAD, 25, 2).setEffect(MapEffectMaker.create(EquipmentEffects.BLEEDING, 30f));
	@Tier(tier = "1")
	public static final Item WEIGHTED_HEAD_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_HEAD, 150, 1).setEffect(MapEffectMaker.create(EquipmentEffects.ATTACK_DAMAGE, 0.5f).add(EquipmentEffects.ATTACK_SPEED, -0.2f));
	@Tier(tier = "1")
	public static final Item LIGHT_HEAD_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_HEAD, 100, 1).setEffect(MapEffectMaker.create(EquipmentEffects.ATTACK_DAMAGE, -0.2f).add(EquipmentEffects.ATTACK_SPEED, 0.4f));
	@Tier(tier = "2")
	public static final Item COMPACT_HEAD_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_HEAD, 200, 1).setEffect(MapEffectMaker.create(EquipmentEffects.ATTACK_RANGE, -0.2f).add(EquipmentEffects.ATTACK_SPEED, 0.4f));
	@Tier(tier = "2")
	public static final Item TELESCOPIC_HEAD_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_HEAD, 200, 1).setEffect(MapEffectMaker.create(EquipmentEffects.ATTACK_RANGE, 0.2f));
	@Tier(tier = "2")
	public static final Item TOOL_HAMMER_HEAD_EXTRA = new EquipmentExtraUpgradeItem("hammer/*", Equipments.P_HEAD, 500, 4).setEffect(MapEffectMaker.create(EquipmentEffects.TOOL_HAMMER, 1f).add(EquipmentEffects.ATTACK_DAMAGE, -1f));
	@Tier(tier = "2")
	public static final Item TOOL_SCREWDRIVER_HEAD_EXTRA = new EquipmentExtraUpgradeItem("spike/*,", Equipments.P_HEAD, 500, 4).setEffect(MapEffectMaker.create(EquipmentEffects.TOOL_WRENCH, 1f).add(EquipmentEffects.ATTACK_DAMAGE, -1f));
	@Tier(tier = "3")
	public static final Item WHISTLE_HEAD_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_HEAD, 120, 1).setEffect(MapEffectMaker.create(EquipmentEffects.THREAT_MODIFIER, 5f));
	@Tier(tier = "3")
	public static final Item CURVED_HEAD_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_HEAD, 150, 1).setEffect(MapEffectMaker.create().add(EquipmentEffects.SWEEPING, 1f, SweepingEffectUpgrade.DAMAGE_BONUS, 10f, SweepingEffectUpgrade.RADIUS_BONUS, 1));
	@Tier(tier = "3")
	public static final Item POISONED_HEAD_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_HEAD, 40, 3).setEffect(MapEffectMaker.create(EquipmentEffects.POISON, 5));
	@Tier(tier = "4")
	public static final Item MAGNETIC_PUNCH_HEAD_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_HEAD, 100, 1).setEffect(MapEffectMaker.create(EquipmentEffects.KNOCKBACK_RESISTANCE, 1f));
	@Tier(tier = "4")
	public static final Item PYROPHOSPHORIC_HEAD_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_HEAD, 50, 2).setEffect(MapEffectMaker.create(EquipmentEffects.FLAME, 5));
	@Tier(tier = "5")
	public static final Item INFECTED_HEAD_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_HEAD, 130, 1).setEffect(MapEffectMaker.create(EquipmentEffects.WITHER, 5));
	@Tier(tier = "5")
	public static final Item MONOFILAMENT_HEAD_EXTRA = new EquipmentExtraUpgradeItem("spike/*,blade/*,hammer/*", Equipments.P_HEAD, 100, 1).setEffect(MapEffectMaker.create().add(EquipmentEffects.CRITICAL_HIT, 5f, CriticalHitEffectUpgrade.DAMAGE_BONUS, 10f));


	@Tier(tier = "1")
	public static final Item TIER1_BATTERY = new EquipmentBatteryUpgrade("blade/induction,electric/*", BatteryTiers.BATTERY, BatteryTiers.BATTERY, 2);
	@Tier(tier = "2")
	public static final Item TIER2_BATTERY = new EquipmentBatteryUpgrade("blade/induction,electric/*", BatteryTiers.BATTERY, BatteryTiers.BATTERY, 4);
	@Tier(tier = "3")
	public static final Item TIER3_BATTERY = new EquipmentBatteryUpgrade("blade/induction,electric/*", BatteryTiers.BATTERY, BatteryTiers.LIPO, 2);
	@Tier(tier = "4")
	public static final Item TIER4_BATTERY = new EquipmentBatteryUpgrade("blade/induction,electric/*", BatteryTiers.LIPO, BatteryTiers.LIPO, 4);
	@Tier(tier = "5")
	public static final Item TIER5_BATTERY = new EquipmentBatteryUpgrade("blade/induction,electric/*", BatteryTiers.LIPO, BatteryTiers.HIGH_TECH, 2);

	//public static final Item SWEEP_MODIFIER = new EquipmentUpgradeItem("hammer/*", Equipments.P_BINDING, 100, 1).setEffect(MapEffectMaker.create().add(EquipmentEffects.SWEEPING, 1f, SweepingEffectUpgrade.DAMAGE_BONUS, 20));
	//public static final Item HAMMER_MODIFIER = new EquipmentUpgradeItem("*/short,*/medium", Equipments.P_HEAD, 100, 1).setEffect(MapEffectMaker.create(EquipmentEffects.TOOL_HAMMER, 1));


	public static final Item SOFT_PADDING = new EquipmentUpgradeItem("armor/*", Equipments.P_PADDING, 200, 1).setEffect(MapEffectMaker.create().add(EquipmentEffects.ARMOR, 0f, ArmorEffectUpgrade.ARMOR_TOUGHNESS, 2f));
	public static final Item BASIC_PADDING = new EquipmentUpgradeItem("armor/*", Equipments.P_PADDING, 400, 2);
	public static final Item LEATHER_PLATING = new EquipmentUpgradeItem("armor/*", Equipments.P_PLATING, 200, 1).setEffect(MapEffectMaker.create().add(EquipmentEffects.ARMOR, 1f));
	public static final Item IRON_PLATING = new EquipmentUpgradeItem("armor/*", Equipments.P_PLATING, 500, 1).setEffect(MapEffectMaker.create().add(EquipmentEffects.ARMOR, 2f));
	public static final Item DIAMOND_PLATING = new EquipmentUpgradeItem("armor/*", Equipments.P_PLATING, 800, 1).setEffect(MapEffectMaker.create().add(EquipmentEffects.ARMOR, 3f));
	public static final Item BASIC_FRAME = new EquipmentUpgradeItem("armor/*", Equipments.P_FRAME, 300, 2);
	public static final Item TOUGH_FRAME = new EquipmentUpgradeItem("armor/*", Equipments.P_FRAME, 1800, 4);
	@Tier(tier = "1")
	public static final Item CHAINSAW_CHAIN = new EquipmentUpgradeItem("electric/cutter", Equipments.P_CHAIN, 1800, 4).setEffect(MapEffectMaker.create(EquipmentEffects.ATTACK_DAMAGE, 4));
	@Tier(tier = "1")
	public static final Item CHAINSAW_CHASSIS = new EquipmentUpgradeItem("electric/cutter", Equipments.P_CHASSIS, 1800, 4).setEffect(MapEffectMaker.create(EquipmentEffects.ELECTRIC_EFFICIENCY, 50));
	@Tier(tier = "1")
	public static final Item CHAINSAW_LONG_CHASSIS = new EquipmentUpgradeItem("electric/cutter", Equipments.P_CHASSIS, 1800, 4).setEffect(MapEffectMaker.create(EquipmentEffects.ATTACK_RANGE, 1));
	@Tier(tier = "1")
	public static final Item CHAINSAW_MOTOR = new EquipmentUpgradeItem("electric/cutter", Equipments.P_MOTOR, 1800, 4).setEffect(MapEffectMaker.create(EquipmentEffects.ELECTRIC_WARMUP_SPEED, 50));

	@Override
	public void postProcessField(String namespace, Item value, String identifier, Field field) {
		if(GreenResurgence.clientHelper.isDatagen()) {
			LangGenerator.auto_name.put(new Identifier(namespace, "equipments/" + identifier), identifier);
			if(field.isAnnotationPresent(Tier.class)) {
				var name = "head_t";
				if(identifier.endsWith("extra"))
					name = "extra_t";
				else if(identifier.endsWith("handle"))
					name = "handle_t";
				else if(identifier.endsWith("binding"))
					name = "binding_t";
				else if(identifier.endsWith("battery"))
					name = "battery_t";
				else if(identifier.endsWith("motor"))
					name = "motor_t";
				else if(identifier.endsWith("chassis"))
					name = "chassis_t";
				else if(identifier.endsWith("chain"))
					name = "chain_t";
				ModelGenerator.createTextureItemModel(value, GreenResurgence.asRessource("item/equipments/parts/" + name + field.getAnnotation(Tier.class).tier()));
			}
		}
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

	private static Map<Identifier, Pair<String, Integer>> autogen = new HashMap<>();

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Tier {
		public String tier() default "1";
	}
}
