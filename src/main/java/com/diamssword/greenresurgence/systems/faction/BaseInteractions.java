package com.diamssword.greenresurgence.systems.faction;

import com.diamssword.greenresurgence.events.BaseEventCallBack;
import com.diamssword.greenresurgence.events.PlaceBlockCallback;
import com.diamssword.greenresurgence.network.AdventureInteract;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.CurrentZonePacket;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.perimeter.FactionInstance;
import com.diamssword.greenresurgence.systems.faction.perimeter.IFactionList;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BaseInteractions {

    public static List<Block> allowedBlocks =new ArrayList<>();
    public static  List<Item> allowedItems=new ArrayList<>();
    public static void registerBlocks()
    {
        BaseInteractions.allowedBlocks.clear();
        BaseInteractions.allowedItems.clear();
        allowedBlocks.add(Blocks.SAND);
        allowedBlocks.add(Blocks.OAK_PLANKS);
        allowedBlocks.add(Blocks.OAK_DOOR);
    }
    public static void register()
    {
       // ServerTickEvents.START_WORLD_TICK.register(BaseInteractions::playerTick);
        AttackBlockCallback.EVENT.register(BaseInteractions::destroyBlock);
       // UseBlockCallback.EVENT.register(BaseInteractions::placeBlock);

        BaseEventCallBack.ENTER.register(BaseInteractions::onEnter);
        BaseEventCallBack.LEAVE.register(BaseInteractions::onLeave);
        PlaceBlockCallback.EVENT.register(BaseInteractions::placeBlock1);
    }

    private static ActionResult placeBlock1(ItemPlacementContext ctx, BlockState state) {
        if(ctx.getPlayer() !=null && ctx.getPlayer() instanceof ServerPlayerEntity pl)
        {
            if(pl.interactionManager.getGameMode().equals(GameMode.SURVIVAL))
            {
                IFactionList list=ctx.getWorld().getComponent(Components.BASE_LIST);
                if(list.canEditAt(pl,ctx.getBlockPos())) {
                    if(allowedBlocks.contains(state.getBlock()))
                        return ActionResult.PASS;
                    else
                        return ActionResult.FAIL;
                }
                else
                    return ActionResult.FAIL;
            }
        }
        return ActionResult.PASS;
    }

    public static AdventureInteract.AllowedList getPacket()
    {
        var l1=allowedItems.stream().map(Registries.ITEM::getId);
        var l2=allowedBlocks.stream().map(Registries.BLOCK::getId);
        return new AdventureInteract.AllowedList(l2.toList().toArray(new Identifier[0]),l1.toList().toArray(new Identifier[0]));
    }
    public static void onEnter(ServerPlayerEntity player, FactionInstance base)
    {
        player.sendMessage(Text.literal("Vous entrez dans "+base.getName()),true);
        if(base.canEdit(player)) {
            if (player.interactionManager.getGameMode().equals(GameMode.ADVENTURE))
                player.changeGameMode(GameMode.SURVIVAL);
            Channels.MAIN.serverHandle(player).send(CurrentZonePacket.from(base));
        }
    }
    public static boolean shouldOverlayBlock(Block b)
    {
        return allowedBlocks.contains(b);
    }
    public static void onLeave(ServerPlayerEntity player, FactionInstance base)
    {
        player.sendMessage(Text.literal("Vous sortez de "+base.getName()),true);
        if(player.interactionManager.getGameMode().equals(GameMode.SURVIVAL))
            player.changeGameMode(GameMode.ADVENTURE);
    }
    public static ActionResult destroyBlock(PlayerEntity player, World w, Hand hand, BlockPos pos, Direction dir)
    {
        if(player instanceof ServerPlayerEntity pl)
        {
            if(pl.interactionManager.getGameMode().equals(GameMode.SURVIVAL))
            {

                IFactionList list=w.getComponent(Components.BASE_LIST);
                if(list.canEditAt(pl,pos)) {
                    if(allowedBlocks.contains(w.getBlockState(pos).getBlock()))
                        return ActionResult.PASS;
                }
                return ActionResult.FAIL;
            }
        }
        else if(w.isClient && !player.isCreative())
        {
            for (BlockBox box : CurrentZonePacket.currentZone) {
                if(box.contains(pos) && allowedBlocks.contains(w.getBlockState(pos).getBlock()))
                    return ActionResult.PASS;
            }
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }
    public static boolean canUseItem(PlayerEntity player,Hand hand)
    {
        var st=player.getStackInHand(hand).getItem();
        if(st instanceof BlockItem be)
        {
            return allowedBlocks.contains(be.getBlock());
        }
        else
            return allowedItems.contains(st);
    }
    public static ActionResult placeBlock(PlayerEntity player, World w, Hand hand, BlockHitResult hit)
    {
        if(player instanceof ServerPlayerEntity pl)
        {
            if(pl.interactionManager.getGameMode().equals(GameMode.SURVIVAL))
            {
                BlockPos p=hit.getBlockPos().offset(hit.getSide());
                IFactionList list=w.getComponent(Components.BASE_LIST);
                if(list.canEditAt(pl,p)) {
                    if(canUseItem(player,hand))
                        return ActionResult.PASS;
                    else
                        return ActionResult.FAIL;
                }
            }
        }
        return ActionResult.PASS;
    }
}
