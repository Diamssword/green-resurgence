package com.diamssword.greenresurgence.systems.equipement;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.items.weapons.ICustomPoseWeapon;
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
		forAll("bone_hammer", "hammer/medium,hammer/long");
		forAll("bone_axe", "hammer/medium,hammer/long");
		forAll("bone_mace", "hammer/medium,hammer/long");
		forAll("bone_knuckles", "hammer/short");
		forAll("bat", "hammer/medium,hammer/long");
		forAll("work_tool", "hammer/*,spike/long");
		forAll("fire_axe", "hammer/long");
		forAll("iron_pipe", "hammer/short,hammer/medium");
		forAll("upgraded_bat", "hammer/medium,hammer/long");
		forAll("street_sign", "hammer/long");
		forAll("makeshift_axe", "hammer/medium,hammer/long");
		forAll("makeshift_mace", "hammer/medium,hammer/long");
		forAll("makeshift_hammer", "hammer/medium,hammer/long");
		forAll("makeshift_knuckles", "hammer/short");
		forAll("wrench", "hammer/medium,hammer/short");
		forAll("bone_blade", "blade/*");
		forAll("bone_matchet", "blade/short,blade/medium");
		forAll("bone_katana", "blade/long");
		forAll("combat_knife", "blade/short");
		forAll("kukri", "blade/medium");
		forAll("makeshift_blade", "blade/*");
		forAll("makeshift_matchet", "blade/short,blade/medium");
		forAll("makeshift_katana", "blade/long");
		forAll("bone_spear", "spike/medium,spike/long");
		forAll("makeshift_spear", "spike/medium,spike/long");
		forAll("screwdriver_flat", "spike/short");
		forAll("screwdriver_cross", "spike/short");
		forAll("chainsaw", "electric/cutter", true);
		forAll("weedwacker", "electric/cutter", true);
		forAll("flame_sword", "electric/hot", true);

	}

	public static Optional<ItemSkinModelDef> get(String skin, Item item) {
		var s = skins.get(skin);
		if(s != null)
			return Optional.ofNullable(s.get(item));
		return Optional.empty();
	}

	public static Optional<String> getDefault(Item item) {
		for(String s : skins.keySet()) {
			if(skins.get(s).containsKey(item))
				return Optional.of(s);
		}
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
		forAll(id, allowed, false, null, 0);
	}

	private static void forAll(String id, String allowed, boolean isGecko) {
		forAll(id, allowed, isGecko, null, 0);
	}

	private static void forAll(String id, String allowed, boolean isGecko, @Nullable Identifier texture, int modeForTwoHanded) {
		for(String s : allowed.split(",")) {
			var ps = s.split("/");
			if(ps[0].equals("*")) {
				Equipments.equipments.forEach((k, v) -> {
					if(ps[1].equals("*")) {
						v.forEach((k1, v1) -> {
							createOne(id, v1, isGecko, texture, modeForTwoHanded);
						});
					} else if(v.containsKey(ps[1]))
						createOne(id, v.get(ps[1]), isGecko, texture, modeForTwoHanded);
				});
			} else {
				var eq = Equipments.equipments.get(ps[0]);
				if(eq != null) {
					if(ps[1].equals("*")) {
						eq.forEach((k1, v1) -> {
							createOne(id, v1, isGecko, texture, modeForTwoHanded);
						});
					} else if(eq.containsKey(ps[1]))
						createOne(id, eq.get(ps[1]), isGecko, texture, modeForTwoHanded);
				}
			}
		}
	}

	private static void createOne(String skin, IEquipmentDef equipment, boolean isGecko, @Nullable Identifier texture, int modeForTwoHanded) {
		skins.putIfAbsent(skin, new HashMap<>());
		var item = equipment.getEquipmentItem();
		if(item instanceof ICustomPoseWeapon wep && wep.shouldRemoveOffHand()) {
			skins.get(skin).put(equipment.getEquipmentItem(), new ItemSkinModelDef(isGecko, skin, equipment, texture, modeForTwoHanded));
		} else
			skins.get(skin).put(equipment.getEquipmentItem(), new ItemSkinModelDef(isGecko, skin, equipment, texture));
	}

	public static class ItemSkinModelDef {
		public final boolean isGecko;
		public final Identifier model;
		public final int extra;
		@Nullable
		public final Identifier texture;

		public ItemSkinModelDef(boolean isGecko, String skin, IEquipmentDef equipment, @Nullable Identifier texture) {
			this(isGecko, skin, equipment, texture, 0);
		}

		public ItemSkinModelDef(boolean isGecko, String skin, IEquipmentDef equipment, @Nullable Identifier texture, int extra) {
			this.isGecko = isGecko;
			if(!isGecko)
				this.model = GreenResurgence.asRessource("equipments/skins/" + skin + "/" + equipment.getEquipmentType() + "_" + equipment.getEquipmentSubtype());
			else
				this.model = GreenResurgence.asRessource(skin + "/" + equipment.getEquipmentType() + "_" + equipment.getEquipmentSubtype());
			this.texture = texture;
			this.extra = extra;
		}

		public Identifier getVanillaPath() {
			if(!isGecko)
				return model;
			else
				return model.withPrefixedPath("equipments/skins/");
		}

		public ItemSkinModelDef(boolean isGecko, Identifier model, Identifier texture) {
			this.isGecko = isGecko;
			this.model = model;
			this.texture = texture;
			this.extra = 0;
		}
	}
}
