package com.diamssword.greenresurgence.structure;

import com.diamssword.greenresurgence.items.IStructureProvider;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.StructureSizePacket;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.MinecraftVersion;
import net.minecraft.registry.Registries;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import java.util.HashMap;
import java.util.Map;

public class StructureInfos {
    public final static Identifier PLACER_ENTRY=new Identifier("build:placer_entry");
    private static Map<Identifier,Map<Direction,StructureInfo>> clientCache=new HashMap<>();


    public static StructureInfo getInfos(Identifier structure, Direction dir, IStructureProvider.StructureType type)
    {
        if(clientCache.containsKey(structure))
        {
            StructureInfo inf=clientCache.get(structure).get(dir);
            if(inf !=null)
                return inf;
        }
        Map<Direction, StructureInfo> m=new HashMap<>();
        Channels.MAIN.clientHandle().send(new StructureSizePacket.StructureRequest(structure, dir,type));
        m.put(dir,new StructureInfo(BlockPos.ORIGIN,new Vec3i(0,0,0)));
        clientCache.put(structure,m);
        return getInfos(structure,dir,type);
    }
    public static void setStructureInfos(StructureSizePacket.StructureResponse packet) {
        if(!clientCache.containsKey(packet.name()))
            clientCache.put(packet.name(),new HashMap<>());
        clientCache.get(packet.name()).put(packet.dir(),new StructureInfo(packet.offset(),packet.size()));
    }
    public static int[] getOffsetSide(Direction facing,boolean centered)
    {
        switch (facing)
        {
            case DOWN, NORTH, UP -> {
                return new int[]{1,centered?1:0};
            }
            case SOUTH -> {
                return new int[]{-1,centered?-1:0};
            }
            case WEST -> {
                return new int[]{centered?1:0,-1};
            }
            case EAST -> {
                return new int[]{centered?-1:0,1};
            }
        }
        return new int[]{-1,-1};
    }
    public static BlockRotation getRotation(Direction facing)
    {
        switch (facing)
        {
            case DOWN, NORTH, UP -> {
                return BlockRotation.CLOCKWISE_180;
            }
            case SOUTH -> {
                return BlockRotation.NONE;
            }
            case WEST -> {
                return BlockRotation.CLOCKWISE_90;
            }
            case EAST -> {
                return BlockRotation.COUNTERCLOCKWISE_90;
            }
        }
        return BlockRotation.NONE;
    }
    public static record StructureInfo(BlockPos offset, Vec3i size){};

}
