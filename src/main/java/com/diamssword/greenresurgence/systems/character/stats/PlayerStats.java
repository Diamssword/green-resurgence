package com.diamssword.greenresurgence.systems.character.stats;

import com.diamssword.greenresurgence.systems.character.PlayerData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PlayerStats {
    private final Map<String,Integer> stats=new HashMap<>();
    private final Map<String,Integer> storedXP=new HashMap<>();
    public final PlayerData parent;
    public PlayerStats(PlayerData parent)
    {
        this.parent=parent;
    }
    public PlayerStats read(NbtCompound nbt)
    {
        var st=nbt.getCompound("stats");
        var ls=StatsDef.getRoles();
        st.getKeys().forEach(k->{
            if(ls.contains(k))
                stats.put(k,st.getInt(k));

        });
        var xps=nbt.getCompound("xp");
        xps.getKeys().forEach(k->{
            try{
                if(ls.contains(k))
                    storedXP.put(k,xps.getInt(k));
            }catch (IllegalArgumentException e){}

        });
        return this;
    }
    public NbtCompound write()
    {
        var nbt=new NbtCompound();
        var nbt2= new NbtCompound();
        var nbt1= new NbtCompound();
        stats.forEach(nbt1::putInt);
        storedXP.forEach(nbt2::putInt);
        nbt.put("stats",nbt1);
        nbt.put("xp",nbt2);
        return nbt;
    }
    public int getLevel(String main)
    {
        return stats.getOrDefault(main,0);
    }
    public int getXp(String role)
    {
        return storedXP.getOrDefault(role,0);
    }

    public void setLevel(String role,int count)
    {
        stats.put(role,count);
        StatsDef.onLevelChange(parent.player,role,count);
    }
    public void setXp(String role,int count)
    {
        storedXP.put(role,count);
    }
    public int getOrCreate(String main,int level)
    {
        if(!stats.containsKey(main))
        {
            stats.put(main,level);
        }
        return  stats.get(main);
    }
    public void onPlayerRespawn()
    {
        for (var item:stats.entrySet()) {
                StatsDef.onLevelChange(parent.player,item.getKey(),item.getValue());
        }
    }
}
