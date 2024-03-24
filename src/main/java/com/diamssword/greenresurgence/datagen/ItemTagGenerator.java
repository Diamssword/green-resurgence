package com.diamssword.greenresurgence.datagen;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.MItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ItemTagGenerator  extends FabricTagProvider.ItemTagProvider {

    public static final TagKey<Item> looting_tool = TagKey.of(RegistryKeys.ITEM, new Identifier(GreenResurgence.ID,"looting_tool"));
    public static final TagKey<Item> looting_wrench = TagKey.of(RegistryKeys.ITEM, new Identifier(GreenResurgence.ID,"looting_tool/type/wrench"));
    public static final TagKey<Item> looting_hammer= TagKey.of(RegistryKeys.ITEM, new Identifier(GreenResurgence.ID,"looting_tool/type/hammer"));
    public static final TagKey<Item> looting_tool_1 = TagKey.of(RegistryKeys.ITEM, new Identifier(GreenResurgence.ID,"looting_tool/level/1"));
    public static final TagKey<Item> looting_tool_2 = TagKey.of(RegistryKeys.ITEM, new Identifier(GreenResurgence.ID,"looting_tool/level/2"));

    public ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);

    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(looting_tool).add(MItems.WRENCH, MItems.WRENCH_1);
        getOrCreateTagBuilder(looting_wrench).add(MItems.WRENCH, MItems.WRENCH_1);
        getOrCreateTagBuilder(looting_hammer).add(MItems.SLEDGEHAMMER);
        getOrCreateTagBuilder(looting_tool_1).add(MItems.WRENCH);
        getOrCreateTagBuilder(looting_tool_2).add(MItems.WRENCH_1);


    }
}
