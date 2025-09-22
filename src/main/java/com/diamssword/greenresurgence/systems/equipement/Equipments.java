package com.diamssword.greenresurgence.systems.equipement;

import com.diamssword.greenresurgence.GreenResurgence;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Equipments {
	public static Map<String, Map<String, IEquipmentDef>> equipments = new HashMap<>();
	public static final String P_SKIN = "skin";
	public static final String P_BLADE = "blade";
	public static final String P_WEIGHT = "weight";
	public static final String P_HANDLE = "handle";
	public static final String P_EXTRA = "extra";

	public static void init() {
		register("blade", "short", P_SKIN, P_BLADE, P_HANDLE, P_EXTRA);
		register("blade", "long", P_SKIN, P_BLADE, P_HANDLE, P_EXTRA);
		register("hammer", "short", P_SKIN, P_WEIGHT, P_HANDLE, P_EXTRA);
		register("hammer", "long", P_SKIN, P_WEIGHT, P_HANDLE, P_EXTRA);

		registers();
		EquipmentSkins.init();
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
			});

		});

	}

	public static void register(String type, String subType, String... slots) {
		equipments.putIfAbsent(type, new HashMap<>());
		equipments.get(type).put(subType, new EquipmentDef(type, subType, slots));
	}
}
