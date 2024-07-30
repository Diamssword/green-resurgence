package com.diamssword.greenresurgence.systems.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.Optional;

@SuppressWarnings("ALL")
public class Recipes {

    public static RecipeLoader loader=new RecipeLoader();
    public static Collection<SimpleRecipe,UniversalResource> test=new Collection<>();
    public static void init()
    {
     //   registry.put(GreenResurgence.asRessource("test"),test);
        for (int i = 0; i < 20; i++) {
            test.add(new SimpleRecipe(Items.FEATHER,new ItemStack(Items.DIAMOND,2),new ItemStack(Items.DANDELION)));
            test.add(new SimpleRecipe(Items.MINECART,new ItemStack(Items.IRON_INGOT,5)));
            test.add(new SimpleRecipe(Items.AMETHYST_CLUSTER,new ItemStack(Items.AMETHYST_SHARD,12)));
            test.add(new SimpleRecipe(Items.NETHERITE_PICKAXE,new ItemStack(Items.DIAMOND_PICKAXE,1),new ItemStack(Items.GOLD_INGOT,4),new ItemStack(Items.NETHERITE_SCRAP,4)));
        }


    }
    public static Optional<Collection<IRecipe<UniversalResource>, UniversalResource>> get(Identifier id)
    {
        return loader.getCollection(id);
    }
}
