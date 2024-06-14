package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import com.diamssword.greenresurgence.systems.clothing.ClothingLoader;
import com.diamssword.greenresurgence.systems.lootables.LootablesReloader;
import io.wispforest.owo.network.OwoNetChannel;
import io.wispforest.owo.network.serialization.PacketBufSerializer;
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
        PacketBufSerializer.register(MultiInvScreenHandler.Props.class, MultiInvScreenHandler.Props::serializer, MultiInvScreenHandler.Props::unserializer);
        PacketBufSerializer.register(LootablesReloader.class, LootablesReloader::serializer, LootablesReloader::unserializer);
        PacketBufSerializer.register(ClothingLoader.class, ClothingLoader::serializer, ClothingLoader::unserializer);
        AdventureInteract.init();
        StructureSizePacket.init();
        CurrentZonePacket.init();
        DictionaryPackets.init();
        GuiPackets.init();
    }
}
