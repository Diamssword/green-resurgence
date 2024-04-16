package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.commands.FactionCommand;
import com.diamssword.greenresurgence.commands.StructureItemCommand;
import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.structure.ItemPlacers;
import com.diamssword.greenresurgence.systems.faction.BaseInteractions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class GreenResurgence implements ModInitializer {
	public static final String ID="green_resurgence";
	public static final Logger LOGGER = LoggerFactory.getLogger("green_resurgence");

	@Override
	public void onInitialize() {
		FieldRegistrationHandler.register(MItems.class, ID, false);
		FieldRegistrationHandler.register(MBlocks.class, ID, false);
		ItemPlacers.init();
		MItems.GROUP.initialize();
		Channels.initialize();
		GenericBlocks.register();
		GenericBlocks.GENERIC_GROUP.initialize();
		registerCommand("giveStructureItem",StructureItemCommand::register);
		registerCommand("faction", FactionCommand::register);
		BaseInteractions.register();

	}
	public void registerCommand(String name, Consumer<LiteralArgumentBuilder<ServerCommandSource>> builder)
	{
		LiteralArgumentBuilder<ServerCommandSource> l=CommandManager.literal(name);
		builder.accept(l);
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> dispatcher.register(l)));
	}
}