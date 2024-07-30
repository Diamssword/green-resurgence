package com.diamssword.greenresurgence.systems.crafting;

import com.diamssword.greenresurgence.blocks.ItemBlock;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SimpleRecipe implements IRecipe<UniversalResource> {
    private final UniversalResource result;
    private final List<UniversalResource> ingredients;
    public SimpleRecipe(ItemStack result,ItemStack... ingredients)
    {
        this.result=UniversalResource.fromItem(result);
        this.ingredients= Arrays.stream(ingredients).map(UniversalResource::fromItem).toList();
    }
    public SimpleRecipe(Identifier result)
    {
        this.result=UniversalResource.fromItem(result);
        this.ingredients=new ArrayList<>();
    }
    public SimpleRecipe(Item result,ItemStack... ingredients)
    {
        this(new ItemStack(result),ingredients);
    }

    public SimpleRecipe(UniversalResource res, List<UniversalResource> ingredients) {
        this.result=res;
        this.ingredients=ingredients;
    }

    @Override
    public UniversalResource result(@Nullable PlayerEntity player) {
        return result;
    }
    public Optional<Block> blocksResult()
    {
        if(this.result.getType()== UniversalResource.Type.item)
        {
            if(this.result.asItem().getItem() instanceof BlockItem be) {
                return Optional.of(be.getBlock());
            }
        }
        return Optional.empty();

    }
    @Override
    public List<UniversalResource> ingredients(PlayerEntity player) {
        return ingredients;
    }

    public static SimpleRecipe deserializer(JsonObject ob) throws Exception
    {
        if (ob.has("ingredients") && ob.has("result")) {
            var ingsR=new ArrayList<UniversalResource>();
            for (JsonElement ing : ob.get("ingredients").getAsJsonArray()) {
                ingsR.add(UniversalResource.deserializer(ing.getAsJsonObject()));
            }
            var res=UniversalResource.deserializer(ob.getAsJsonObject("result"));
            return new SimpleRecipe(res,ingsR);

        }
        else
            throw new Exception("missing 'ingredients' or 'result' element");

    }
    public JsonObject serialize()
    {
        var res=new JsonObject();
        var ia=new JsonArray();
        this.ingredients.forEach(v->ia.add(v.serializer()));
        res.add("ingredients",ia);
        res.add("result",this.result.serializer());
        return res;

    }
    public static List<SimpleRecipe> deserializerMulti(JsonObject ob) throws Exception
    {
        var res=new ArrayList<SimpleRecipe>();
        if (ob.has("ingredients") && ob.has("results")) {
            var ingsR=new ArrayList<UniversalResource>();
            for (JsonElement ing : ob.get("ingredients").getAsJsonArray()) {
                ingsR.add(UniversalResource.deserializer(ing.getAsJsonObject()));
            }
            for (JsonElement ing : ob.get("results").getAsJsonArray()) {
                res.add(new SimpleRecipe(UniversalResource.deserializer(ing.getAsJsonObject()),ingsR));
            }
        }
        else
            throw new Exception("missing 'ingredients' or 'result' element");
        return res;
    }
}
