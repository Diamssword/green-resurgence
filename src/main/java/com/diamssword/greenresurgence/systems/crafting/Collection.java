package com.diamssword.greenresurgence.systems.crafting;

import net.minecraft.entity.player.PlayerEntity;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.List;

public class Collection<T extends IRecipe<A>,A extends IResource> {
    private final List<T> recipes=new ArrayList<>();
    public void add(T recipe)
    {
        recipes.add(recipe);
    }
    public void addAll(List<? extends  T> recipes)
    {
        this.recipes.addAll(recipes);
    }
    public List<A> getResults(PlayerEntity player)
    {
        return recipes.stream().map(v->v.result(player)).toList();
    }
    public List<T> getRecipes(PlayerEntity player)
    {
        return recipes;
    }
}
