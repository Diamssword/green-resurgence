package com.diamssword.greenresurgence.systems.lootables;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.*;

public class Lootable {
    private final Map<Identifier,Identifier> tables=new HashMap<>();
    private final Block block;
    private final Block empty;
    public Lootable(Identifier block,Identifier empty) throws Exception {
        Block b=Registries.BLOCK.get(block);
        Block b1=Registries.BLOCK.get(empty);
        if(b==null || b ==Blocks.AIR)
            throw new Exception();
        this.block=b;
        if(b1==null)
            b1=Blocks.AIR;
        this.empty=b1;
    }
    public Lootable(Block block,Block empty){
        this.block=block;
        this.empty=empty;
    }
    public boolean asTool(Identifier id)
    {
        return tables.containsKey(id);
    }
    public Block getEmptyBlock()
    {
        return empty;
    }
    public Block getBlock()
    {
        return block;
    }
    public Identifier getLootForTool(Identifier tool)
    {
        return tables.get(tool);
    }
    public Lootable addTool(Identifier tool, Identifier lootable)
    {
        tables.put(tool,lootable);
        return this;
    }
    public NbtCompound toNBT()
    {
        var comp=new NbtCompound();
        comp.putString("block",Registries.BLOCK.getId(this.block).toString());
        comp.putString("empty",Registries.BLOCK.getId(this.empty).toString());
        var ls=new NbtCompound();
        this.tables.forEach((v,t)->{
            ls.putString(v.toString(),t.toString());
        });
        comp.put("tools",ls);
        return comp;
    }
    public static Lootable fromNBT(NbtCompound comp) throws Exception {
        Identifier b=new Identifier(comp.getString("block"));
        Identifier b1=new Identifier(comp.getString("empty"));
        var res=new Lootable(b,b1);
        var ls=comp.getCompound("tools");
        ls.getKeys().forEach(k->{
            res.addTool(new Identifier(k),new Identifier(ls.getString(k)));
        });
        return res;
    }
}
