package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.systems.faction.perimeter.FactionInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.List;

public class CurrentZonePacket {
    public static List<BlockBox> currentZone=new ArrayList<>();
    public record ZoneResponse(NbtCompound tag){};
    public static ZoneResponse from(FactionInstance base)
    {
        NbtCompound tag=new NbtCompound();
            base.writeNetworkNBT(tag);
            return new ZoneResponse(tag);
    }
    public static void init() {
        Channels.MAIN.registerClientbound(ZoneResponse.class, (msg, ctx) -> {
            FactionInstance inst=new FactionInstance();
            inst.readFromNbt(msg.tag);
            currentZone=inst.getBoxes();

        });
    }
}
