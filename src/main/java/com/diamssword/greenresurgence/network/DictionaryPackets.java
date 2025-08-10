package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.systems.character.stats.ClassesLoader;
import com.diamssword.greenresurgence.systems.clothing.ClothingLoader;
import com.diamssword.greenresurgence.systems.crafting.RecipeLoader;
import com.diamssword.greenresurgence.systems.crafting.Recipes;
import com.diamssword.greenresurgence.systems.lootables.Lootables;
import com.diamssword.greenresurgence.systems.lootables.LootablesReloader;

public class DictionaryPackets {
	public record LootableList(LootablesReloader loader) {
	}

	public record ClothingList(ClothingLoader loader) {
	}

	public record RecipeList(RecipeLoader loader) {
	}

	public record RoleList(ClassesLoader loader) {
	}

	public static void init() {
		Channels.MAIN.registerClientbound(LootableList.class, (msg, ctx) -> {
			Lootables.loader = msg.loader;
		});
		Channels.MAIN.registerClientbound(ClothingList.class, (msg, ctx) -> {
			ClothingLoader.instance = msg.loader;
		});
		Channels.MAIN.registerClientbound(RecipeList.class, (msg, ctx) -> {
			Recipes.loader = msg.loader;
		});
		Channels.MAIN.registerClientbound(RoleList.class, (msg, ctx) -> {
			ClassesLoader.instance = msg.loader;
		});

	}
}
