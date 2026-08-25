package com.diamssword.greenresurgence.systems.crafting.recipesProviders;

import com.diamssword.greenresurgence.systems.crafting.SimpleRecipe;
import com.diamssword.greenresurgence.systems.crafting.UniversalResource;
import com.google.gson.JsonObject;
import net.minecraft.item.BlockItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreativeTabJsonProvider implements IRecipesProvider {

	private List<UniversalResource> ingredients = new ArrayList<>();
	private List<Identifier> tabs = new ArrayList<>();
	private boolean ignoreFullBlocks = true;
	private boolean blocksOnly = true;

	@Override
	public Map<String, SimpleRecipe> getRecipes(String id, World world) {
		var recipes = createRecipes(world);
		var m = new HashMap<String, SimpleRecipe>();
		recipes.forEach((k, v) -> m.put(id + "_" + k, v));

		return m;

	}

	@Override
	public void fromJson(JsonObject ob) throws Exception {
		tabs.clear();
		var tabs = ob.get("tabs").getAsJsonArray();
		tabs.forEach(e -> {
			var tabId = new Identifier(e.getAsString());
			this.tabs.add(tabId);
		});
		ingredients = SimpleRecipe.deserializeIngredients(ob);
		if(ob.has("ignoreFullBlocks"))
			this.ignoreFullBlocks = ob.get("ignoreFullBlocks").getAsBoolean();
		if(ob.has("blocksOnly"))
			this.blocksOnly = ob.get("blocksOnly").getAsBoolean();
	}

	private Map<String, SimpleRecipe> createRecipes(World world) {
		Map<String, SimpleRecipe> recipes = new HashMap<>();
		tabs.forEach(e -> {
			var entry = Registries.ITEM_GROUP.getEntry(RegistryKey.of(RegistryKeys.ITEM_GROUP, e));
			entry.ifPresent(tab -> {
				tab.value().getDisplayStacks().forEach(st -> {
					if(!blocksOnly || st.getItem() instanceof BlockItem) {
						var flg = true;
						if(ignoreFullBlocks && st.getItem() instanceof BlockItem be) {
							flg = !be.getBlock().getDefaultState().isFullCube(world, BlockPos.ORIGIN);
						}
						if(flg) {
							var id = Registries.ITEM.getId(st.getItem()).toUnderscoreSeparatedString();
							recipes.put(id, new SimpleRecipe(UniversalResource.fromItem(st), ingredients));
						}
					}
				});
			});
		});
		return recipes;
	}

	@Override
	public void deserializer(NbtCompound ob) {
		tabs.clear();
		if(ob.contains("ingredients") && ob.contains("tabs")) {
			var ls1 = ob.getList("tabs", NbtElement.STRING_TYPE);
			ls1.forEach(l -> {
				if(l instanceof NbtString st)
					tabs.add(new Identifier(st.asString()));
			});
			this.blocksOnly = ob.getBoolean("blocksOnly");
			this.ignoreFullBlocks = ob.getBoolean("ignoreFull");
			ingredients = IRecipesProvider.unserializeIngredients(ob);
		}

	}


	@Override
	public NbtCompound serialize() {
		NbtCompound res = new NbtCompound();

		if(!ingredients.isEmpty()) {
			NbtList ing = new NbtList();

			this.ingredients.forEach(v -> ing.add(v.toNBT()));
			res.putBoolean("ignoreFull", ignoreFullBlocks);
			res.putBoolean("blocksOnly", blocksOnly);
			res.put("ingredients", ing);
			NbtList tabs = new NbtList();
			this.tabs.forEach(r -> tabs.add(NbtString.of(r.toString())));
			res.put("tabs", tabs);
		}

		return res;
	}
}