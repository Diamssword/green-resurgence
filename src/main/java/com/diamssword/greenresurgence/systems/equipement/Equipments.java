package com.diamssword.greenresurgence.systems.equipement;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.items.equipment.EquipmentSecondHandNew;
import com.diamssword.greenresurgence.items.equipment.EquipmentToolElectric;
import com.diamssword.greenresurgence.items.equipment.EquipmentToolElectricTwoHanded;
import com.diamssword.greenresurgence.items.equipment.EquipmentTwoHanded;
import com.diamssword.greenresurgence.systems.equipement.utils.EquipmentEnergyStorage;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import team.reborn.energy.api.EnergyStorage;

import java.util.*;
import java.util.function.BiFunction;

public class Equipments {
	public static Map<String, Map<String, IEquipmentDef>> equipments = new HashMap<>();
	public static final String TYPE_BLADE = "blade";
	public static final String TYPE_HAMMER = "hammer";
	public static final String TYPE_SPIKE = "spike";
	public static final String TYPE_ELECTRIC = "electric";
	public static final String P_SKIN = "skin";
	public static final String P_HEAD = "head";
	public static final String P_BINDING = "binding";

	public static final String P_BINDING_EXTRA = "extra_binding";
	public static final String P_HANDLE_EXTRA = "extra_handle";
	public static final String P_HEAD_EXTRA = "extra_head";
	public static final String P_BINDING_EXTRA_2 = "extra2_binding";
	public static final String P_HANDLE_EXTRA_2 = "extra2_handle";
	public static final String P_HEAD_EXTRA_2 = "extra2_head";

	public static final String P_SPIKE = "spike";
	public static final String P_BATTERY = "battery";
	public static final String P_HANDLE = "handle";
	public static final String P_EXTRA = "extra";
	public static final String[] REQUIRED_SLOTS = new String[]{P_SKIN, P_HEAD, P_BINDING, P_HANDLE};

	public static void init() {
		//TODO set durability modifer
		register(TYPE_BLADE, "short", EquipmentValues.SMALL_BLADE_BASE, REQUIRED_SLOTS, ExtraSlots());
		register(TYPE_BLADE, "medium", EquipmentValues.MEDIUM_BLADE_BASE, REQUIRED_SLOTS, ExtraSlots());
		register(TYPE_BLADE, "long", EquipmentValues.LONG_BLADE_BASE, REQUIRED_SLOTS, ExtraSlots(P_HEAD_EXTRA_2));

		register(TYPE_HAMMER, "short", EquipmentValues.SMALL_HAMMER_BASE, REQUIRED_SLOTS, ExtraSlots());
		register(TYPE_HAMMER, "medium", EquipmentValues.MEDIUM_HAMMER_BASE, REQUIRED_SLOTS, ExtraSlots());
		register(TYPE_HAMMER, "long", EquipmentValues.LONG_HAMMER_BASE, REQUIRED_SLOTS, ExtraSlots(P_BINDING_EXTRA_2));

		register(TYPE_SPIKE, "short", EquipmentValues.SMALL_SPIKE_BASE, REQUIRED_SLOTS, ExtraSlots());
		register(TYPE_SPIKE, "medium", EquipmentValues.MEDIUM_SPIKE_BASE, REQUIRED_SLOTS, ExtraSlots());
		register(TYPE_SPIKE, "long", EquipmentValues.LONG_SPIKE_BASE, REQUIRED_SLOTS, ExtraSlots(P_HANDLE_EXTRA_2));

		register(TYPE_ELECTRIC, "cutter", new HashMap<>(), new String[]{P_SKIN, P_BATTERY}, P_EXTRA);
		register(TYPE_ELECTRIC, "hot", new HashMap<>(), new String[]{P_SKIN, P_BATTERY}, P_EXTRA);

		registers();
		EquipmentSkins.init();
		EquipmentEffects.init();

	}

	private static String[] ExtraSlots(String... addedSlots) {
		var ls = new ArrayList<String>();
		ls.add(P_HEAD_EXTRA);
		ls.add(P_BINDING_EXTRA);
		ls.add(P_HANDLE_EXTRA);
		ls.addAll(Arrays.asList(addedSlots));
		return ls.toArray(new String[0]);
	}

	public static Optional<IEquipmentDef> getEquipment(String type, String subtype) {
		var t = equipments.get(type);
		if(t != null)
			return Optional.ofNullable(t.get(subtype));
		return Optional.empty();
	}

	private static void registers() {
		equipments.forEach((k, v) -> {
			v.forEach((k1, v1) -> {
				if(v1.getBlueprintItem() != null)
					Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, "equipments/bp_" + k + "_" + k1), v1.getBlueprintItem());
				if(v1.getEquipmentItem() != null)
					Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, "equipments/" + k + "_" + k1), v1.getEquipmentItem());
				if(v1.getEquipmentItem() instanceof EquipmentToolElectric) {
					EnergyStorage.ITEM.registerForItems((a, c) -> EquipmentEnergyStorage.createEquipmentStorage(c, a), v1.getEquipmentItem());
				}
			});
		});

	}

	public static void register(String type, String subType, Map<String, EffectLevel> bases, String[] requiredSlots, String... extraSlots) {
		bases.put(EquipmentEffects.BASE_DAMAGE_MOD, new EffectLevel(1f));
		equipments.putIfAbsent(type, new HashMap<>());
		BiFunction<String, String, Item> gen = (e, f) -> new EquipmentSecondHandNew(e, f, bases);

		if(subType.equals("long")) {
			gen = (e, f) -> new EquipmentTwoHanded(e, f, bases);
		} else if(type.equals("electric")) {
			if(subType.equals("cutter"))
				gen = (a, b) -> new EquipmentToolElectricTwoHanded(a, b, false);
			else
				gen = (a, b) -> new EquipmentToolElectric(a, b, true);
		}
		var equ = new EquipmentDef(type, subType, gen, requiredSlots, extraSlots);
		if(type.equals(TYPE_BLADE))
			equ.setDamageChance(P_HEAD, 2f).setDamageChance(P_BINDING, 2f);
		else if(type.equals(TYPE_HAMMER))
			equ.setDamageChance(P_BINDING, 2f);
		else if(type.equals(TYPE_SPIKE))
			equ.setDamageChance(P_HANDLE, 2f).setDamageChance(P_BINDING, 2f);
		equipments.get(type).put(subType, equ);
	}
}
