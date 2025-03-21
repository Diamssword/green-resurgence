package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.MBlocks;
import com.diamssword.greenresurgence.blockEntities.LootableItemBlockEntity;
import com.diamssword.greenresurgence.blockEntities.LootableShelfEntity;
import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import com.diamssword.greenresurgence.entities.TwoPassengerVehicle;
import com.diamssword.greenresurgence.systems.faction.BaseInteractions;
import com.diamssword.greenresurgence.systems.lootables.IAdvancedLootableBlock;
import com.diamssword.greenresurgence.systems.lootables.LootableLogic;
import com.diamssword.greenresurgence.systems.lootables.Lootables;
import com.diamssword.greenresurgence.systems.lootables.LootablesReloader;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.WorldEvents;

import java.util.HashMap;
import java.util.Map;

public class AdventureInteract {
    static Map<PlayerEntity,Long> cooldowns=new HashMap<>();
    public record BlockInteract(BlockPos pos){};
    public record AllowedList(Identifier[] blocks, Identifier[] items){};
    public static void init()
    {
        Channels.MAIN.registerClientbound(AllowedList.class,(msg,ctx)->{
            BaseInteractions.allowedBlocks.clear();
            BaseInteractions.allowedItems.clear();
            for (Identifier block : msg.blocks) {
                BaseInteractions.allowedBlocks.add(Registries.BLOCK.get(block));
            }
            for (Identifier block : msg.items) {
                BaseInteractions.allowedItems.add(Registries.ITEM.get(block));
            }
        });
        Channels.MAIN.registerServerbound(BlockInteract.class,(msg,ctx)->{

            if(ctx.player().interactionManager.getGameMode().isSurvivalLike() && checkCooldown(ctx.player())) {
                ItemStack st=ctx.player().getMainHandStack();
                BlockState state=ctx.player().getWorld().getBlockState(msg.pos);
                if(state.getBlock()== MBlocks.LOOTED_BLOCK)
                {
                    LootedBlockEntity ent=MBlocks.LOOTED_BLOCK.getBlockEntity(msg.pos,ctx.player().getWorld());
                    if(st !=null && LootableLogic.isGoodTool(st,ent.getRealBlock(),0))
                    {
                        setCooldown(ctx.player());
  //                      if(LootableLogic.isDestroyInteract(st))
                            ent.attackBlock(ctx.player());
//                        else
                      //      ent.openInventory(ctx.player());
                    }
                }
                else if(state.hasBlockEntity() && ctx.player().getWorld().getBlockEntity(msg.pos) instanceof IAdvancedLootableBlock res)
                {
                    if(res.canBeInteracted())
                    {
                        setCooldown(ctx.player());
                        res.lootBlock(ctx.player());
                    }
                }
                else if(st !=null && LootableLogic.isGoodTool(st,state,0))
                {
                    ctx.player().getWorld().setBlockState(msg.pos, MBlocks.LOOTED_BLOCK.getDefaultState());
                    var te=MBlocks.LOOTED_BLOCK.getBlockEntity(msg.pos, ctx.player().getWorld());
                    te.setRealBlock(state);
                    setCooldown(ctx.player());
                  //  if( LootableLogic.isDestroyInteract(st)) {
                        te.lastBreak=System.currentTimeMillis();
                        te.markDirty();
                        LootableLogic.giveLoot(ctx.player(), msg.pos, state);
                        ctx.player().getWorld().syncWorldEvent(WorldEvents.BLOCK_BROKEN, msg.pos, Block.getRawIdFromState(state));
                        ctx.player().getWorld().playSound(null, msg.pos, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.BLOCKS, 0.5f, 1f + (float) Math.random());
                 /*   }
                    else
                    {
                        te.durability=LootedBlockEntity.MAX+1;
                        te.openInventory(ctx.player());
                    }*/
                }
            }
        });
    }
    private static boolean checkCooldown(PlayerEntity player)
    {
            if(cooldowns.containsKey(player))
            {
                return player.getWorld().getTime() > cooldowns.get(player) + 10;
            }
                return true;
    }
    private static void setCooldown(PlayerEntity player)
    {
        cooldowns.put(player,player.getWorld().getTime());
    }
}
