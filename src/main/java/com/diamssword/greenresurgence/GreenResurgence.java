package com.diamssword.greenresurgence;

import com.diamssword.characters.api.CharactersApi;
import com.diamssword.greenresurgence.commands.*;
import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.containers.player.CustomPlayerInventory;
import com.diamssword.greenresurgence.effects.ResurgenceEffects;
import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import com.diamssword.greenresurgence.items.equipment.EquipmentItems;
import com.diamssword.greenresurgence.materials.Materials;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.structure.ItemPlacers;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.Events;
import com.diamssword.greenresurgence.systems.armor.ArmorLoader;
import com.diamssword.greenresurgence.systems.attributs.Attributes;
import com.diamssword.greenresurgence.systems.character.classes.ClassesRegister;
import com.diamssword.greenresurgence.systems.crafting.Recipes;
import com.diamssword.greenresurgence.systems.equipement.Equipments;
import com.diamssword.greenresurgence.systems.faction.BaseInteractions;
import com.diamssword.greenresurgence.systems.lootables.Lootables;
import com.diamssword.greenresurgence.utils.ClientSideHelper;
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

	//public static StatusEffect CONQUEST_SLOWNESS = null;
	public static final com.diamssword.greenresurgence.ResurgenceConfig CONFIG = com.diamssword.greenresurgence.ResurgenceConfig.createAndLoad();
	public static ClientSideHelper clientHelper = new ClientSideHelper();

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(Lootables.loader);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(Recipes.loader);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ArmorLoader.loader);
		FieldRegistrationHandler.register(MItems.class, ID, false);
		registerSubCat(Weapons.class, ID, "tools/", false);
		registerSubCat(Shields.class, ID, "tools/shields/", false);
		registerSubCat(EquipmentItems.class, ID, "equipments/", false);
		FieldRegistrationHandler.register(MBlocks.class, ID, false);
		FieldRegistrationHandler.register(MBlockEntities.class, ID, false);
		MBlockEntities.registerAll();
		FieldRegistrationHandler.register(Containers.class, ID, false);
		FieldRegistrationHandler.register(MEntities.class, ID, false);
		FieldRegistrationHandler.register(ResurgenceEffects.class, ID, false);
		MEntities.addAtributs();
		ItemPlacers.init();
		MItems.GROUP.initialize();
		Channels.initialize();
		GenericBlocks.register();
		GenericBlocks.GENERIC_GROUP.initialize();
		Materials.init();
		BaseInteractions.register();
		Attributes.init();
		Events.init();
		Equipments.init();
		registerCommand("giveStructureItem", StructureItemCommand::register);
		registerCommand("faction", FactionCommand::register);
		registerCommand("structureBlockHelper", StructureBlockHelperCommand::register);
		registerCommand("resurgenceGui", OpenScreenCommand::register);
		registerCommand("recipeHelper", RecipeHelperCommand::register);
		registerCommand("worldTools", WorldToolsCommand::register);

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
		if(!havePostInited) {
			havePostInited = true;
		}
		ClassesRegister.init();
		CharactersApi.instance.unattachComponentFromCharacters(CharactersApi.CHARACTER_ATTACHED_COMPONENT_INVENTORY);
		CharactersApi.instance.attachComponentToCharacters(asRessource("inventory"), p -> p.getComponent(Components.PLAYER_INVENTORY).getInventory(), CustomPlayerInventory::serializer, CustomPlayerInventory::unserializer);
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

		if(recurseIntoInnerClasses) {
			ReflectionUtils.forApplicableSubclasses(clazz, AutoRegistryContainer.class, subclass -> {
				var classModId = namespace;
				if(subclass.isAnnotationPresent(RegistryNamespace.class))
					classModId = subclass.getAnnotation(RegistryNamespace.class).value();
				registerSubCat((Class<? extends AutoRegistryContainer<T>>) subclass, classModId, subname, true);
			});
		}

		container.afterFieldProcessing();
	}

	private static <T> ReflectionUtils.FieldConsumer<T> createProcessor(ReflectionUtils.FieldConsumer<T> delegate, FieldProcessingSubject<T> handler) {
		return (value, name, field) -> {
			if(!handler.shouldProcessField(value, name, field)) return;
			delegate.accept(value, name, field);
		};
	}
}