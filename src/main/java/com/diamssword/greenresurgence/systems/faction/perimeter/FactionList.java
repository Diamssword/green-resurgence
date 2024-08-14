package com.diamssword.greenresurgence.systems.faction.perimeter;

import com.diamssword.greenresurgence.systems.Components;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FactionList implements IFactionList, AutoSyncedComponent {
    private final List<FactionInstance> bases=new ArrayList<>();
    private final World provider;

    public FactionList(World provider) {
        this.provider = provider;
    }

    @Override
    public List<FactionInstance> getAll() {
        return bases;
    }

    @Override
    public List<String> getNames() {
        return bases.stream().map(FactionInstance::getName).toList();
    }

    @Override
    public Optional<FactionInstance> getAt(Vec3i pos) {
            return bases.stream().filter(b->b.isIn(pos)).findFirst();

    }

    @Override
    public Optional<TerrainInstance> getTerrainAt(Vec3i pos) {
        for (FactionInstance base : bases) {
            var b=base.getSubTerrainAt(pos);
            if(b.isPresent())
                return b;
        }
        return Optional.empty();
    }

    @Override
    public boolean canEditAt(PlayerEntity player, Vec3i pos) {
        Optional<FactionInstance> base= getAt(pos);
        return base.map(baseInstance -> baseInstance.canEdit(player)).orElse(false);
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity player) {
        NbtList ls= new NbtList();
        bases.forEach(b->{
            NbtCompound t=new NbtCompound();
            b.writeNetworkNBT(t);
            ls.add(t);
        });
        NbtCompound tag = new NbtCompound();
        tag.put("bases",ls);
        buf.writeNbt(tag);
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {

        NbtCompound tag=buf.readNbt();
        NbtList ls= tag.getList("bases", NbtList.COMPOUND_TYPE);
        bases.clear();
        ls.forEach(c->{
            FactionInstance b=new FactionInstance(provider);
            b.readFromNbt((NbtCompound) c);
            bases.add(b);
        });
    }
    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player.isCreative();
    }
    @Override
    public List<FactionInstance> getNear(Vec3i pos, int distance) {
            List<FactionInstance> res=new ArrayList<>();
            bases.forEach(v->{
                   boolean match=v.getBoxes().stream().anyMatch(v1-> {
                       BlockPos p = new BlockPos(v1.getMinX(), v1.getMinY(), v1.getMinZ());
                       if (p.isWithinDistance(pos, distance)) {
                           return true;
                       } else {
                           BlockPos p1 = new BlockPos(v1.getMaxX(), v1.getMaxY(), v1.getMaxZ());
                           return p1.isWithinDistance(pos, distance);
                       }
                   });
                   if(match)
                    res.add(v);
            });
        return res;
    }

    @Override
    public Optional<FactionInstance> getClosest(Vec3i pos, int distance) {
        FactionInstance close = null;
        int curr=Integer.MAX_VALUE;
        for (FactionInstance basis : bases) {
            for (BlockBox box : basis.getBoxes()) {
                BlockPos p = new BlockPos(box.getMinX(), box.getMinY(), box.getMinZ());
                int dist=(int) MathHelper.square(pos.getSquaredDistance(p));
                if(dist<curr)
                {
                    curr=dist;
                    close=basis;
                }
            }
        }
        if(curr<distance)
            return Optional.ofNullable(close);
        return Optional.empty();
    }

    @Override
    public boolean add(FactionInstance base) {
        if(bases.stream().noneMatch(v->v.getName().equals(base.getName())))
        {
            bases.add(base);
            Components.BASE_LIST.sync(provider);
            return true;
        }
        else
            return false;
    }

    @Override
    public boolean delete(String name) {
        Optional<FactionInstance> t=this.get(name);
        if(t.isPresent())
        {
            bases.remove(t.get());
            return true;
        }
        return false;

    }

    @Override
    public Optional<FactionInstance> get(String name) {
        return bases.stream().filter(v->v.getName().equals(name)).findFirst();
    }

    @Override
    public List<Triple<String,String, BlockBox>> getBoxesForClient() {
        ArrayList<Triple<String,String, BlockBox>> res = new ArrayList<>();
        this.bases.forEach(v->{
            v.getSubTerrains().forEach((k,v1)->{
                v1.getBoxes().forEach(v2->{
                    res.add(new ImmutableTriple<>(v.getName(),k,v2));
                });
            });
        });
        return res;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {

        NbtList ls= tag.getList("bases", NbtList.COMPOUND_TYPE);
        bases.clear();
        ls.forEach(c->{
            FactionInstance b=new FactionInstance(provider);
            b.readFromNbt((NbtCompound) c);
            bases.add(b);
        });
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtList ls= new NbtList();
        bases.forEach(b->{
            NbtCompound t=new NbtCompound();
            b.writeToNbt(t);
            ls.add(t);
        });
        tag.put("bases",ls);
    }

    @Override
    public void serverTick() {
        if(provider.getTime()%40==0)
        {
            IFactionList list=provider.getComponent(Components.BASE_LIST);
            list.getAll().forEach(b->b.tick((ServerWorld) provider));
        }
    }
}
