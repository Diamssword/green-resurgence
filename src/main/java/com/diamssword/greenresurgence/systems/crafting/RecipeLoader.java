package com.diamssword.greenresurgence.systems.crafting;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.DictionaryPackets;
import com.diamssword.greenresurgence.systems.crafting.recipesProviders.*;
import com.diamssword.greenresurgence.systems.crafting.stonecutters.ConquestStoneCutterRecipe;
import com.diamssword.greenresurgence.systems.crafting.stonecutters.IStoneCutterTypeRecipe;
import com.diamssword.greenresurgence.systems.crafting.stonecutters.MutltiStoneCutterRecipe;
import com.diamssword.greenresurgence.systems.faction.BaseInteractions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.Supplier;

public class RecipeLoader implements SimpleSynchronousResourceReloadListener {

	private final Map<Identifier, RecipeCollection> registry = new HashMap<>();
	private final Map<Identifier, IStoneCutterTypeRecipe> stoneCuttersRegistry = new HashMap<>();
	private final Map<Identifier, List<NbtCompound>> loadedProviders = new HashMap<>();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static final Logger LOGGER = LogUtils.getLogger();
	private boolean shouldSync = false;
	private Map<String, Supplier<IRecipesProvider>> recipesProviders = new HashMap<>();

	public RecipeLoader() {
		recipesProviders.put("simple", SimpleJsonProvider::new);
		recipesProviders.put("multi", MultiJsonProvider::new);
		recipesProviders.put("creativeTab", CreativeTabJsonProvider::new);
		recipesProviders.put("creativeTabConquest", CreativeTabConquest::new);
		stoneCuttersRegistry.put(GreenResurgence.asRessource("stone_cutter"), new MutltiStoneCutterRecipe(new ConquestStoneCutterRecipe()));

	}

	public Optional<IStoneCutterTypeRecipe> getStoneCutter(Identifier id) {
		return Optional.ofNullable(stoneCuttersRegistry.get(id));
	}

	public Optional<RecipeCollection> getCollection(Identifier id) {
		return Optional.ofNullable(registry.get(id));
	}

	@Override
	public Identifier getFabricId() {
		return GreenResurgence.asRessource("grecipes");
	}

	public void compileRecipes(World world) {
		if(world != null) {
			LOGGER.info("loading itemGroups content");
			var ctx = new ItemGroup.DisplayContext(world.getEnabledFeatures(), false, world.getRegistryManager());
			Registries.ITEM_GROUP.getKeys().forEach(k -> {

				Registries.ITEM_GROUP.get(k.getValue()).updateEntries(ctx);
			});
		}
		loadedProviders.forEach((k, v) -> {
			var coll = new RecipeCollection(k);
			registry.put(k, coll);
			v.forEach(val -> {
				var idR = val.getString("baseID");
				if(!idR.isBlank()) {
					try {
						if(val.contains("type")) {
							var type = val.getString("type");
							var providerF = recipesProviders.get(type);
							if(providerF != null) {
								var provider = providerF.get();
								provider.fromNbt(val);
								var recipes = provider.getRecipes(idR, world, (b) -> BaseInteractions.allowedListRecipe.addBlockToList(world, b));
								if(!recipes.isEmpty()) {
									coll.addAll(recipes);
								}
							} else
								LOGGER.error("[unserializing]no provider found for type : '{}' for {} from {}", type, idR, getFabricId());
						} else {
							LOGGER.error("[unserializing]missing 'type' field for {} from {}", idR, getFabricId());
						}
					} catch(Exception e) {
						LOGGER.error("Failed to decode a recipe provider: {} for collection '{}' of mod '{}'", idR, k, GreenResurgence.ID);
						e.printStackTrace();
					}
				}
			});
		});
	}

	@Override
	public void reload(ResourceManager manager) {
		registry.clear();
		loadedProviders.clear();
		BaseInteractions.allowedListRecipe.clear();
		manager.findResources("grecipes", v -> v.getPath().endsWith(".json") && v.getPath().split("/").length > 2).forEach((id, re) -> {
			try {
				BufferedReader reader = re.getReader();
				try {
					var jsonElement = JsonHelper.deserialize(GSON, reader, JsonObject.class);
					var recipeId = id.getPath().substring(id.getPath().lastIndexOf("/") + 1).replace(".json", "");
					if(jsonElement.has("type")) {
						var type = jsonElement.get("type").getAsString();
						var providerF = recipesProviders.get(type);
						if(recipesProviders != null) {
							var provider = providerF.get();
							provider.fromJson(jsonElement);
							var id1 = new Identifier(id.getNamespace(), id.getPath().substring(id.getPath().indexOf("/") + 1));
							id1 = new Identifier(id1.getNamespace(), id1.getPath().substring(0, id1.getPath().lastIndexOf("/")));
							var nbt = provider.toNbt();
							var ls = loadedProviders.computeIfAbsent(id1, _v -> new ArrayList<>());
							nbt.putString("type", jsonElement.get("type").getAsString());
							nbt.putString("baseID", recipeId.toString());
							ls.add(nbt);
						} else
							LOGGER.error("no provider found for type : '{}' for {} from {}", type, recipeId, getFabricId());
					} else {
						LOGGER.error("missing 'type' field for {} from {}", recipeId, getFabricId());
					}
				} catch(Exception e) {
					LOGGER.error("Couldn't parse data file {} from {}", id, getFabricId(), e);
				} finally {
					((Reader) reader).close();
				}
			} catch(JsonParseException | IOException | IllegalArgumentException exception) {
				LOGGER.error("Couldn't parse data file {} from {}", id, getFabricId(), exception);
			}
		});
		shouldSync = true;
	}

	public void worldTick(MinecraftServer server) {
		if(shouldSync) {
			shouldSync = false;
			compileRecipes(server.getOverworld());
			Channels.serverHandle(server).send(new DictionaryPackets.RecipeList(this));
		}
	}

	public static void serializer(PacketByteBuf write, RecipeLoader val) {
		var ob = new NbtCompound();
		val.loadedProviders.forEach((k, v) -> {
			var ls = new NbtList();
			ls.addAll(v);
			ob.put(k.toString(), ls);
		});
		write.writeNbt(ob);
	}

	public static RecipeLoader unserializer(PacketByteBuf read) {
		RecipeLoader loader = new RecipeLoader();
		var ob = read.readNbt();
		ob.getKeys().forEach(id -> {
			try {
				var collection = new Identifier(id);
				var lsR = new ArrayList<NbtCompound>();
				loader.loadedProviders.put(collection, lsR);
				var ls = ob.getList(id, NbtElement.COMPOUND_TYPE);
				var coll = loader.registry.get(collection);
				ls.forEach(prov -> {
					if(prov instanceof NbtCompound provO) {
						lsR.add(provO);
					}

				});
			} catch(Exception e) {
				LOGGER.error("Failed to decode a recipe collection: {} for {}", id, GreenResurgence.ID);
				e.printStackTrace();
			}


		});
		return loader;
	}
}
