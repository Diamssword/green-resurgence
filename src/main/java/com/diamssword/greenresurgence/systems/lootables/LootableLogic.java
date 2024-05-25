package com.diamssword.greenresurgence.systems.lootables;

import com.diamssword.greenresurgence.GreenResurgence;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Random;

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
        var tool=getGoodTool(st,state);
        if(tool!=null ) {
            var loot=findLootTag(state,tool);
            if(loot!=null) {
                LootTable lootTable = serverWorld.getServer().getLootManager().getLootTable(loot);
                lootTable.generateLoot(lootContextParameterSet, l -> {
                    if (!player.giveItemStack(l))
                        player.dropStack(l);
                });
            }
        }
    }
    private static Random rand=new Random();
    public static void createLootInventory(ServerPlayerEntity player, BlockPos pos, BlockState state, Inventory inv)
    {
        LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder((ServerWorld) player.getWorld())
                .add(LootContextParameters.TOOL,player.getMainHandStack())
                .add(LootContextParameters.BLOCK_STATE, state)
                .add(LootContextParameters.ORIGIN, pos.toCenterPos())
                .build(LootContextTypes.BLOCK);
        ServerWorld serverWorld = lootContextParameterSet.getWorld();
            var loot=findLootTag(state,Lootables.HAND.id());
            if(loot!=null) {
                LootTable lootTable = serverWorld.getServer().getLootManager().getLootTable(loot);
                lootTable.supplyInventory(inv,lootContextParameterSet,rand.nextLong());
                inv.markDirty();
            }
    }
    public static Identifier findLootTag(BlockState state, Identifier tool)
    {
        return Lootables.getTableForBlock(state.getBlock(),tool);

    }
    public static boolean isDestroyInteract(Identifier tool)
    {
        return !tool.equals( Lootables.HAND.id());
    }
    public static boolean isDestroyInteract(ItemStack tool)
    {
        return !tool.isEmpty();
    }
    public static boolean isGoodTool(ItemStack tool,BlockState block)
    {
        return getGoodTool(tool,block)!=null;
    }
    public static Identifier getGoodTool(ItemStack tool,BlockState block)
    {
        if(tool.isEmpty() && block!=null)
        {
            if(Lootables.isGoodTool(block.getBlock(), Lootables.HAND.id()))
                return Lootables.HAND.id();
        }
        List<TagKey<Item>> list = tool.streamTags().filter(v -> v.id().getNamespace().equals(GreenResurgence.ID) && v.id().getPath().startsWith("lootable/tools")).toList();
        for(TagKey<Item> it : list)
        {
           if(Lootables.isGoodTool(block.getBlock(), it.id()))
               return it.id();
        }
        return null;
    }
}
