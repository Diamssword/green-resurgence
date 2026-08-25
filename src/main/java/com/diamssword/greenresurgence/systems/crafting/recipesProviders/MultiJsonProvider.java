package com.diamssword.greenresurgence.systems.crafting.recipesProviders;

import com.diamssword.greenresurgence.systems.crafting.SimpleRecipe;
import com.diamssword.greenresurgence.systems.crafting.UniversalResource;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiJsonProvider implements IRecipesProvider {

	private List<SimpleRecipe> recipes = new ArrayList<>();

	@Override
	public Map<String, SimpleRecipe> getRecipes(String id, World world) {
		if(recipes != null) {
			var m = new HashMap<String, SimpleRecipe>();
			for(int i = 0; i < recipes.size(); i++) {
				m.put(id + i, recipes.get(i));
			}
			return m;
		}

		return Map.of();
	}

	@Override
	public void fromJson(JsonObject ob) throws Exception {
		recipes = SimpleRecipe.deserializerMulti(ob);
	}

	@Override
	public void deserializer(NbtCompound ob) {
		recipes.clear();
		if(ob.contains("ingredients") && ob.contains("results")) {
			var ingredients = IRecipesProvider.unserializeIngredients(ob);
			var ls1 = ob.getList("results", NbtElement.COMPOUND_TYPE);
			for(int i = 0; i < ls1.size(); i++) {
				var res = UniversalResource.fromNBT(ls1.getCompound(i));
				recipes.add(new SimpleRecipe(res, ingredients));
			}
		}
	}


	@Override
	public NbtCompound serialize() {
		NbtCompound res = new NbtCompound();

		if(recipes != null && !recipes.isEmpty()) {
			IRecipesProvider.serializeIngredients(this.recipes.get(0), res);

			NbtList results = new NbtList();

			recipes.forEach(r -> {
				results.add(r.result(null).toNBT());
			});

			res.put("results", results);
		}

		return res;
	}
}