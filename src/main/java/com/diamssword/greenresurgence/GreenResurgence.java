package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.commands.*;
import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import com.diamssword.greenresurgence.materials.Materials;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.structure.ItemPlacers;
import com.diamssword.greenresurgence.systems.Events;
import com.diamssword.greenresurgence.systems.attributs.Attributes;
import com.diamssword.greenresurgence.systems.clothing.ClothingLoader;
import com.diamssword.greenresurgence.systems.crafting.Recipes;
import com.diamssword.greenresurgence.systems.faction.BaseInteractions;
import com.diamssword.greenresurgence.systems.lootables.Lootables;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.wispforest.owo.registration.annotations.RegistryNamespace;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import io.wispforest.owo.registration.reflect.FieldProcessingSubject;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import io.wispforest.owo.util.ReflectionUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class GreenResurgence implements ModInitializer {
	public static final String ID = "green_resurgence";
	public static final Logger LOGGER = LoggerFactory.getLogger("green_resurgence");

	public static Identifier asRessource(String name) {
		return new Identifier(ID, name);
	}

	public static final com.diamssword.greenresurgence.ResurgenceConfig CONFIG = com.diamssword.greenresurgence.ResurgenceConfig.createAndLoad();

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(Lootables.loader);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ClothingLoader.instance);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(Recipes.loader);
		FieldRegistrationHandler.register(MItems.class, ID, false);
		registerSubCat(Weapons.class, ID, "tools/", false);
		FieldRegistrationHandler.register(MBlocks.class, ID, false);
		FieldRegistrationHandler.register(MBlockEntities.class, ID, false);
		MBlockEntities.registerAll();
		FieldRegistrationHandler.register(Containers.class, ID, false);
		FieldRegistrationHandler.register(MEntities.class, ID, false);
		ItemPlacers.init();
		MItems.GROUP.initialize();
		Channels.initialize();
		GenericBlocks.register();
		GenericBlocks.GENERIC_GROUP.initialize();
		Materials.init();
		registerCommand("giveStructureItem", StructureItemCommand::register);
		registerCommand("faction", FactionCommand::register);
		registerCommand("structureBlockHelper", StructureBlockHelperCommand::register);
		registerCommand("resurgenceGui", OpenScreenCommand::register);
		registerCommand("recipeHelper", RecipeHelperCommand::register);
		registerCommand("character", SkinCommand::register);
		registerCommand("pstats", PStatsCommand::register);
		BaseInteractions.register();
		Attributes.init();
		Events.init();
		ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
			onPostInit();
		});

	}

	private static boolean havePostInited = false;

	/**
	 * Called when a client join a world
	 * Called only once
	 */
	public static void onPostInit() {
		if (!havePostInited) {
			havePostInited = true;
		}
	}

	public void registerCommand(String name, Consumer<LiteralArgumentBuilder<ServerCommandSource>> builder) {
		LiteralArgumentBuilder<ServerCommandSource> l = CommandManager.literal(name);
		builder.accept(l);
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> dispatcher.register(l)));
	}

	public static <T> void registerSubCat(Class<? extends AutoRegistryContainer<T>> clazz, String namespace, String subname, boolean recurseIntoInnerClasses) {
		AutoRegistryContainer<T> container = ReflectionUtils.tryInstantiateWithNoArgs(clazz);

		ReflectionUtils.iterateAccessibleStaticFields(clazz, container.getTargetFieldType(), createProcessor((fieldValue, identifier, field) -> {
			Registry.register(container.getRegistry(), new Identifier(namespace, subname + identifier), fieldValue);
			container.postProcessField(namespace, fieldValue, identifier, field);
		}, container));

		if (recurseIntoInnerClasses) {
			ReflectionUtils.forApplicableSubclasses(clazz, AutoRegistryContainer.class, subclass -> {
				var classModId = namespace;
				if (subclass.isAnnotationPresent(RegistryNamespace.class))
					classModId = subclass.getAnnotation(RegistryNamespace.class).value();
				registerSubCat((Class<? extends AutoRegistryContainer<T>>) subclass, classModId, subname, true);
			});
		}

		container.afterFieldProcessing();
	}

	private static <T> ReflectionUtils.FieldConsumer<T> createProcessor(ReflectionUtils.FieldConsumer<T> delegate, FieldProcessingSubject<T> handler) {
		return (value, name, field) -> {
			if (!handler.shouldProcessField(value, name, field)) return;
			delegate.accept(value, name, field);
		};
	}
}