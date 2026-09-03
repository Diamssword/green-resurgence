package com.diamssword.greenresurgence.systems.crafting;

import com.diamssword.greenresurgence.systems.crafting.stonecutters.IStoneCutterTypeRecipe;
import net.minecraft.util.Identifier;

import java.util.Optional;

@SuppressWarnings("ALL")
public class Recipes {

	public static RecipeLoader loader = new RecipeLoader();

	public static Optional<RecipeCollection> get(Identifier id) {
		return loader.getCollection(id);
	}

	public static Optional<SimpleRecipe> getRecipe(ComposedIdentifier id) {
		var i = get(id.getCollection());
		if(i.isPresent()) {
			return i.get().getById(id.getId());
		}
		return Optional.empty();
	}

	public static Optional<IStoneCutterTypeRecipe> getStoneCutter(Identifier collection) {
		return loader.getStoneCutter(collection);
	}
}
