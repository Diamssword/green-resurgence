package com.diamssword.greenresurgence.systems.crafting.recipesProviders;

import com.diamssword.greenresurgence.systems.crafting.SimpleRecipe;
import com.diamssword.greenresurgence.systems.crafting.UniversalResource;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CreativeTabConquest extends CreativeTabJsonProvider {
	protected List<String> tags = new ArrayList<>();

	@Override
	protected Map<String, SimpleRecipe> createRecipes(World world, Consumer<Block> addBlockToWhitelist) {
		Map<String, SimpleRecipe> recipes = new HashMap<>();
		var trueTags = tags.stream().map(t -> TagKey.of(RegistryKeys.BLOCK, new Identifier(t))).toList();
		tabs.forEach(e -> {
			var entry = Registries.ITEM_GROUP.getEntry(RegistryKey.of(RegistryKeys.ITEM_GROUP, e));
			entry.ifPresent(tab -> {
				tab.value().getDisplayStacks().forEach(st -> {
					if(st.getItem() instanceof BlockItem be) {
						if(be.getBlock().getDefaultState().isFullCube(world, BlockPos.ORIGIN) && trueTags.stream().anyMatch(t -> be.getBlock().getDefaultState().isIn(t))) {
							var id = Registries.ITEM.getId(st.getItem()).toUnderscoreSeparatedString();
							recipes.put(id, new SimpleRecipe(UniversalResource.fromItem(st).withCustomName(Text.literal("[Material] ").append(st.getName())).withLore(Text.literal("This is just craft material"), Text.literal("You can craft it into useful shapes on the stonework table")), ingredients));
						}
					}
				});
			});
		});
		return recipes;
	}

	@Override
	public void fromNbt(NbtCompound ob) {
		super.fromNbt(ob);
		var strL = ob.getList("tags", NbtElement.STRING_TYPE);
		tags = new ArrayList<>();
		for(NbtElement nbtElement : strL) {
			tags.add(nbtElement.asString());
		}
	}

	@Override
	public NbtCompound toNbt() {
		var o = super.toNbt();
		var ls = new NbtList();
		for(String material : tags) {
			ls.add(NbtString.of(material));
		}
		o.put("tags", ls);
		return o;
	}

	@Override
	public void fromJson(JsonObject ob) throws Exception {
		super.fromJson(ob);
		if(ob.has("tags"))
			this.tags = ob.get("tags").getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList();
	}
}