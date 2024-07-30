package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.commands.*;
import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import com.diamssword.greenresurgence.materials.Materials;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.structure.ItemPlacers;
import com.diamssword.greenresurgence.systems.Events;
import com.diamssword.greenresurgence.systems.clothing.ClothingLoader;
import com.diamssword.greenresurgence.systems.crafting.RecipeLoader;
import com.diamssword.greenresurgence.systems.crafting.Recipes;
import com.diamssword.greenresurgence.systems.faction.BaseInteractions;
import com.diamssword.greenresurgence.systems.lootables.Lootables;
import com.diamssword.greenresurgence.systems.lootables.LootablesReloader;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import io.wispforest.owo.ui.component.EntityComponent;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModelParsingException;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class GreenResurgence implements ModInitializer {
	public static final String ID="green_resurgence";
	public static final Logger LOGGER = LoggerFactory.getLogger("green_resurgence");
	public static Identifier asRessource(String name)
	{
		return new Identifier(ID,name);
	}

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(Lootables.loader);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ClothingLoader.instance);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(Recipes.loader);
		FieldRegistrationHandler.register(MItems.class, ID, false);
		FieldRegistrationHandler.register(MBlocks.class, ID, false);
		FieldRegistrationHandler.register(MBlockEntities.class, ID, false);
		FieldRegistrationHandler.register(Containers.class, ID, false);
		FieldRegistrationHandler.register(MEntities.class, ID, false);
		ItemPlacers.init();
		MItems.GROUP.initialize();
		Channels.initialize();
		GenericBlocks.register();
		GenericBlocks.GENERIC_GROUP.initialize();
		Materials.init();
		Recipes.init();
		registerCommand("giveStructureItem",StructureItemCommand::register);
		registerCommand("faction", FactionCommand::register);
		registerCommand("structureBlockHelper", StructureBlockHelperCommand::register);
		registerCommand("resurgenceGui", OpenScreenCommand::register);
		registerCommand("recipeHelper", RecipeHelperCommand::register);
		BaseInteractions.register();
		Events.init();
		ServerLifecycleEvents.SERVER_STARTING.register((server)->{
			onPostInit();
		});

	}

	private static boolean havePostInited=false;
	/**
	 * Called when a client join a world
	 * Called only once
	 */
	public static void onPostInit()
	{
		if(!havePostInited) {
			havePostInited = true;
		}
	}
	public void registerCommand(String name, Consumer<LiteralArgumentBuilder<ServerCommandSource>> builder)
	{
		LiteralArgumentBuilder<ServerCommandSource> l=CommandManager.literal(name);
		builder.accept(l);
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> dispatcher.register(l)));
	}
}