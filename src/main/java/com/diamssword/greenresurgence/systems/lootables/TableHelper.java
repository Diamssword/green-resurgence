package com.diamssword.greenresurgence.systems.lootables;

import com.diamssword.greenresurgence.GreenResurgence;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class TableHelper {

    private static Map<Identifier, LootTable.Builder> tables=new HashMap<>();
    public  static Identifier create(String name, LootTable.Builder loottable)
    {
        var id=GreenResurgence.asRessource("lootable/"+name);
        tables.put(id,loottable);
        return id;
    }
    public static void generate(BiConsumer<Identifier, LootTable.Builder> exporter)
    {
        tables.forEach(exporter);

    }
    public static LootTable.Builder pools(LootPool... pools)
    {
        var b=LootTable.builder();
        for(var p :pools)
        {
            b=b.pool(p);
        }
        return b;

    }
    public static LeafEntry.Builder<?> item(Identifier item, int min, int max, boolean byToolLevel)
    {
        return ItemEntry.builder(Registries.ITEM.get(item)).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(min,max)));
    }
    public static LeafEntry.Builder<?> item(Item item, int min, int max, boolean byToolLevel)
    {
        return ItemEntry.builder(item).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(min,max)));
    }
    public static LootPool.Builder fixed(int min, int max, boolean byToolLevel)
    {
       return LootPool.builder().rolls( UniformLootNumberProvider.create(min,max));
    }
    public static ToolTableMaterial simple(int min, int max, boolean byToolLevel)
    {
        return new ToolTableMaterial(min,max, byToolLevel);
    }
    public static ToolTableMaterial simple(int max)
    {
        return new ToolTableMaterial(1,max,false);
    }
    public static class ToolTableMaterial{
        private LootPool.Builder pool;
        private ToolTableMaterial(int min,int max, boolean byToolLevel)
        {
         pool=LootPool.builder().rolls(UniformLootNumberProvider.create(min,max));
        }
        public ToolTableMaterial add(Item item,int min,int max, boolean byToolLevel)
        {
                pool.with(ItemEntry.builder(item).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(min,max))));
                return this;
        }
        public ToolTableMaterial add(Item item,int min,int max,int weight, boolean byToolLevel)
        {

            pool.with(ItemEntry.builder(item).weight(weight).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(min,max))));
            return this;
        }
        public ToolTableMaterial addAir(int weight)
        {
            pool.with(ItemEntry.builder(Items.AIR).weight(weight));
            return this;
        }
        public ToolTableMaterial add(Item item,int min,int max,int weight)
        {
            return  this.add(item,min,max,weight,false);
        }
        public ToolTableMaterial add(Item item,int min,int max)
        {
           return this.add(item,min,max,false);
        }
        public LootTable.Builder build()
        {
            return LootTable.builder().pool(pool);
        }
    }
}
