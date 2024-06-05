package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.MBlocks;
import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import com.diamssword.greenresurgence.systems.faction.BaseInteractions;
import com.diamssword.greenresurgence.systems.lootables.LootableLogic;
import com.diamssword.greenresurgence.systems.lootables.Lootables;
import com.diamssword.greenresurgence.systems.lootables.LootablesReloader;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.WorldEvents;

import java.util.HashMap;
import java.util.Map;

public class AdventureInteract {
    static Map<PlayerEntity,Long> cooldowns=new HashMap<>();
    public record BlockInteract(BlockPos pos){};
    public record LootableList(LootablesReloader loader){};
    public static void init()
    {
        UseBlockCallback.EVENT.register(LootableLogic::onRightClick);
        Channels.MAIN.registerClientbound(LootableList.class,(msg,ctx)->{
            Lootables.loader=msg.loader;
        });
        Channels.MAIN.registerServerbound(BlockInteract.class,(msg,ctx)->{

            if(ctx.player().interactionManager.getGameMode().isSurvivalLike() && checkCooldown(ctx.player())) {
                ItemStack st=ctx.player().getMainHandStack();
                BlockState state=ctx.player().getWorld().getBlockState(msg.pos);
                if(state.getBlock()== MBlocks.LOOTED_BLOCK)
                {
                    LootedBlockEntity ent=MBlocks.LOOTED_BLOCK.getBlockEntity(msg.pos,ctx.player().getWorld());
                    if(st !=null && LootableLogic.isGoodTool(st,ent.getRealBlock()))
                    {
                        setCooldown(ctx.player());
                        if(LootableLogic.isDestroyInteract(st))
                            ent.attackBlock(ctx.player());
                        else
                            ent.openInventory(ctx.player());
                    }
                }
                else if(st !=null && LootableLogic.isGoodTool(st,state))
                {
                    ctx.player().getWorld().setBlockState(msg.pos, MBlocks.LOOTED_BLOCK.getDefaultState());
                    var te=MBlocks.LOOTED_BLOCK.getBlockEntity(msg.pos, ctx.player().getWorld());
                    te.setRealBlock(state);
                    setCooldown(ctx.player());
                    if( LootableLogic.isDestroyInteract(st)) {
                        te.lastBreak=System.currentTimeMillis();
                        te.markDirty();
                        LootableLogic.giveLoot(ctx.player(), msg.pos, state);
                        ctx.player().getWorld().syncWorldEvent(WorldEvents.BLOCK_BROKEN, msg.pos, Block.getRawIdFromState(state));
                        ctx.player().getWorld().playSound(null, msg.pos, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.BLOCKS, 0.5f, 1f + (float) Math.random());
                    }
                    else
                    {
                        te.durability=LootedBlockEntity.MAX+1;
                        te.openInventory(ctx.player());
                    }
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
