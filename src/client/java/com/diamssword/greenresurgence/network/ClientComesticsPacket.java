package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.blockEntities.ImageBlockEntity;
import com.diamssword.greenresurgence.cosmetics.SkinsLoader;
import com.diamssword.greenresurgence.gui.CharacterCustomizationScreen;
import com.diamssword.greenresurgence.gui.ImageBlockGui;
import com.diamssword.greenresurgence.gui.WardrobeGui;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockPos;

public class ClientComesticsPacket {
    public static void init()
    {

        Channels.MAIN.registerClientbound(CosmeticsPackets.RefreshSkin.class,(message, access) -> {
            SkinsLoader.instance.markReload(message.player(),true);
        });
        Channels.MAIN.registerClientbound(PosesPackets.LiftOtherPlayer.class,(msg,ctx)->{
            var player=ctx.runtime().world.getPlayerByUuid(msg.player());
            if(msg.carried())
                ctx.player().startRiding(player);
            else
                player.startRiding(ctx.player(),true);
        });
        Channels.MAIN.registerClientbound(PosesPackets.DismountedPlayerNotify.class,(msg,ctx)->{
            var player=ctx.runtime().world.getPlayerByUuid(msg.player());
            player.dismountVehicle();
        });
    }

}
