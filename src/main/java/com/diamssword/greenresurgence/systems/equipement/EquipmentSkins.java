package com.diamssword.greenresurgence.systems.equipement;

import com.diamssword.greenresurgence.GreenResurgence;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EquipmentSkins {

	public static Map<String, Map<Item, ItemSkinModelDef>> skins = new HashMap<>();

	public static void init() {
		forAll("bone_hammer", "hammer/*");
		forAll("bone_axe", "hammer/*");
		forAll("bone_blade", "blade/*");
		forAll("bone_knife", "blade/*");
	}

	public static Optional<ItemSkinModelDef> get(String skin, Item item) {
		var s = skins.get(skin);
		if(s != null)
			return Optional.ofNullable(s.get(item));
		return Optional.empty();
	}

	public static Optional<ItemSkinModelDef> get(String skin, long worldtime) {
		var s = skins.get(skin);
		if(s != null) {
			var arr = s.keySet().stream().toList();
			return arr.isEmpty() ? Optional.empty() : Optional.of(s.get(arr.get(MathHelper.floor(worldtime / 40f) % arr.size())));
		}
		return Optional.empty();
	}

	private static void forAll(String id, String allowed) {
		forAll(id, allowed, false, null);
	}

	private static void forAll(String id, String allowed, boolean isGecko) {
		forAll(id, allowed, isGecko, null);
	}

	private static void forAll(String id, String allowed, boolean isGecko, @Nullable Identifier texture) {
		for(String s : allowed.split(",")) {
			var ps = s.split("/");
			if(ps[0].equals("*")) {
				Equipments.equipments.forEach((k, v) -> {
					if(ps[1].equals("*")) {
						v.forEach((k1, v1) -> {
							createOne(id, v1, isGecko, texture);
						});
					} else if(v.containsKey(ps[1]))
						createOne(id, v.get(ps[1]), isGecko, texture);
				});
			} else {
				var eq = Equipments.equipments.get(ps[0]);
				if(eq != null) {
					if(ps[1].equals("*")) {
						eq.forEach((k1, v1) -> {
							createOne(id, v1, isGecko, texture);
						});
					} else if(eq.containsKey(ps[1]))
						createOne(id, eq.get(ps[1]), isGecko, texture);
				}
			}
		}
	}

	private static void createOne(String skin, IEquipmentDef equipment, boolean isGecko, @Nullable Identifier texture) {
		skins.putIfAbsent(skin, new HashMap<>());
		skins.get(skin).put(equipment.getEquipmentItem(), new ItemSkinModelDef(isGecko, skin, equipment, texture));
	}

	public static class ItemSkinModelDef {
		public final boolean isGecko;
		public final Identifier model;
		@Nullable
		public final Identifier texture;

		public ItemSkinModelDef(boolean isGecko, String skin, IEquipmentDef equipment, @Nullable Identifier texture) {
			this.isGecko = isGecko;
			if(!isGecko)
				this.model = GreenResurgence.asRessource("equipments/skins/" + skin + "/" + equipment.getEquipmentType() + "_" + equipment.getEquipmentSubtype());
			else
				this.model = GreenResurgence.asRessource(skin + "/" + equipment.getEquipmentType() + "_" + equipment.getEquipmentSubtype());
			this.texture = texture;
		}

		public ItemSkinModelDef(boolean isGecko, Identifier model, Identifier texture) {
			this.isGecko = isGecko;
			this.model = model;
			this.texture = texture;
		}
	}
}
