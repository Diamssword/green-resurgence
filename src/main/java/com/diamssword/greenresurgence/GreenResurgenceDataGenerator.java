package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class GreenResurgenceDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ItemTagGenerator::new);
		pack.addProvider(BlockTagGenerator::new);
		pack.addProvider(LootTableGenerator::new);
		pack.addProvider(ModelGenerator::new);
		pack.addProvider((FabricDataGenerator.Pack.Factory<LangGenerator>) b -> new LangGenerator(b, "en_us"));
		pack.addProvider((FabricDataGenerator.Pack.Factory<LangGenerator>) b -> new LangGenerator(b, "fr_fr"));
		pack.addProvider(BlockLootGenerator::new);
		GreenResurgence.onPostInit();

	}
}
