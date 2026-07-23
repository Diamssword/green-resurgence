package com.diamssword.greenresurgence.datagen;

import com.diamssword.greenresurgence.genericBlocks.GenericBlockSet;
import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import com.diamssword.greenresurgence.items.materials.MaterialSet;
import com.diamssword.greenresurgence.systems.equipement.Equipments;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModelGenerator extends FabricModelProvider {
	public static Map<Item, Identifier> textureItemModels = new HashMap<>();
	public static Map<Identifier, Item> blockItems = new HashMap<>();

	public static void createTextureItemModel(Item item, Identifier texture) {
		textureItemModels.put(item, texture);
	}

	public ModelGenerator(FabricDataOutput generator) {
		super(generator);
	}

	@Override
	public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

		for(GenericBlockSet set : GenericBlocks.sets) {
			set.modelGenerator(blockStateModelGenerator);
		}

	}

	@Override
	public void generateItemModels(ItemModelGenerator itemModelGenerator) {
		for(GenericBlockSet set : GenericBlocks.sets) {
			set.modelGenerator(itemModelGenerator);
		}
		blockItems.forEach((k, v) -> {
			itemModelGenerator.register(v, new Model(Optional.of(new Identifier(k.getNamespace(), "block/" + k.getPath())), Optional.empty()));
		});
		textureItemModels.forEach((i, v) -> {
			new Model(Optional.of(new Identifier("item/generated")), Optional.empty(), TextureKey.LAYER0)
					.upload(ModelIds.getItemModelId(i), TextureMap.layer0(v), itemModelGenerator.writer);
		});
		Equipments.equipments.forEach((k, v) -> {
			v.forEach((k1, v1) -> {
				var it = v1.getEquipmentItem();
				itemModelGenerator.register(it, new DevModelCopy("gecko_delegated", new HashMap<>()));

			});
		});
		MaterialSet.registerModels(itemModelGenerator);
	}
}
