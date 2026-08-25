package com.diamssword.greenresurgence.systems.crafting.recipesProviders;

import com.diamssword.greenresurgence.systems.crafting.SimpleRecipe;
import com.diamssword.greenresurgence.systems.crafting.UniversalResource;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import java.util.Map;

public class SimpleJsonProvider implements IRecipesProvider {

	private SimpleRecipe recipe;

	@Override
	public Map<String, SimpleRecipe> getRecipes(String id, World world) {
		if(recipe != null)
			return Map.of(id, recipe);
		return Map.of();
	}

	@Override
	public void fromJson(JsonObject ob) throws Exception {
		recipe = SimpleRecipe.deserializer(ob);
	}

	@Override
	public void deserializer(NbtCompound ob) {
		if(ob.contains("ingredients") && ob.contains("result")) {
			var ingredients = IRecipesProvider.unserializeIngredients(ob);
			var res = UniversalResource.fromNBT(ob.getCompound("result"));
			recipe = new SimpleRecipe(res, ingredients);
		}
	}

	@Override
	public NbtCompound serialize() {
		NbtCompound res = new NbtCompound();

		if(recipe != null) {
			IRecipesProvider.serializeIngredients(recipe, res);
			res.put("result", recipe.result(null).toNBT());
		}

		return res;
	}
}
