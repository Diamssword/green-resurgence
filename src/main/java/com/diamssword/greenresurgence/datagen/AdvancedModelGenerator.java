package com.diamssword.greenresurgence.datagen;

import com.diamssword.greenresurgence.GreenResurgence;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AdvancedModelGenerator {
	public static Map<Block, Identifier> fluidLikes = new HashMap<>();

	public static void create(BlockStateModelGenerator gen) {
		fluidLikes.forEach((k, v) -> {
			var id = Registries.BLOCK.getId(k);
			var key = TextureKey.of("0");
			for(int i = 1; i < 9; i++) {
				int ind = i * 2;
				new Model(Optional.of(GreenResurgence.asRessource("block/fluidlike/base/" + ind)), Optional.empty(), key)
						.upload(GreenResurgence.asRessource("block/fluidlike/" + id.getPath() + "/" + i), TextureMap.of(key, v), gen.modelCollector);
				new Model(Optional.of(GreenResurgence.asRessource("block/fluidlike/base/" + ind + "_off")), Optional.empty(), key)
						.upload(GreenResurgence.asRessource("block/fluidlike/" + id.getPath() + "/" + i + "_off"), TextureMap.of(key, v), gen.modelCollector);
			}
			gen.blockStateCollector.accept(new SchematicBlockStateSupplier(k, "fluidlike", Map.of("sub", id.getPath())));


		});
	}
}
