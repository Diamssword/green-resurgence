package com.diamssword.greenresurgence.systems.crafting;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleRecipe implements IRecipe<ItemResource> {
    private final ItemResource result;
    private final List<ItemResource> ingredients;
    public SimpleRecipe(ItemStack result,ItemStack... ingredients)
    {
        this.result=new ItemResource(result);
        this.ingredients= Arrays.stream(ingredients).map(ItemResource::new).toList();
    }
    public SimpleRecipe(Item result,ItemStack... ingredients)
    {
        this(new ItemStack(result),ingredients);
    }
    @Override
    public ItemResource result(PlayerEntity player) {
        return result;
    }

    @Override
    public List<ItemResource> ingredients(PlayerEntity player) {
        return ingredients;
    }
}
