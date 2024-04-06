package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.GreenResurgence;
import io.wispforest.owo.network.OwoNetChannel;
import io.wispforest.owo.network.serialization.PacketBufSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;

public class Channels {
    public static final OwoNetChannel MAIN = OwoNetChannel.create(new Identifier(GreenResurgence.ID, "main"));

    public static void initialize()
    {
        PacketBufSerializer.register(Vec3i.class,(write,val)->{
            write.writeInt(val.getX());
            write.writeInt(val.getY());
            write.writeInt(val.getZ());
        },(read)->{
            int x=read.readInt();
            int y=read.readInt();
            int z=read.readInt();
            return new Vec3i(x,y,z);
        });
        AdventureInteract.init();
        StructureSizePacket.init();
    }
}
