package com.diamssword.greenresurgence.datagen;

import com.diamssword.greenresurgence.genericBlocks.GenericBlockSet;
import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import com.diamssword.greenresurgence.materials.MaterialSet;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;

public class ModelGenerator extends FabricModelProvider {
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
		MaterialSet.registerModels(itemModelGenerator);
	}
}
