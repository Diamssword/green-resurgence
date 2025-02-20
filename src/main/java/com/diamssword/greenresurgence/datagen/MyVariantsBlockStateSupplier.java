package com.diamssword.greenresurgence.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.client.VariantsBlockStateSupplier;

public record MyVariantsBlockStateSupplier(BlockStateSupplier parent, int count) implements BlockStateSupplier {

    public JsonElement getVariant()
    {
        var base = parent.get();
        var variants = base.getAsJsonObject().get("variants").getAsJsonObject();
        variants.keySet().forEach(key -> {
            var mb = variants.get(key).getAsJsonObject();
            if (mb.has("model")) {
                var m = mb.get("model").getAsString();
                var arr = new JsonArray();
                for (int i = 0; i < count; i++) {
                    var mb1 = mb.deepCopy();
                    mb1.addProperty("model", m +"/"+ i);
                    arr.add(mb1);
                }
                variants.add(key, arr);
            }
        });
        return base;
    }
    public JsonElement getMultipart()
    {
        var base = parent.get();
        var variants = base.getAsJsonObject().get("multipart").getAsJsonArray();
        variants.forEach(ob -> {
            var mb=ob.getAsJsonObject().get("apply").getAsJsonObject();
            if (mb.has("model")) {
                var m = mb.get("model").getAsString();
                var arr = new JsonArray();
                for (int i = 0; i < count; i++) {
                    var mb1 = mb.deepCopy();
                    mb1.addProperty("model", m +"/"+ i);
                    arr.add(mb1);
                }

                ob.getAsJsonObject().add("apply", arr);
            }
        });
        return base;
    }
    public JsonElement get() {
        if(parent instanceof  VariantsBlockStateSupplier)
            return getVariant();
        return getMultipart();
    }

    public Block getBlock() {
        return parent.getBlock();
    }
}
