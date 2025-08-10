package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.datagen.LangGenerator;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Shields implements ItemRegistryContainer {
	public static final List<Item> specialRenderRegister = new ArrayList<>();
	private static final OwoItemGroup GROUP = MItems.GROUP;

	public static final Item SHIELD_TRASH_CAN = new ShieldItem(defaut().maxDamage(500));
	public static final Item SHIELD_TRASH_CAN_GREEN = new ShieldItem(defaut().maxDamage(500));
	public static final Item SHIELD_WOOD_PLANK = new ShieldItem(defaut().maxDamage(500));
	public static final Item SHIELD_NO_WAY = new ShieldItem(defaut().maxDamage(500));
	public static final Item SHIELD_POLICE = new ShieldItem(defaut().maxDamage(500));
	public static final Item SHIELD_BARREL_BLUE = new ShieldItem(defaut().maxDamage(500));
	public static final Item SHIELD_BARREL_RED = new ShieldItem(defaut().maxDamage(500));
	public static final Item SHIELD_BARREL_RUST = new ShieldItem(defaut().maxDamage(500));
	public static final Item SHIELD_BARREL_TOXIC = new ShieldItem(defaut().maxDamage(500));
	public static final Item SHIELD_FRIDGE = new ShieldItem(defaut().maxDamage(500));
	public static final Item SHIELD_FRIDGE_SQUARE = new ShieldItem(defaut().maxDamage(500));

	private static OwoItemSettings defaut() {
		return new OwoItemSettings().group(GROUP).tab(1);
	}

	@Override
	public void postProcessField(String namespace, Item value, String identifier, Field field) {
		specialRenderRegister.add(value);
		LangGenerator.auto_name.put(new Identifier(namespace, "tools/shields/" + identifier), identifier);
	}
}
