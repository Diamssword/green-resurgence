package com.diamssword.greenresurgence.systems.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SimpleRecipe {
	private Identifier id;
	private final UniversalResource result;
	private final List<UniversalResource> ingredients;

	public SimpleRecipe setID(Identifier collection, String recipeID) {
		this.id = new Identifier(collection.getNamespace(), collection.getPath() + "/" + recipeID);

		return this;
	}

	public SimpleRecipe setID(Identifier fullID) {
		this.id = fullID;
		return this;
	}

	@Nullable
	public Identifier getId() {
		return id;
	}

	public SimpleRecipe(ItemStack result, ItemStack... ingredients) {
		this.result = UniversalResource.fromItem(result);
		this.ingredients = Arrays.stream(ingredients).map(UniversalResource::fromItem).toList();
	}

	public SimpleRecipe(Identifier result) {
		this.result = UniversalResource.fromItem(result);
		this.ingredients = new ArrayList<>();
	}

	public SimpleRecipe(Item result, ItemStack... ingredients) {
		this(new ItemStack(result), ingredients);
	}

	public SimpleRecipe(UniversalResource res, List<UniversalResource> ingredients) {
		this.result = res;
		this.ingredients = ingredients;
	}

	public UniversalResource result(@Nullable PlayerEntity player) {
		return result;
	}

	public Optional<Block> blocksResult() {
		if(this.result.getType() == UniversalResource.Type.item) {
			if(this.result.asItem().getItem() instanceof BlockItem be) {
				return Optional.of(be.getBlock());
			}
		}
		return Optional.empty();

	}

	public List<UniversalResource> ingredients(@Nullable PlayerEntity player) {
		return ingredients;
	}

	public static SimpleRecipe deserializer(JsonObject ob) throws Exception {
		if(ob.has("ingredients") && ob.has("result")) {
			var res = UniversalResource.deserializer(ob.getAsJsonObject("result"));
			var r = new SimpleRecipe(res, deserializeIngredients(ob));
			if(ob.has("id"))
				r.setID(new Identifier(ob.get("id").getAsString()));
			return r;

		} else
			throw new Exception("missing 'ingredients' or 'result' element");

	}

	public JsonObject serialize() {
		var res = new JsonObject();
		var ia = new JsonArray();
		this.ingredients.forEach(v -> ia.add(v.serializer()));
		res.add("ingredients", ia);
		res.add("result", this.result.serializer());
		if(this.id != null)
			res.addProperty("id", id.toString());
		return res;

	}

	public static List<UniversalResource> deserializeIngredients(JsonObject ob) throws Exception {
		var ingsR = new ArrayList<UniversalResource>();
		for(JsonElement ing : ob.get("ingredients").getAsJsonArray()) {
			ingsR.add(UniversalResource.deserializer(ing.getAsJsonObject()));
		}
		return ingsR;
	}

	public static List<SimpleRecipe> deserializerMulti(JsonObject ob) throws Exception {
		var res = new ArrayList<SimpleRecipe>();
		if(ob.has("ingredients") && ob.has("results")) {
			for(JsonElement ing : ob.get("results").getAsJsonArray()) {
				res.add(new SimpleRecipe(UniversalResource.deserializer(ing.getAsJsonObject()), deserializeIngredients(ob)));
			}
		} else
			throw new Exception("missing 'ingredients' or 'result' element");
		return res;
	}

	public static void serializer(PacketByteBuf write, SimpleRecipe val) {
		if(val.getId() != null)
			write.writeString(val.getId().toString());
	}

	public static SimpleRecipe unserializer(PacketByteBuf read) {
		var id = read.readString();
		if(!id.isEmpty()) {

			var id1 = new Identifier(id);
			return Recipes.getRecipe(id1).orElse(new SimpleRecipe(ItemStack.EMPTY.copy(), ItemStack.EMPTY.copy()));
		}
		return null;
	}
}
