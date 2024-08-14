package com.diamssword.greenresurgence.systems.crafting;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RecipeCollection {
    private final Map<String,SimpleRecipe> recipes=new HashMap<>();
    private final Identifier id;

    public RecipeCollection(Identifier id) {
        this.id = id;
    }

    public void add(String id,SimpleRecipe recipe)
    {
        recipes.put(id,recipe.setID(this.id,id));
    }
    public void add(SimpleRecipe recipe)
    {
        var id1="";
        var id=recipe.getId();
        if(id!=null)
        {
            id1=id.getPath().substring(id.getPath().lastIndexOf("/")+1);
        }
        else
            id1=recipes.size()+"";
        recipes.put(id1,recipe.setID(this.id,id1));
    }
    public void addAll(Map<String,SimpleRecipe> recipes)
    {
        recipes.forEach((k,v)->v.setID(this.id,k));
        this.recipes.putAll(recipes);
    }
    public List<UniversalResource> getResults(PlayerEntity player)
    {
        return recipes.values().stream().map(v->v.result(player)).toList();
    }
    public Optional<SimpleRecipe> getById(String id)
    {
        return Optional.ofNullable(recipes.get(id));
    }
    public List<SimpleRecipe> getRecipes(@Nullable PlayerEntity player)
    {
        return recipes.values().stream().toList();
    }
}
