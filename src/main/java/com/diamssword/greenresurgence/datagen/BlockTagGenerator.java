package com.diamssword.greenresurgence.datagen;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class BlockTagGenerator extends FabricTagProvider.BlockTagProvider {
    public static final TagKey<Block> lootable_block = TagKey.of(RegistryKeys.BLOCK, new Identifier(GreenResurgence.ID,"lootable_block"));
    public static final TagKey<Block> lootable_plank = TagKey.of(RegistryKeys.BLOCK, new Identifier(GreenResurgence.ID,"lootable/planks"));
    public static final TagKey<Block> looting_wrench = TagKey.of(RegistryKeys.BLOCK, new Identifier(GreenResurgence.ID,"looting_tool/type/wrench"));
    public static final TagKey<Block> looting_hammer = TagKey.of(RegistryKeys.BLOCK, new Identifier(GreenResurgence.ID,"looting_tool/type/hammer"));
    public BlockTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);

    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        GenericBlocks.sets.forEach(set->{
            set.tagGenerator(this::getOrCreateTagBuilder);
        });
        getOrCreateTagBuilder(lootable_block).add(Blocks.SAND,Blocks.STONE);
        getOrCreateTagBuilder(lootable_plank).add(Blocks.SAND,Blocks.STONE);
        getOrCreateTagBuilder(looting_wrench).add(Blocks.SAND);
        getOrCreateTagBuilder(looting_hammer).add(Blocks.STONE);

    }
}
