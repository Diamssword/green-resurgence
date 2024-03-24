package com.diamssword.greenresurgence.systems;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.datagen.BlockTagGenerator;
import com.diamssword.greenresurgence.datagen.ItemTagGenerator;
import com.diamssword.greenresurgence.datagen.LootTableGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.stream.Stream;

public class LootableLogic {
    public static void giveLoot(ServerPlayerEntity player, BlockPos pos,BlockState state)
    {
        LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder((ServerWorld) player.getWorld())
                .add(LootContextParameters.TOOL,player.getMainHandStack())
                .add(LootContextParameters.BLOCK_STATE, state)
                .add(LootContextParameters.ORIGIN, pos.toCenterPos())
                .build(LootContextTypes.BLOCK);
        ServerWorld serverWorld = lootContextParameterSet.getWorld();
        ItemStack st=lootContextParameterSet.get(LootContextParameters.TOOL);
        if(isGoodTool(st,state)) {
            findLootTag(state).forEach((v) -> {
                LootTable lootTable = serverWorld.getServer().getLootManager().getLootTable(v);
                lootTable.generateLoot(lootContextParameterSet, l -> {
                    if (!player.giveItemStack(l))
                        player.dropStack(l);
                });
            });
        }
    }
    public static List<Identifier> findLootTag(BlockState state)
    {
        if(state.isIn(BlockTagGenerator.lootable_block)) {
            Stream<TagKey<Block>> lootable = state.streamTags().filter(v -> v.id().getNamespace().equals(GreenResurgence.ID) && v.id().getPath().startsWith("lootable/"));
            Stream<Identifier> identifierStream = lootable.map((v) -> new Identifier(v.id().getNamespace(), v.id().getPath().replace("lootable/", "block_loots/")));
            return identifierStream.toList();
        }
        return List.of();
    }
    public static boolean isGoodTool(ItemStack tool,BlockState block)
    {
        List<TagKey<Item>> list = tool.streamTags().filter(v -> v.id().getNamespace().equals(GreenResurgence.ID) && v.id().getPath().startsWith("looting_tool/type")).toList();
        for(TagKey<Item> it : list)
        {
            if(block.isIn(TagKey.of(RegistryKeys.BLOCK,it.id())))
                return true;
        }
        return false;

    }
}
