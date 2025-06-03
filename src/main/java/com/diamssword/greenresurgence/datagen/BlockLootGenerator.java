package com.diamssword.greenresurgence.datagen;

import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;

public class BlockLootGenerator extends FabricBlockLootTableProvider {
	public static List<Block> blocks = new ArrayList<>();

	public BlockLootGenerator(FabricDataOutput dataOutput) {
		super(dataOutput);
	}

	@Override
	public void generate() {
		blocks.forEach(this::addDrop);
		GenericBlocks.sets.forEach(set -> {
			set.blockDropGenerator(this);
		});
	}
}
