package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.crafting.CraftingProvider;
import com.diamssword.greenresurgence.systems.crafting.CraftingResult;
import com.diamssword.greenresurgence.systems.crafting.SimpleRecipe;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CraftPackets {
    private static int statusIndex=0;
    private static Consumer<CraftingResult> currentTrackedClient;
    private static Map<ServerPlayerEntity, CraftStatusTracked> currentTrackedServer=new HashMap<>();
    public record RequestCraft(BlockPos pos, SimpleRecipe recipe){};
    public record RequestPlayerCraft(SimpleRecipe recipe){};
    public record RequestCraftStatus(Integer index,BlockPos pos, SimpleRecipe recipe){};
    public record SendCraftStatus(Integer index, CraftingResult recipe){};
    private record CraftStatusTracked(Integer index, CraftingProvider provider,SimpleRecipe recipe){};
    public record AllowedList(Identifier[] blocks, Identifier[] items){};
    public static void init()
    {
        Channels.MAIN.registerClientbound(SendCraftStatus.class,(msg,ctx)->{

            if(msg.index==statusIndex && currentTrackedClient!=null)
            {
                currentTrackedClient.accept(msg.recipe);
            }
        });
        Channels.MAIN.registerServerbound(RequestCraft.class,(msg,ctx)->{
            var prov=new CraftingProvider().setForFaction(ctx.player(),msg.pos);
            var r=prov.craftRecipe(msg.recipe,ctx.player());
            if(r && currentTrackedServer.containsKey(ctx.player()))
            {
                var p1=currentTrackedServer.get(ctx.player());
                Channels.MAIN.serverHandle(ctx.player()).send(new SendCraftStatus(p1.index,prov.getRecipeStatus(msg.recipe, ctx.player())));
            }

        });
        Channels.MAIN.registerServerbound(RequestPlayerCraft.class,(msg,ctx)->{
         var pli=ctx.player().getComponent(Components.PLAYER_INVENTORY);
            pli.getCrafterProvider().craftRecipe(msg.recipe,ctx.player());
        });
        Channels.MAIN.registerServerbound(RequestCraftStatus.class,(msg,ctx)->{
            var prov=new CraftingProvider().setForFaction(ctx.player(),msg.pos);
            currentTrackedServer.put(ctx.player(),new CraftStatusTracked(msg.index,prov,msg.recipe));
            Channels.MAIN.serverHandle(ctx.player()).send(new SendCraftStatus(msg.index,prov.getRecipeStatus(msg.recipe, ctx.player())));
        });
        ServerTickEvents.START_SERVER_TICK.register(v->{
            if(v.getOverworld().getTime()%20==0)
            {
                currentTrackedServer.forEach((k,va)->{
                    Channels.MAIN.serverHandle(k).send(new SendCraftStatus(va.index,va.provider.getRecipeStatus(va.recipe,k)));
                });
            }
        });
    }
    public static void sendCraftRequest(SimpleRecipe recipe,BlockPos pos)
    {
        Channels.MAIN.clientHandle().send(new RequestCraft(pos,recipe));
    }
    public static void requestStatus(SimpleRecipe recipe,BlockPos pos, Consumer<CraftingResult> result)
    {
        statusIndex++;
        currentTrackedClient=result;
        Channels.MAIN.clientHandle().send(new RequestCraftStatus(statusIndex,pos,recipe));
    }
}
