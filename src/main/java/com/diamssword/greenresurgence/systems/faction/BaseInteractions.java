package com.diamssword.greenresurgence.systems.faction;

import com.diamssword.greenresurgence.events.BaseEventCallBack;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.CurrentZonePacket;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.perimeter.FactionInstance;
import com.diamssword.greenresurgence.systems.faction.perimeter.IFactionList;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public class BaseInteractions {


    public static void register()
    {
       // ServerTickEvents.START_WORLD_TICK.register(BaseInteractions::playerTick);
        AttackBlockCallback.EVENT.register(BaseInteractions::destroyBlock);
        UseBlockCallback.EVENT.register(BaseInteractions::placeBlock);
        BaseEventCallBack.ENTER.register(BaseInteractions::onEnter);
        BaseEventCallBack.LEAVE.register(BaseInteractions::onLeave);
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
                if(!list.canEditAt(pl,pos))
                    return ActionResult.FAIL;
            }
        }
        else if(w.isClient && !player.isCreative())
        {
            for (BlockBox box : CurrentZonePacket.currentZone) {
                if(box.contains(pos))
                    return ActionResult.PASS;
            }
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }
    public static ActionResult placeBlock(PlayerEntity player, World w, Hand hand, BlockHitResult hit)
    {
        if(player instanceof ServerPlayerEntity pl)
        {
            if(pl.interactionManager.getGameMode().equals(GameMode.SURVIVAL))
            {
                BlockPos p=hit.getBlockPos().offset(hit.getSide());

                IFactionList list=w.getComponent(Components.BASE_LIST);
                if(!list.canEditAt(pl,p))
                    return ActionResult.FAIL;
            }
        }
        return ActionResult.PASS;
    }
}
