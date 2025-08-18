package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.systems.crafting.RecipeLoader;
import com.diamssword.greenresurgence.systems.crafting.Recipes;
import com.diamssword.greenresurgence.systems.lootables.Lootables;
import com.diamssword.greenresurgence.systems.lootables.LootablesReloader;

public class DictionaryPackets {
	public record LootableList(LootablesReloader loader) {
	}


	public record RecipeList(RecipeLoader loader) {
	}

	public static void init() {
		Channels.MAIN.registerClientbound(LootableList.class, (msg, ctx) -> {
			Lootables.loader = msg.loader;
		});
		Channels.MAIN.registerClientbound(RecipeList.class, (msg, ctx) -> {
			Recipes.loader = msg.loader;
		});
	}
}
