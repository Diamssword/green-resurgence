package com.diamssword.greenresurgence.systems.crafting;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;
import java.util.Map;

public class CraftingResult{
        public boolean canCraft =false;
        public Map<UniversalResource,Boolean> stacks=new HashMap<>();
        public CraftingResult(boolean success,Map<UniversalResource,Boolean> stacks)
        {
            this.canCraft=success;
            this.stacks=stacks;
        }
        public static void serializer(PacketByteBuf write, CraftingResult val)
        {
            write.writeBoolean(val.canCraft);
            var ls=new NbtList();
            val.stacks.forEach((k,v)->{
                var tag=new NbtCompound();
                tag.putBoolean("result",v);
                tag.put("ressource",k.toNBT());
                ls.add(tag);
            });
            var r=new NbtCompound();
            r.put("list",ls);
            write.writeNbt(r);
        }
        public static CraftingResult unserializer(PacketByteBuf read)
        {
            var craft=read.readBoolean();
            var tag=read.readNbt();
            var ls=tag.getList("list", NbtElement.COMPOUND_TYPE);
            Map<UniversalResource,Boolean> items=new HashMap<>();
            ls.forEach(el->{
                if(el instanceof NbtCompound c)
                {
                    items.put(UniversalResource.fromNBT(c.getCompound("ressource")),c.getBoolean("result"));
                }
            });
            return new CraftingResult(craft,items);
        }

    }