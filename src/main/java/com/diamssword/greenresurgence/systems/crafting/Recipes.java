package com.diamssword.greenresurgence.systems.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.Optional;

@SuppressWarnings("ALL")
public class Recipes {

    public static RecipeLoader loader=new RecipeLoader();

    public static Optional<RecipeCollection> get(Identifier id)
    {
        return loader.getCollection(id);
    }
    public static Optional<SimpleRecipe> getRecipe(Identifier id)
    {

        var id1=new Identifier(id.getNamespace(),id.getPath().substring(0,id.getPath().lastIndexOf("/")));
        var i=get(id1);
        if(i.isPresent()){
            return i.get().getById(id.getPath().substring(id.getPath().lastIndexOf("/")+1));
        }
        return Optional.empty();
    }
}
