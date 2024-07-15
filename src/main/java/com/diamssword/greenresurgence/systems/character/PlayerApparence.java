package com.diamssword.greenresurgence.systems.character;

import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.clothing.ClothingLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlayerApparence {
    public float width=1;
    public float height=1.2f;
    private Map<ClothingLoader.Layer, ClothingLoader.Cloth> cloths=new HashMap<>();
    public int hair_color=0;
    public final PlayerData parent;
    private final List<String> unlockedCloths=new ArrayList<>();
    private final SavedOutfit[] outfits=new SavedOutfit[7];
    public PlayerApparence(PlayerData parent)
    {
        this.parent=parent;
    }
    public void unlockCloth(String id)
    {
        if(!unlockedCloths.contains(id))
            unlockedCloths.add(id);
    }
    public ArrayList<String> getUnlockedCloths()
    {
        return new ArrayList<>(unlockedCloths);
    }
    public Optional<ClothingLoader.Cloth> getCloth(ClothingLoader.Layer layer)
    {
        return Optional.ofNullable(cloths.get(layer));
    }
    public Map<ClothingLoader.Layer, ClothingLoader.Cloth> getCloths()
    {
        var res= new HashMap<>(cloths);
        for (ClothingLoader.Layer layer : ClothingLoader.Layer.clothLayers()) {
            if(!res.containsKey(layer))
                res.put(layer,null);
        }
        return res;
    }
    public boolean equipCloth(ClothingLoader.Layer layer,@Nullable ClothingLoader.Cloth cloth)
    {

        if(cloth ==null) {
            setCloth(layer,null);
            return true;
        }

        if(this.parent.player.getWorld().isClient || this.parent.player.isCreative() || this.unlockedCloths.contains(cloth.id()))
        {
            setCloth(layer,cloth);
            return true;
        }
        else
            return false;
    }
    public void setCloth(ClothingLoader.Layer layer,@Nullable ClothingLoader.Cloth cloth)
    {
        if(cloth==null)
            this.cloths.remove(layer);
        else
            this.cloths.put(layer,cloth);
        Components.PLAYER_DATA.sync(parent.player);

    }
    public void saveOutfit(String name,int index)
    {
        if(index<this.outfits.length && index>=0)
        {
            this.outfits[index]=new SavedOutfit(name);
            this.outfits[index].populate();
        }
    }
    public List<String> getOutfits()
    {
        var res=new ArrayList<String>();
        for (SavedOutfit outfit : this.outfits) {
            if(outfit!=null)
                res.add(outfit.name);
        }
        return res;
    }
    public void equipOutfit(int index)
    {
        if(index<this.outfits.length && index>=0 && this.outfits[index]!=null)
        {
            this.outfits[index].equipe();
        }
    }
    public void readFromNbt(NbtCompound tag) {
        if(tag.contains("width"))
            width=Math.max(Math.min(1.4f,tag.getFloat("width")),0.7f);
        else
            width=1;
        if(tag.contains("height"))
            height=Math.max(Math.min(1.4f,tag.getFloat("height")),0.7f);
        else
            height=1;
        hair_color=tag.getInt("hair");
        if(tag.contains("cloths"))
        {
            cloths.clear();
            var cl=tag.getCompound("cloths");
            cl.getKeys().forEach(k->{
                try {
                    var cl1=ClothingLoader.instance.getCloth(cl.getString(k));
                    cl1.ifPresent(v->cloths.put(ClothingLoader.Layer.valueOf(k),v));
                }catch (Exception ignored){}
            });
        }
        if(tag.contains("unlockedCloths"))
        {
            unlockedCloths.clear();
            var cl=tag.getList("unlockedCloths", NbtElement.COMPOUND_TYPE);
            cl.forEach(k-> unlockedCloths.add(((NbtCompound)k).getString("id")));
        }
        if(tag.contains("outfits"))
        {
            var cl=tag.getList("outfits", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < cl.size(); i++) {
                if(i<this.outfits.length)
                {
                    if(((NbtCompound)cl.get(i)).isEmpty())
                        outfits[i]=null;
                    else
                        outfits[i]=new SavedOutfit("").fromNBT(((NbtCompound)cl.get(i)));
                }

            }

        }
    }

    public void writeToNbt(NbtCompound tag) {
       tag.putFloat("width",width);
        tag.putFloat("height",height);
        tag.putInt("hair",hair_color);
        var cloths=new NbtCompound();
        var unlocked=new NbtList();
        this.unlockedCloths.forEach(v->{
            var t=new NbtCompound();
            t.putString("id",v);;
            unlocked.add(t);
        });
        this.cloths.forEach((i,v)->{
            cloths.putString(i.toString(),v.id());
        });
        tag.put("unlockedCloths",unlocked);
        tag.put("cloths",cloths);
        var outLs=new NbtList();
        for (SavedOutfit outfit : outfits) {
            if(outfit!=null)
                outLs.add(outfit.toNBT());
            else
                outLs.add(new NbtCompound());
        }
        tag.put("outfits",outLs);
    }
    public float getRestrainedHeight()
    {
        return Math.max(0.8f,Math.min(1.1f,this.height));
    }
    public float getRestrainedWidth()
    {
        return Math.max(0.8f,Math.min(1.2f,this.width));
    }
    public class SavedOutfit {
        public List<String> cloths=new ArrayList<>();
        public final String name;

        public SavedOutfit(String name) {
            this.name = name;
        }
        public void populate()
        {
            cloths.clear();
            PlayerApparence.this.getCloths().forEach((l,c)->{
                if(c!=null)
                    cloths.add(c.id());
            });
        }
        public SavedOutfit fromNBT(NbtCompound tag)
        {
            var res=new SavedOutfit(tag.getString("name"));
            var ls=tag.getList("cloths",NbtElement.COMPOUND_TYPE);
            ls.forEach(v->{
                res.cloths.add(((NbtCompound)v).getString("id"));
            });
            return res;
        }
        public NbtCompound toNBT()
        {
            var res=new NbtCompound();
            res.putString("name",this.name);
            var ls=new NbtList();
            this.cloths.forEach(c->{
                var c1=new NbtCompound();
                c1.putString("id",c);
                ls.add(c1);
            });
            res.put("cloths",ls);
            return res;
        }
        public void equipe()
        {
            cloths.forEach(v->{
                ClothingLoader.instance.getCloth(v).ifPresent(v1-> PlayerApparence.this.equipCloth(v1.layer(),v1));
            });

        }
    }
}
