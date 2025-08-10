package com.diamssword.greenresurgence.datagen;

import com.diamssword.greenresurgence.genericBlocks.GenericBlockSet;
import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import com.diamssword.greenresurgence.materials.MaterialSet;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModelGenerator extends FabricModelProvider {
	public static Map<Identifier, Item> blockItems = new HashMap<>();

	public ModelGenerator(FabricDataOutput generator) {
		super(generator);
	}

	@Override
	public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

		for (GenericBlockSet set : GenericBlocks.sets) {
			set.modelGenerator(blockStateModelGenerator);
		}

	}

	@Override
	public void generateItemModels(ItemModelGenerator itemModelGenerator) {
		for (GenericBlockSet set : GenericBlocks.sets) {
			set.modelGenerator(itemModelGenerator);
		}
		blockItems.forEach((k, v) -> {
			itemModelGenerator.register(v, new Model(Optional.of(new Identifier(k.getNamespace(), "block/" + k.getPath())), Optional.empty()));
		});

		MaterialSet.registerModels(itemModelGenerator);
	}
}
