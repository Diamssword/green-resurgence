package com.diamssword.greenresurgence.systems.crafting;

import com.diamssword.greenresurgence.GreenResurgence;
import com.mojang.serialization.Lifecycle;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ALL")
public class Recipes {
    private static final Map<Identifier,Collection<?,?>> registry=new HashMap<>();
    public static Collection<SimpleRecipe,ItemResource> test=new Collection<>();
    public static void init()
    {
        registry.put(GreenResurgence.asRessource("test"),test);
        for (int i = 0; i < 20; i++) {
            test.add(new SimpleRecipe(Items.FEATHER,new ItemStack(Items.DIAMOND,2),new ItemStack(Items.DANDELION)));
            test.add(new SimpleRecipe(Items.MINECART,new ItemStack(Items.IRON_INGOT,5)));
            test.add(new SimpleRecipe(Items.AMETHYST_CLUSTER,new ItemStack(Items.AMETHYST_SHARD,12)));
            test.add(new SimpleRecipe(Items.NETHERITE_PICKAXE,new ItemStack(Items.DIAMOND_PICKAXE,1),new ItemStack(Items.GOLD_INGOT,4),new ItemStack(Items.NETHERITE_SCRAP,4)));
        }


    }
    public static Collection<IRecipe<IResource>,IResource> get(Identifier id)
    {
        return (Collection<IRecipe<IResource>, IResource>) registry.get(id);
    }
}
