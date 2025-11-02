package com.diamssword.greenresurgence.systems.equipement;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.items.equipment.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import team.reborn.energy.api.EnergyStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class Equipments {
	public static Map<String, Map<String, IEquipmentDef>> equipments = new HashMap<>();
	public static final String P_SKIN = "skin";
	public static final String P_BLADE = "blade";
	public static final String P_HEAD = "head";
	public static final String P_BINDING = "binding";

	public static final String P_BINDING_EXTRA = "extra_binding";
	public static final String P_HANDLE_EXTRA = "extra_handle";
	public static final String P_HEAD_EXTRA = "extra_head";
	public static final String P_BINDING_EXTRA_2 = "extra_binding_2";
	public static final String P_HANDLE_EXTRA_2 = "extra_handle_2";
	public static final String P_HEAD_EXTRA_2 = "extra_head_2";
	public static final String P_SPIKE = "spike";
	public static final String P_BATTERY = "battery";
	public static final String P_HANDLE = "handle";
	public static final String P_EXTRA = "extra";

	public static void init() {
		register("blade", "short", new String[]{P_SKIN, P_HEAD, P_BINDING, P_HANDLE}, P_HEAD_EXTRA, P_BINDING_EXTRA, P_HANDLE_EXTRA);
		register("blade", "medium", new String[]{P_SKIN, P_HEAD, P_BINDING, P_HANDLE}, P_HEAD_EXTRA, P_BINDING_EXTRA, P_HANDLE_EXTRA);
		register("blade", "long", new String[]{P_SKIN, P_HEAD, P_BINDING, P_HANDLE}, P_HEAD_EXTRA, P_BINDING_EXTRA, P_HANDLE_EXTRA, P_HEAD_EXTRA_2, P_BINDING_EXTRA_2, P_HANDLE_EXTRA_2);

		register("hammer", "short", new String[]{P_SKIN, P_HEAD, P_HANDLE}, P_EXTRA);
		register("hammer", "medium", new String[]{P_SKIN, P_HEAD, P_HANDLE}, P_EXTRA);
		register("hammer", "long", new String[]{P_SKIN, P_HEAD, P_HANDLE}, P_EXTRA);

		register("spike", "short", new String[]{P_SKIN, P_SPIKE, P_HANDLE}, P_EXTRA);
		register("spike", "medium", new String[]{P_SKIN, P_SPIKE, P_HANDLE}, P_EXTRA);
		register("spike", "long", new String[]{P_SKIN, P_SPIKE, P_HANDLE}, P_EXTRA);

		register("electric", "cutter", new String[]{P_SKIN, P_BATTERY}, P_EXTRA);
		register("electric", "hot", new String[]{P_SKIN, P_BATTERY}, P_EXTRA);

		registers();
		EquipmentSkins.init();
		EquipmentEffects.init();

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

	public static void register(String type, String subType, String[] requiredSlots, String... extraSlots) {
		equipments.putIfAbsent(type, new HashMap<>());
		BiFunction<String, String, Item> gen = EquipmentSecondHand::new;
		if(type.equals("hammer") && subType.equals("medium"))
			gen = EquipmentToolHammer::new;
		else if(subType.equals("long")) {
			gen = EquipmentTwoHanded::new;
		} else if(subType.equals("short")) {
			gen = EquipmentSecondHand::new;
		} else if(type.equals("electric")) {
			if(subType.equals("cutter"))
				gen = (a, b) -> new EquipmentToolElectricTwoHanded(a, b, false);
			else
				gen = (a, b) -> new EquipmentToolElectric(a, b, true);
		}
		equipments.get(type).put(subType, new EquipmentDef(type, subType, gen, requiredSlots, extraSlots));
	}
}
