package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.blockEntities.ConnectorBlockEntity;
import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import com.diamssword.greenresurgence.network.AdventureInteract;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.render.BoxRenderers;
import com.diamssword.greenresurgence.render.CableRenderer;
import com.diamssword.greenresurgence.systems.LootableLogic;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.GameMode;

import static com.diamssword.greenresurgence.render.BoxRenderers.drawStructureItemOverlay;
import static com.diamssword.greenresurgence.render.BoxRenderers.drawStructureOverlay;

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
            if(playerListEntry!=null && playerListEntry.getGameMode()== GameMode.ADVENTURE)
            {
                PlayerEntity player=MinecraftClient.getInstance().player;
                ItemStack st=player.getMainHandStack();
                BlockState state=player.getWorld().getBlockState(pos);
                if(state.getBlock()== MBlocks.LOOTED_BLOCK)
                {
                    LootedBlockEntity ent=MBlocks.LOOTED_BLOCK.getBlockEntity(pos,player.getWorld());
                    if(st !=null && LootableLogic.isGoodTool(st,ent.getRealBlock()))
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
            CableRenderer.render(ctx);
        });
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((ctx, hit)->{
            if(hit.getType()== HitResult.Type.BLOCK)
            {
                if(hit instanceof BlockHitResult) {
                    if(playerListEntry ==null)
                        playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(MinecraftClient.getInstance().player.getUuid());

                    if(playerListEntry!=null && playerListEntry.getGameMode()== GameMode.ADVENTURE)
                    {
                        ItemStack st=MinecraftClient.getInstance().player.getMainHandStack();

                        BlockState state=ctx.world().getBlockState(((BlockHitResult) hit).getBlockPos());
                        if(state.getBlock()== MBlocks.LOOTED_BLOCK)
                        {
                            LootedBlockEntity ent=MBlocks.LOOTED_BLOCK.getBlockEntity(((BlockHitResult) hit).getBlockPos(),ctx.world());
                            if(st !=null && LootableLogic.isGoodTool(st,ent.getRealBlock()))
                                BoxRenderers.drawAdventureOutline(((BlockHitResult) hit).getBlockPos(), ctx);
                        }
                        else if(st !=null && LootableLogic.isGoodTool(st,ctx.world().getBlockState(((BlockHitResult) hit).getBlockPos())))
                           BoxRenderers.drawAdventureOutline(((BlockHitResult) hit).getBlockPos(), ctx);
                    }

                }
            }
            return true;
        });
    }

}
