package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.datagen.LangGenerator;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;

public class Weapons implements ItemRegistryContainer {
	//BASE_ATTACK_SPEED is 4;
	private static final float SLOWER = -3.5f;
	private static final float SLOW = -3f;
	private static final float NORMAL = -2.4f;
	private static final float FAST = -2f;
	private static final float FASTER = -1f;
	private static final float FASTEST = 1f;
	private static final OwoItemGroup GROUP = MItems.GROUP;

	public static final Item WRENCH = new SwordItem(ToolMaterials.IRON, 0, FAST, defaut());
	public static final Item SLEDGEHAMMER = new SwordItem(ToolMaterials.IRON, 1, NORMAL, defaut());

	/*public static final Item FLAME_SWORD_ONE_HANDED = new GeckoActivated(ToolMaterials.IRON, 7, NORMAL, 14, NORMAL, true, defaut());

	public static final Item CHAINSAW = new GeckoActivatedTwoHand(ToolMaterials.WOOD, 1, FAST, 20, SLOWER, false, defaut());
	public static final Item WEEDWACKER = new GeckoActivatedTwoHand(ToolMaterials.WOOD, 1, SLOW, 1, FASTEST, false, defaut());
*/
	private static OwoItemSettings defaut() {
		return new OwoItemSettings().group(GROUP).tab(1);
	}

	@Override
	public void postProcessField(String namespace, Item value, String identifier, Field field) {
		LangGenerator.auto_name.put(new Identifier(namespace, "tools/" + identifier), identifier);
	}
}
