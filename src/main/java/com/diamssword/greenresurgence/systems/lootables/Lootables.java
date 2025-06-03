package com.diamssword.greenresurgence.systems.lootables;

import com.diamssword.greenresurgence.GreenResurgence;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class Lootables {
	/*
	Le temps de refresh d'un block en millisecondes (7J ici)
	 */
	public static LootablesReloader loader = new LootablesReloader();
	public static final TagKey<Item> WRENCH = createTool("wrench");
	public static final TagKey<Item> HAMMER = createTool("hammer");
	public static final TagKey<Item> HAND = createTool("hand");
	public static final TagKey<Item> CONTAINER = createTool("container");

	private static TagKey<Item> createTool(String name) {
		return TagKey.of(RegistryKeys.ITEM, GreenResurgence.asRessource("lootable/tools/" + name));
	}

	public static boolean isGoodTool(Block b, Identifier tool) {
		return loader.getTable(b).map(lootable -> lootable.asTool(tool)).orElse(false);
	}

	public static Identifier getTableForBlock(Block b, Identifier tool) {
		var val = loader.getTable(b);
		if (val.isPresent() && val.get().asTool(tool)) {
			return val.get().getLootForTool(tool);
		}
		return null;
	}

	public static Block getEmptyBlock(Block b) {
		return loader.getTable(b).map(Lootable::getEmptyBlock).orElse(Blocks.AIR);

	}


}

