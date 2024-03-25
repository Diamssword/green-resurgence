package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.MBlocks;
import com.diamssword.greenresurgence.systems.CableNetwork;
import com.diamssword.greenresurgence.systems.LootableLogic;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConnectorBlockEntity extends BlockEntity {
    public List<BlockPos> connections = new ArrayList<>();
    public ConnectorBlockEntity(BlockPos pos, BlockState state) {
        super(MBlocks.CONNECTOR_BE, pos, state);
    }

    public void addConnection(BlockPos from)
    {
        this.connections.add(from);
        this.markDirty();
        this.world.updateListeners(this.pos,this.getCachedState(),this.getCachedState(), Block.NOTIFY_ALL);
    }
    public void clearConnection(BlockPos from)
    {
        this.connections.remove(from);
        this.markDirty();
        this.world.updateListeners(this.pos,this.getCachedState(),this.getCachedState(), Block.NOTIFY_ALL);
    }
    public void clearConnections()
    {
        this.connections.clear();
        this.markDirty();
        this.world.updateListeners(this.pos,this.getCachedState(),this.getCachedState(), Block.NOTIFY_ALL);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putLongArray("connections", this.connections.stream().map(BlockPos::asLong).toList());

        super.writeNbt(nbt);
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.connections= new ArrayList<>( Arrays.stream(nbt.getLongArray("connections")).mapToObj(BlockPos::fromLong).toList());
        if(world != null )
        {
            if(world.isClient)
                loadClientCables();
            else
               this.world.updateListeners(this.pos,this.getCachedState(),this.getCachedState(), Block.NOTIFY_ALL);
        }

    }
    public void loadClientCables()
    {
        this.connections.forEach(c->{

            if(CableNetwork.clientCables.stream().noneMatch(p->(p.getLeft().equals(this.pos) && p.getRight().equals(c))||(p.getLeft().equals(c) && p.getRight().equals(this.pos))))
                CableNetwork.clientCables.add(new Pair<>(this.pos,c));
        });
    }
    public void unloadClientCables()
    {
        this.connections.forEach(c->{
            CableNetwork.clientCables.removeAll(CableNetwork.clientCables.stream().filter(p->(p.getLeft().equals(this.pos)  || p.getRight().equals(this.pos))).toList());

        });
    }
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
