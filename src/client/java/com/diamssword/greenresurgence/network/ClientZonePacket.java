package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.blockEntities.ImageBlockEntity;
import com.diamssword.greenresurgence.gui.CharacterCustomizationScreen;
import com.diamssword.greenresurgence.gui.ImageBlockGui;
import com.diamssword.greenresurgence.gui.WardrobeGui;
import com.diamssword.greenresurgence.systems.faction.perimeter.FactionInstance;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class ClientZonePacket {
    public static void init()
    {

        Channels.MAIN.registerClientbound(CurrentZonePacket.ZoneResponse.class,(msg, ctx) -> {
            FactionInstance inst=new FactionInstance(ctx.netHandler().getWorld());
            inst.readFromNbt(msg.tag());
            CurrentZonePacket.currentZone=inst.getBoxes();
        });
    }
    public static <T extends BlockEntity> T getTile(Class<T> clazz,BlockPos pos)
    {
        BlockEntity te= MinecraftClient.getInstance().world.getBlockEntity(pos);
        if(clazz.isInstance(te))
            return (T) te;
        return null;
    }
    private static void openGui(Screen screen)
    {
        MinecraftClient.getInstance().setScreen(screen);
    }
}
