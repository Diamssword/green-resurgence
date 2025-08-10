package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.datagen.LangGenerator;
import com.diamssword.greenresurgence.items.weapons.GeckoActivated;
import com.diamssword.greenresurgence.items.weapons.GeckoActivatedTwoHand;
import com.diamssword.greenresurgence.items.weapons.KnuckleItem;
import com.diamssword.greenresurgence.items.weapons.TwoHandedSword;
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
	public static final Item WRENCH_HOOK = new SwordItem(ToolMaterials.IRON, 2, FAST, defaut());
	public static final Item SLEDGEHAMMER = new SwordItem(ToolMaterials.IRON, 1, NORMAL, defaut());
	public static final Item IRON_PIPE = new SwordItem(ToolMaterials.IRON, 2, FAST, defaut());
	public static final Item BASIC_SPEAR_WOOD = new SwordItem(ToolMaterials.STONE, 3, FASTER, defaut());
	public static final Item BASEBALL_BAT_WOOD = new SwordItem(ToolMaterials.STONE, 5, SLOWER, defaut());
	public static final Item BASEBALL_BAT_WOOD_NAIL = new SwordItem(ToolMaterials.STONE, 6, SLOWER, defaut());
	public static final Item BASEBALL_BAT_ALUMINIUM = new SwordItem(ToolMaterials.IRON, 6, SLOW, defaut());
	public static final Item BASEBALL_BAT_ALUMINIUM_SAW = new SwordItem(ToolMaterials.IRON, 7, SLOW, defaut());
	public static final Item MAKESHIFT_MACHETTE = new SwordItem(ToolMaterials.IRON, 7, NORMAL, defaut());
	public static final Item IRON_PIPE_AXE = new SwordItem(ToolMaterials.IRON, 8, SLOW, defaut());
	public static final Item MAKESHIFT_SWORD = new SwordItem(ToolMaterials.IRON, 7, NORMAL, defaut());
	public static final Item MAKESHIFT_MACE = new SwordItem(ToolMaterials.IRON, 10, SLOWER, defaut());
	public static final Item MAKESHIFT_KNIFE = new SwordItem(ToolMaterials.IRON, 6, FAST, defaut());
	public static final Item MAKESHIFT_DAGGER = new SwordItem(ToolMaterials.IRON, 5, FASTER, defaut());
	public static final Item MAKESHIFT_AXE = new SwordItem(ToolMaterials.IRON, 8, SLOW, defaut());
	public static final Item MAKESHIFT_KATANA = new TwoHandedSword(ToolMaterials.IRON, 7, FAST, defaut(), false);
	public static final Item MAKESHIFT_AXE_TWO_HANDED = new TwoHandedSword(ToolMaterials.IRON, 7, FAST, defaut(), true);
	public static final Item MAKESHIFT_HALBERD_TWO_HANDED = new TwoHandedSword(ToolMaterials.IRON, 7, FAST, defaut(), true);
	public static final Item MAKESHIFT_SWORD_TWO_HANDED = new TwoHandedSword(ToolMaterials.IRON, 7, FAST, defaut(), false);
	public static final Item MAKESHIFT_MACE_TWO_HANDED = new TwoHandedSword(ToolMaterials.IRON, 7, FAST, defaut(), true);
	public static final Item MAKESHIFT_HAMMER_TWO_HANDED = new TwoHandedSword(ToolMaterials.IRON, 7, FAST, defaut(), true);
	public static final Item MAKESHIFT_HAMMER = new SwordItem(ToolMaterials.IRON, 10, SLOWER, defaut());
	public static final Item MAKESHIFT_BRASS_KNUCKLES = new KnuckleItem(ToolMaterials.IRON, 4, FASTER, defaut());
	public static final Item FLAME_SWORD_ONE_HANDED = new GeckoActivated(ToolMaterials.IRON, 7, NORMAL, 14, NORMAL, true, defaut());
	public static final Item FIRE_AXE = new TwoHandedSword(ToolMaterials.IRON, 13, SLOW, defaut(), true);
	public static final Item BONE_DAGGER = new SwordItem(ToolMaterials.WOOD, 4, FASTER, defaut());
	public static final Item BONE_KNIFE = new SwordItem(ToolMaterials.WOOD, 4, FASTER, defaut());
	public static final Item BONE_MACE = new SwordItem(ToolMaterials.WOOD, 10, SLOWER, defaut());
	public static final Item BONE_AXE = new SwordItem(ToolMaterials.WOOD, 8, SLOW, defaut());
	public static final Item BONE_SWORD = new SwordItem(ToolMaterials.WOOD, 8, SLOW, defaut());
	public static final Item BONE_MACHETTE = new SwordItem(ToolMaterials.WOOD, 8, SLOW, defaut());
	public static final Item BONE_BRASS_KNUCKLES = new KnuckleItem(ToolMaterials.WOOD, 8, SLOW, defaut());
	public static final Item BONE_HAMMER = new SwordItem(ToolMaterials.WOOD, 8, SLOW, defaut());
	public static final Item BONE_SPEAR = new SwordItem(ToolMaterials.WOOD, 4, FASTER, defaut());
	public static final Item BONE_SWORD_TWO_HANDED = new TwoHandedSword(ToolMaterials.WOOD, 8, SLOW, defaut(), false);
	public static final Item BONE_MACE_TWO_HANDED = new TwoHandedSword(ToolMaterials.WOOD, 9, SLOW, defaut(), true);
	public static final Item BONE_AXE_TWO_HANDED = new TwoHandedSword(ToolMaterials.WOOD, 9, SLOW, defaut(), true);
	public static final Item BONE_HALBERD_TWO_HANDED = new TwoHandedSword(ToolMaterials.WOOD, 9, SLOW, defaut(), true);
	public static final Item BONE_HAMMER_TWO_HANDED = new TwoHandedSword(ToolMaterials.IRON, 7, FAST, defaut(), true);
	public static final Item BONE_KATANA = new TwoHandedSword(ToolMaterials.IRON, 7, FAST, defaut(), false);
	public static final Item CROWBAR = new SwordItem(ToolMaterials.IRON, 2, FASTER, defaut());
	public static final Item GARDEN_TOOL_SHOVEL = new TwoHandedSword(ToolMaterials.IRON, 4, SLOW, defaut(), false);
	public static final Item GARDEN_TOOL_FORK = new SwordItem(ToolMaterials.IRON, 4, SLOW, defaut());
	public static final Item KUKRI = new SwordItem(ToolMaterials.IRON, 6, FAST, defaut());
	public static final Item COMBAT_KNIFE = new SwordItem(ToolMaterials.IRON, 4, FASTER, defaut());
	public static final Item SCREWDRIVER_FLAT = new SwordItem(ToolMaterials.IRON, 2, FASTER, defaut());
	public static final Item SCREWDRIVER_CRUCIFORM = new SwordItem(ToolMaterials.IRON, 2, FASTER, defaut());
	public static final Item CHAINSAW = new GeckoActivatedTwoHand(ToolMaterials.WOOD, 1, FAST, 20, SLOWER, false, defaut());
	public static final Item WEEDWACKER = new GeckoActivatedTwoHand(ToolMaterials.WOOD, 1, SLOW, 1, FASTEST, false, defaut());
	public static final Item MACE_TWO_HANDED_STOP = new TwoHandedSword(ToolMaterials.IRON, 6, NORMAL, defaut(), true);


	private static OwoItemSettings defaut() {
		return new OwoItemSettings().group(GROUP).tab(1);
	}

	@Override
	public void postProcessField(String namespace, Item value, String identifier, Field field) {
		LangGenerator.auto_name.put(new Identifier(namespace, "tools/" + identifier), identifier);
	}
}
