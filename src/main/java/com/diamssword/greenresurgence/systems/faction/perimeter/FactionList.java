package com.diamssword.greenresurgence.systems.faction.perimeter;

import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionGuild;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionMember;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionZone;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.Perms;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
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

public class FactionList implements AutoSyncedComponent, ServerTickingComponent {
    private final List<FactionGuild> guilds=new ArrayList<>();
    private final World provider;

    public FactionList(World provider) {
        this.provider = provider;
    }

    public List<FactionGuild> getAll() {
        return guilds;
    }

    public List<String> getNames() {
        return guilds.stream().map(FactionGuild::getName).toList();
    }

    public Optional<FactionGuild> getAt(Vec3i pos) {
            return guilds.stream().filter(b->b.isIn(pos)).findFirst();

    }
    public Optional<FactionZone> getTerrainAt(Vec3i pos) {
        for (FactionGuild base : guilds) {
            var b=base.getTerrainAt(pos);
            if(b.isPresent())
                return b;
        }
        return Optional.empty();
    }
    public boolean isAllowedAt(Vec3i pos,FactionMember member, Perms perm)
    {
        Optional<FactionGuild> base= getAt(pos);
        return base.map(baseInstance -> baseInstance.isAllowed(member,perm)).orElse(false);
    }
  /*  @Override
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
/*
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
*/
    public Optional<FactionGuild> get(String name) {
        return guilds.stream().filter(v->v.getName().equals(name)).findFirst();
    }

    /*
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
*/

    @Override
    public void readFromNbt(NbtCompound tag) {

    /*    NbtList ls= tag.getList("bases", NbtList.COMPOUND_TYPE);
        bases.clear();
        ls.forEach(c->{
            FactionInstance b=new FactionInstance(provider);
            b.readFromNbt((NbtCompound) c);
            bases.add(b);
        });
        */
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtList ls= new NbtList();
     /*   bases.forEach(b->{
            NbtCompound t=new NbtCompound();
            b.writeToNbt(t);
            ls.add(t);
        });
        tag.put("bases",ls);

      */
    }

    @Override
    public void serverTick() {
        if(provider.getTime()%40==0)
        {

            guilds.forEach(b->b.tick((ServerWorld) provider));
        }
    }
}
