package com.diamssword.greenresurgence.systems.faction.perimeter;

import com.diamssword.greenresurgence.events.BaseEventCallBack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.*;

public class FactionInstance {

    private final Map<String,TerrainInstance> boxes=new HashMap<>();

    private String name;
    private List<ServerPlayerEntity> inBase=new ArrayList<>();
    private List<UUID> members=new ArrayList<>();
    protected final World world;
    public FactionInstance(World world)
    {
        this.world = world;
    }

    public FactionInstance(World world,String name,String subname, BlockBox box)
    {
        this.world = world;
        this.name=name;
        this.boxes.put(subname,new TerrainInstance(box,this));

    }
    public Optional<TerrainInstance> getSubTerrainAt(Vec3i pos)
    {
        for (TerrainInstance value : boxes.values()) {
            if(value.isIn(pos))
                return Optional.of(value);
        }
        return Optional.empty();
    }
    public boolean removeAreaAt(BlockPos pos)
    {
        for (TerrainInstance value : boxes.values()) {
            if(value.removeAreaAt(pos))
                return true;
        }
        return false;
    }
    public void tick(ServerWorld world)
    {
        world.getPlayers().forEach(p->{
            if(inBase.contains(p))
            {
                if(!this.isIn(p.getBlockPos()))
                {
                    inBase.remove(p);
                    BaseEventCallBack.LEAVE.invoker().enterOrLeave(p,this);
                }

            }
            else
            {
                if(this.isIn(p.getBlockPos()))
                {
                    inBase.add(p);
                    BaseEventCallBack.ENTER.invoker().enterOrLeave(p,this);
                }
            }
        });
        inBase=new ArrayList<>(inBase.stream().filter(v->!v.isDead()).toList());
    }
    public List<BlockBox> getBoxes()
    {
        var ar= new ArrayList<BlockBox>();
        boxes.values().forEach(v->ar.addAll(v.getBoxes()));
        return ar;
    }
    public Map<String,TerrainInstance> getSubTerrains()
    {
        return new HashMap<>(this.boxes);
    }
    public String getName()
    {
        return name;
    }
    public void addArea(String subname,BlockBox b)
    {
        if(boxes.containsKey(subname))
           boxes.get(subname).addArea(b);
        else
            boxes.put(subname,new TerrainInstance(b,this));
    }
    public boolean isIn(Vec3i pos)
    {
        for(TerrainInstance b : boxes.values())
        {
            if(b.isIn(pos))
                return true;
        }
        return false;
    }

    public boolean canEdit(PlayerEntity player)
    {
        return members.contains(player.getUuid());
    }
    public void addMember(PlayerEntity player)
    {
        if(!members.contains(player.getUuid()))
            members.add(player.getUuid());
    }
    public void removeMember(PlayerEntity player)
    {
            members.remove(player.getUuid());
    }
    public void readFromNbt(NbtCompound tag) {
        boxes.clear();
        NbtList ls= tag.getList("terrains", NbtList.COMPOUND_TYPE);
        ls.forEach(c->{
            if(c instanceof NbtCompound c1)
            {
                var n=c1.getString("name");
                var t=new TerrainInstance(this);
                t.readFromNbt(c1);
                boxes.put(n,t);
            }
        });
        name=tag.getString("name");
        if(tag.contains("members"))
        {
            ls= tag.getList("members", NbtList.COMPOUND_TYPE);
            members.clear();
            ls.forEach(c->{
                members.add(((NbtCompound)c).getUuid("id"));
            });
        }
    }
    public void writeToNbt(NbtCompound tag) {
        NbtList ls=new NbtList();
        members.forEach(m->{
            NbtCompound t=new NbtCompound();
            t.putUuid("id",m);
            ls.add(t);
        });
        NbtList terrains=new NbtList();
        this.boxes.forEach((k,t)->{
            var r=new NbtCompound();
            r.putString("name",k);
            t.writeNbt(r);
            terrains.add(r);
        });
        tag.put("terrains",terrains);
        tag.putString("name",name);
        tag.put("members",ls);
    }
    public void writeNetworkNBT(NbtCompound tag) {
        NbtList terrains=new NbtList();
        this.boxes.forEach((k,t)->{
            var r=new NbtCompound();
            r.putString("name",k);
            t.writeToNetworkNbt(r);
            terrains.add(r);
        });
        tag.put("terrains",terrains);
        tag.putString("name",name);
    }
}
