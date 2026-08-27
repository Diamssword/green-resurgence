package com.diamssword.greenresurgence.systems.crafting;

import net.minecraft.util.Identifier;

import java.util.Optional;

@SuppressWarnings("ALL")
public class Recipes {

	public static RecipeLoader loader = new RecipeLoader();

	public static Optional<RecipeCollection> get(Identifier id) {
		return loader.getCollection(id);
	}

	public static Optional<SimpleRecipe> getRecipe(Identifier id) {
		var i = get(RecipeCollection.getCollectionFromFullId(id));
		if(i.isPresent()) {
			return i.get().getById(id.getPath().substring(id.getPath().lastIndexOf("/") + 1));
		}
		return Optional.empty();
	}
}
