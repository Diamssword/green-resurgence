package com.diamssword.greenresurgence.systems.faction.perimeter;

import com.diamssword.greenresurgence.events.BaseEventCallBack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FactionInstance {
    private final List<BlockBox> boxes=new ArrayList<>();
    private String name;
    private List<ServerPlayerEntity> inBase=new ArrayList<>();
    private List<UUID> members=new ArrayList<>();
    public FactionInstance()
    {
    }

    public FactionInstance(String name, BlockBox box)
    {
        this.name=name;
        this.boxes.add(box);

    }
    public boolean removeAreaAt(BlockPos pos)
    {
        Optional<BlockBox> box =boxes.stream().filter(v -> v.contains(pos)).findFirst();
        return box.map(boxes::remove).orElse(false);
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
        return  new ArrayList<>(boxes);
    }
    public String getName()
    {
        return name;
    }
    public void addArea(BlockBox b)
    {
        if(boxes.stream().noneMatch(b1->b1.equals(b)))
            boxes.add(b);
    }
    public boolean isIn(Vec3i pos)
    {
        for(BlockBox b : boxes)
        {
            if(b.contains(pos))
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
        NbtList ls= tag.getList("boxes", NbtList.INT_ARRAY_TYPE);
        ls.forEach(c->{
            NbtIntArray arr= (NbtIntArray) c;
            if(arr.size()>5) {
                boxes.add(new BlockBox(arr.get(0).intValue(),arr.get(1).intValue(),arr.get(2).intValue(),arr.get(3).intValue(),arr.get(4).intValue(),arr.get(5).intValue()));
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
        this.writeNetworkNBT(tag);
        NbtList ls=new NbtList();
        members.forEach(m->{
            NbtCompound t=new NbtCompound();
            t.putUuid("id",m);
            ls.add(t);
        });
        tag.put("members",ls);
    }
    public void writeNetworkNBT(NbtCompound tag) {
        NbtList boxes=new NbtList();
        this.boxes.forEach(b->{
            boxes.add(new NbtIntArray(new int[]{b.getMinX(), b.getMinY(),b.getMinZ(),b.getMaxX(),b.getMaxY(),b.getMaxZ()}));
        });
        tag.put("boxes",boxes);
        tag.putString("name",name);
    }
}
