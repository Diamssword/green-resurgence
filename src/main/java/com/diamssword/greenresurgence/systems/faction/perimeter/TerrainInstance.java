package com.diamssword.greenresurgence.systems.faction.perimeter;

import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionTerrainStorage;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.TerrainEnergyStorage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TerrainInstance {
    private final List<BlockBox> boxes=new ArrayList<>();

    public final FactionInstance parent;
    public TerrainEnergyStorage energyStorage=new TerrainEnergyStorage();
    public FactionTerrainStorage storage=new FactionTerrainStorage();
    public TerrainInstance(FactionInstance parent)
    {
        this.parent=parent;
    }
    public TerrainInstance(BlockBox box,FactionInstance parent)
    {
        this.parent=parent;
        this.boxes.add(box);
    }
    public void addArea(BlockBox b)
    {
        if(boxes.stream().noneMatch(b1->b1.equals(b)))
            boxes.add(b);
    }
    public List<BlockBox> getBoxes()
    {
        return new ArrayList<>(boxes);
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
    public boolean removeAreaAt(BlockPos pos)
    {
        Optional<BlockBox> box =boxes.stream().filter(v -> v.contains(pos)).findFirst();
        return box.map(boxes::remove).orElse(false);
    }
    public void readFromNbt(NbtCompound tag) {
        NbtList ls= tag.getList("boxes", NbtList.INT_ARRAY_TYPE);
        ls.forEach(c->{
            NbtIntArray arr= (NbtIntArray) c;
            if(arr.size()>5) {
                boxes.add(new BlockBox(arr.get(0).intValue(),arr.get(1).intValue(),arr.get(2).intValue(),arr.get(3).intValue(),arr.get(4).intValue(),arr.get(5).intValue()));
            }
        });
        if(tag.contains("storage"))
            storage.fromNBT(tag.getCompound("storage"),parent.world);
        if(tag.contains("energy"))
            energyStorage.fromNBT(tag.getCompound("energy"));
    }

    public void writeNbt(NbtCompound tag) {
        this.writeToNetworkNbt(tag);
        var t1=new NbtCompound();
        storage.toNBT(t1);
        tag.put("storage",t1);
        var t2=new NbtCompound();
        energyStorage.toNBT(t2);
        tag.put("energy",t1);

    }
    public void writeToNetworkNbt(NbtCompound tag) {
        NbtList boxes=new NbtList();
        this.boxes.forEach(b->{
            boxes.add(new NbtIntArray(new int[]{b.getMinX(), b.getMinY(),b.getMinZ(),b.getMaxX(),b.getMaxY(),b.getMaxZ()}));
        });
        tag.put("boxes",boxes);
    }
}
