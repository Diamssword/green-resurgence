package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.commands.FactionCommand;
import com.diamssword.greenresurgence.commands.StructureBlockHelperCommand;
import com.diamssword.greenresurgence.commands.StructureItemCommand;
import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import com.diamssword.greenresurgence.materials.Materials;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.structure.ItemPlacers;
import com.diamssword.greenresurgence.systems.faction.BaseInteractions;
import com.diamssword.greenresurgence.systems.lootables.Lootables;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

		FieldRegistrationHandler.register(MItems.class, ID, false);
		FieldRegistrationHandler.register(MBlocks.class, ID, false);
		FieldRegistrationHandler.register(MBlockEntities.class, ID, false);
		FieldRegistrationHandler.register(Containers.class, ID, false);
		ItemPlacers.init();
		MItems.GROUP.initialize();
		Channels.initialize();
		GenericBlocks.register();
		GenericBlocks.GENERIC_GROUP.initialize();
		Materials.init();
		registerCommand("giveStructureItem",StructureItemCommand::register);
		registerCommand("faction", FactionCommand::register);
		registerCommand("structureBlockHelper", StructureBlockHelperCommand::register);
		BaseInteractions.register();
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
			Lootables.init();
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