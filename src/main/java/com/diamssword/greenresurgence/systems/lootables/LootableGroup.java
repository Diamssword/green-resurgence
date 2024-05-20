package com.diamssword.greenresurgence.systems.lootables;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.*;

public class LootableGroup {
    private static List<LootableGroup> list=new ArrayList<>();
    private final Map<Identifier,Identifier> tables=new HashMap<>();
    private final List<Block> blocks=new ArrayList<>();
    private final Map<Block,Block> replacements=new HashMap<>();
    private LootableGroup(){}
    public boolean asTool(Identifier id)
    {
        return tables.containsKey(id);
    }
    public Block getEmptyBlock(Block b)
    {
        return replacements.getOrDefault(b, Blocks.AIR);
    }
    public boolean asBlock(Block b)
    {
        return blocks.contains(b);
    }
    public Identifier getLootForTool(Identifier tool)
    {
        return tables.get(tool);
    }
    public LootableGroup addTool(TagKey<Item> tool, Identifier lootable)
    {
        tables.put(tool.id(),lootable);
        return this;
    }
    public LootableGroup add(Block... b)
    {
        Collections.addAll(blocks, b);
        return this;
    }
    public LootableGroup addRep(Block replace, Block... b)
    {
        Collections.addAll(blocks, b);
        for (Block block : b) {
            replacements.put(block,replace);
        }
        return this;
    }
    public LootableGroup addRep(Identifier replace, Identifier... b)
    {
        Block r1=Registries.BLOCK.get(replace);
        for (Identifier identifier : b) {
            Block b1=Registries.BLOCK.get(identifier);
            if(b1!=null) {
                blocks.add(b1);
                replacements.put(b1,r1);
            }
        }
        return this;
    }
    public LootableGroup add(Identifier... b)
    {
        for (Identifier identifier : b) {
            Block b1=Registries.BLOCK.get(identifier);
            if(b1!=null)
                blocks.add(b1);
        }

        return this;
    }
    public static LootableGroup create()
    {
        var res=new LootableGroup();
        list.add(res);
        return res;
    }
    public static List<LootableGroup> getGroups()
    {
        return list;
    }
}
