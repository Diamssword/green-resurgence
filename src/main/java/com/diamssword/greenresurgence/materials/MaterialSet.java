package com.diamssword.greenresurgence.materials;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.items.MaterialItem;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.*;

public class MaterialSet {
	public static final Map<String, MaterialSet> sets = new HashMap<>();

	public static MaterialSet createSet(String material) {
		var m = new MaterialSet(material);
		sets.put(material, m);
		return m;
	}

	public final String material;
	private final Map<Integer, String> tiersLabel = new HashMap<>();
	private final Map<Identifier, MaterialItem> items = new HashMap<>();
	private final Map<Identifier, Pair<String, String>> translates = new HashMap<>();
	private final List<Item> is3D = new ArrayList<>();

	private MaterialSet(String material) {
		this.material = material;
	}

	public static void registerLangs(FabricLanguageProvider.TranslationBuilder builder) {
		sets.forEach((m, v) -> {
			v.translates.forEach((i, p) -> {
				builder.add(v.items.get(i), p.getLeft());
				builder.add("desc." + i.getNamespace() + "." + i.getPath(), p.getRight());
			});
			v.getTiers().forEach(v1 -> {
				String s = v.tiersLabel.get(v1);
				builder.add("desc." + GreenResurgence.ID + ".materials.tier." + v1 + "." + v.material, s != null ? s : "");
			});

		});
	}

	public Item get(String name) {
		return items.get(GreenResurgence.asRessource("material_" + material + "_" + name));
	}

	public static void registerModels(ItemModelGenerator builder) {
		sets.forEach((m, v) -> {
			v.items.forEach((i, it) -> {
				if (!v.is3D.contains(it))
					new Model(Optional.of(new Identifier("item/generated")), Optional.empty(), TextureKey.LAYER0).upload(ModelIds.getItemModelId(it), TextureMap.layer0(new Identifier(GreenResurgence.ID, "item/materials/" + i.getPath().replace("material_", ""))), builder.writer);
			});
		});
	}

	public List<Integer> getTiers() {
		List<Integer> ls = new ArrayList<Integer>();
		this.items.forEach((n, i) -> {
			if (!ls.contains(i.tier))
				ls.add(i.tier);
		});
		return ls;
	}

	public List<MaterialItem> getItems() {
		return new ArrayList<>(items.values());
	}

	public MaterialSet setTierLabel(int tier, String label) {
		tiersLabel.put(tier, label);
		return this;
	}

	public MaterialSet add(int tier, String id, String name, String desc) {
		return add(tier, id, name, desc, false);
	}

	public MaterialSet add(int tier, String id, String name, String desc, boolean is3D) {
		return add(tier, id, name, desc, is3D, MaterialItem::new);
	}

	public MaterialSet add(int tier, String id, String name, String desc, boolean is3D, MaterialItemFactory itemFactory) {
		Identifier idd = GreenResurgence.asRessource("material_" + material + "_" + id);
		var it = itemFactory.create(new OwoItemSettings().group(MItems.GROUP).tab(3), tier, id, material);
		translates.put(idd, new Pair<>(name, desc));
		items.put(idd, it);
		if (is3D)
			this.is3D.add(it);
		Registry.register(Registries.ITEM, idd, it);
		return this;
	}

	@FunctionalInterface
	public interface MaterialItemFactory {

		MaterialItem create(OwoItemSettings settings, int tier, String id, String material);
	}

}
