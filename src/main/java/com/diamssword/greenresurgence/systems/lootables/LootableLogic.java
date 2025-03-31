package com.diamssword.greenresurgence.systems.lootables;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.MBlocks;
import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class LootableLogic {
    public static ActionResult onRightClick(PlayerEntity player, World w, Hand hand, BlockHitResult hit) {
        if (player instanceof ServerPlayerEntity pl) {
            if (pl.interactionManager.getGameMode().isSurvivalLike() && player.getMainHandStack().isEmpty()) {
                BlockPos p = hit.getBlockPos();
                BlockState state = player.getWorld().getBlockState(p);
                if(state.getBlock()== MBlocks.LOOTED_BLOCK)
                {
                    LootedBlockEntity ent=MBlocks.LOOTED_BLOCK.getBlockEntity(p,player.getWorld());

                    if(Lootables.CONTAINER.id().equals(getGoodTool(ItemStack.EMPTY,ent.getRealBlock(),1)))
                    {
                        ent.openInventory((ServerPlayerEntity) player);
                        return ActionResult.SUCCESS;
                    }
                }
                else if(Lootables.CONTAINER.id().equals(getGoodTool(ItemStack.EMPTY,state,1))) {
                   player.getWorld().setBlockState(p, MBlocks.LOOTED_BLOCK.getDefaultState());
                    var te=MBlocks.LOOTED_BLOCK.getBlockEntity(p, player.getWorld());
                    te.setRealBlock(state);
                    te.durability=LootedBlockEntity.MAX+1;
                    te.openInventory((ServerPlayerEntity) player);
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.PASS;
    }
    public static void giveLoot(ServerPlayerEntity player, BlockPos pos,BlockState state)
    {
        LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder((ServerWorld) player.getWorld())
                .add(LootContextParameters.TOOL,player.getMainHandStack())
                .add(LootContextParameters.BLOCK_STATE, state)
                .add(LootContextParameters.ORIGIN, pos.toCenterPos())
                .build(LootContextTypes.BLOCK);
        ServerWorld serverWorld = lootContextParameterSet.getWorld();
        ItemStack st=lootContextParameterSet.get(LootContextParameters.TOOL);
        var tool=getGoodTool(st,state,2);
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
    private static final Random rand=new Random();
    public static void createLootInventory(ServerPlayerEntity player, BlockPos pos, BlockState state, Inventory inv)
    {
        LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder((ServerWorld) player.getWorld())
                .add(LootContextParameters.TOOL,player.getMainHandStack())
                .add(LootContextParameters.BLOCK_STATE, state)
                .add(LootContextParameters.ORIGIN, pos.toCenterPos())
                .build(LootContextTypes.BLOCK);
        ServerWorld serverWorld = lootContextParameterSet.getWorld();
            var loot=findLootTag(state,Lootables.CONTAINER.id());
            if(loot!=null) {
                LootTable lootTable = serverWorld.getServer().getLootManager().getLootTable(loot);
                lootTable.supplyInventory(inv,lootContextParameterSet,rand.nextLong());
                inv.markDirty();
            }
    }
    private static Identifier findLootTag(BlockState state, Identifier tool)
    {
        return Lootables.getTableForBlock(state.getBlock(),tool);

    }
    public static boolean isDestroyInteract(Identifier tool)
    {
        return !tool.equals( Lootables.CONTAINER.id());
    }

    public static boolean isGoodTool(ItemStack tool,BlockState block,int mode)
    {
        return getGoodTool(tool,block,mode)!=null;
    }

    /**
     *
     * @param tool tool itemstack
     * @param block block to test
     * @param mode 0= left click only 1= right click only 2= both
     * @return the tool ID or null;
     */
    private static Identifier getGoodTool(ItemStack tool,BlockState block,int mode)
    {
        if(tool.isEmpty() && block!=null)
        {
            if(Lootables.isGoodTool(block.getBlock(), Lootables.CONTAINER.id()))
                return (mode==1||mode==2)?Lootables.CONTAINER.id():null;
            else if( Lootables.isGoodTool(block.getBlock(), Lootables.HAND.id()))
                return (mode==0||mode==2)?Lootables.HAND.id():null;
            else
                return null;
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
