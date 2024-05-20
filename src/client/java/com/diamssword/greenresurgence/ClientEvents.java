package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.blockEntities.ConnectorBlockEntity;
import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import com.diamssword.greenresurgence.network.AdventureInteract;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.CurrentZonePacket;
import com.diamssword.greenresurgence.render.BoxRenderers;
import com.diamssword.greenresurgence.render.CableRenderer;
import com.diamssword.greenresurgence.systems.lootables.LootableLogic;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import static com.diamssword.greenresurgence.render.BoxRenderers.*;

public class ClientEvents {
    static PlayerListEntry playerListEntry;
    static long cooldown= 0;

    public static void initialize()
    {
        AttackBlockCallback.EVENT.register((pos,dir)->{
            if(System.currentTimeMillis()<cooldown+600)
            {
                return ActionResult.FAIL;
            }
            if(playerListEntry!=null && playerListEntry.getGameMode().isSurvivalLike())
            {
                PlayerEntity player=MinecraftClient.getInstance().player;
                ItemStack st=player.getMainHandStack();
                BlockState state=player.getWorld().getBlockState(pos);
                if(state.getBlock()== MBlocks.LOOTED_BLOCK)
                {
                    LootedBlockEntity ent=MBlocks.LOOTED_BLOCK.getBlockEntity(pos,player.getWorld());
                    if(st !=null && LootableLogic.isGoodTool(st,ent.getDisplayBlock()))
                    {
                        Channels.MAIN.clientHandle().send(new AdventureInteract.BlockInteract(pos));
                        cooldown=System.currentTimeMillis();
                        player.swingHand(player.preferredHand);
                        return ActionResult.SUCCESS;
                    }
                }
                else if(st !=null && LootableLogic.isGoodTool(st,MinecraftClient.getInstance().world.getBlockState(pos)))
                {
                    cooldown=System.currentTimeMillis();
                    Channels.MAIN.clientHandle().send(new AdventureInteract.BlockInteract(pos));
                    return ActionResult.SUCCESS;
                }
            }

            return ActionResult.PASS;
        });
        ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((te,w)->{
            if(te instanceof ConnectorBlockEntity)
            {
                ((ConnectorBlockEntity) te).unloadClientCables();
            }
        });
        WorldRenderEvents.LAST.register((ctx)->{
            drawStructureItemOverlay(ctx.matrixStack());
            drawBaseOverlays(ctx.matrixStack());
            CableRenderer.render(ctx);
        });
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((ctx, hit)->{
            if(hit.getType()== HitResult.Type.BLOCK)
            {
                if(hit instanceof BlockHitResult hitB) {
                    if(playerListEntry ==null)
                        playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(MinecraftClient.getInstance().player.getUuid());

                    if(playerListEntry!=null && playerListEntry.getGameMode().isSurvivalLike())
                    {
                        ItemStack st=MinecraftClient.getInstance().player.getMainHandStack();

                        BlockState state=ctx.world().getBlockState((hitB).getBlockPos());
                        if(state.getBlock()== MBlocks.LOOTED_BLOCK)
                        {
                            LootedBlockEntity ent=MBlocks.LOOTED_BLOCK.getBlockEntity((hitB).getBlockPos(),ctx.world());
                            if(st !=null && LootableLogic.isGoodTool(st,ent.getDisplayBlock())) {
                                BoxRenderers.drawAdventureOutline((hitB).getBlockPos(), ctx);
                                return false;
                            }

                        }
                        else if(st !=null && LootableLogic.isGoodTool(st,ctx.world().getBlockState((hitB).getBlockPos()))) {
                            BoxRenderers.drawAdventureOutline((hitB).getBlockPos(), ctx);
                            return false;
                        }
                        for (BlockBox box : CurrentZonePacket.currentZone) {
                            if(box.contains(hitB.getBlockPos()))
                                return true;
                        }
                        return false;
                    }

                }
            }
            return true;
        });
        UseBlockCallback.EVENT.register(ClientEvents::placeBlock);
    }
    public static ActionResult placeBlock(PlayerEntity player, World w, Hand hand, BlockHitResult hit)
    {
        GameMode mode=MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(player.getUuid()).getGameMode();
        if(mode==GameMode.CREATIVE)
            return ActionResult.PASS;
        BlockPos p=hit.getBlockPos().offset(hit.getSide());
        for (BlockBox box : CurrentZonePacket.currentZone) {
            if(box.contains(p))
                return ActionResult.PASS;
            if((!(player.getMainHandStack().getItem() instanceof BlockItem)) ||mode ==GameMode.ADVENTURE)
            {
                return ActionResult.PASS;
            }

        }
        if((!(player.getMainHandStack().getItem() instanceof BlockItem)) ||mode ==GameMode.ADVENTURE)
        {
            return ActionResult.PASS;
        }
        return ActionResult.FAIL;
    }
}
