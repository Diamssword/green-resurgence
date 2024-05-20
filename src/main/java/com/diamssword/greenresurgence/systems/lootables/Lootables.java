package com.diamssword.greenresurgence.systems.lootables;

import com.diamssword.greenresurgence.GreenResurgence;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.time.temporal.Temporal;

public class Lootables {
    /*
    Le temps de refresh d'un block en millisecondes (7J ici)
     */
    public static final long refreshPhase =30000; //604_800_000;
    public static final TagKey<Item> WRENCH= createTool("wrench");
    public static final TagKey<Item> HAMMER=createTool("hammer");
    public static final TagKey<Item> HAND=createTool("hand");
    private static TagKey<Item> createTool(String name)
    {
        return TagKey.of(RegistryKeys.ITEM,GreenResurgence.asRessource("lootable/tools/"+name));
    }

    public static  void init()
    {
        LootableGroup.create().add(GreenResurgence.asRessource("teien_smoker"),GreenResurgence.asRessource("last_days_furnace"),GreenResurgence.asRessource("last_days_smoker")).addTool(WRENCH,Tables.FURNACE).addTool(HAND,Tables.FURNACE_INV);
        LootableGroup.create().addRep(new Identifier("conquest:rustic_spruce_wood_planks"), new Identifier("conquest:glass_paned_cabinets"),new Identifier("conquest:fancy_oak_wood_cabinets"),new Identifier("conquest:cupboards"),new Identifier("conquest:poor_wardrobe"),new Identifier("conquest:fancy_wardrobe")).addTool(HAMMER,Tables.FURNITURE);

    }
    public static boolean isGoodTool(Block b, Identifier tool) {
        for (LootableGroup group : LootableGroup.getGroups()) {
            if (group.asBlock(b) && group.asTool(tool)) {
                return true;
            }
        }
        return false;
    }
    public static Identifier getTableForBlock(Block b, Identifier tool) {
        for (LootableGroup group : LootableGroup.getGroups()) {
            if (group.asBlock(b) && group.asTool(tool)) {
                return group.getLootForTool(tool);
            }
        }
        return null;
    }
    public static Block getEmptyBlock(Block b) {
        for (LootableGroup group : LootableGroup.getGroups()) {
            if(group.asBlock(b))
                return group.getEmptyBlock(b);
        }
        return Blocks.AIR;
    }
}

