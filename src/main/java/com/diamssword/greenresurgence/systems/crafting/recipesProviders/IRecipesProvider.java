package com.diamssword.greenresurgence.systems.crafting.recipesProviders;

import com.diamssword.greenresurgence.systems.crafting.SimpleRecipe;
import com.diamssword.greenresurgence.systems.crafting.UniversalResource;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface IRecipesProvider {

	public Map<String, SimpleRecipe> getRecipes(String id, World world, Consumer<Block> addBlockToWhitelist);

	public void fromJson(JsonObject ob) throws Exception;

	public void fromNbt(NbtCompound ob);

	public NbtCompound toNbt();

	public static List<UniversalResource> unserializeIngredients(NbtCompound nbt) {
		var ingredients = new ArrayList<UniversalResource>();
		if(nbt.contains("ingredients")) {
			var ls = nbt.getList("ingredients", NbtElement.COMPOUND_TYPE);
			for(int i = 0; i < ls.size(); i++) {
				ingredients.add(UniversalResource.fromNBT(ls.getCompound(i)));
			}
		}
		return ingredients;
	}

	public static NbtCompound serializeIngredients(SimpleRecipe recipe, NbtCompound tag) {
		NbtList ingredients = new NbtList();

		recipe.ingredients(null).forEach(v -> ingredients.add(v.toNBT()));

		tag.put("ingredients", ingredients);
		return tag;
	}
}
