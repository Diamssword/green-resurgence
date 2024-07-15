package com.diamssword.greenresurgence.systems.clothing;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.DictionaryPackets;
import com.diamssword.greenresurgence.systems.Components;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class ClothingLoader implements SimpleSynchronousResourceReloadListener {

    public static ClothingLoader instance=new ClothingLoader();

    public static enum Layer{
        hat(15,16),

        hair(11,12),
        glasses(10,11),
        accessories(8,11),
        jacket(9,10),
        teeshirt(4,5),
        underwear(2),
        pants(3,6),
        shoes(7,8),
        full(13,14),
        body(0,1);
        public final int layer1;
        public final int layer2;
        private Layer(int layer1,int layer2)
        {
            this.layer1=layer1;
            this.layer2=layer2;
        }
        private Layer(int layer)
        {
            this.layer1=layer;
            this.layer2=-1;
        }
        public static Layer[] clothLayers()
        {
            return Arrays.stream(values()).filter(v->v!= hair && v!=body).toList().toArray(new Layer[1]);
        }
    }
    public static record Cloth(String id, String name, Layer layer,String collection){
        public NbtCompound toNBT()
        {
            var res=new NbtCompound();
            res.putString("name",name);
            res.putString("layer",layer.toString());
            res.putString("collection",collection);
            return res;
        }
        public static Cloth fromNBT(NbtCompound comp, String id)
        {
            try {
                return new Cloth(id,comp.getString("name"), Layer.valueOf(comp.getString("layer")),comp.getString("collection"));
            }catch (IllegalArgumentException e)
            {
                return null;
            }
        }
    };
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private Map<String, Cloth> cloths=new HashMap<>();
    private final List<String> collections=new ArrayList<>();
    private boolean shouldSync=false;
    public ClothingLoader() {
    }

    public Optional<Cloth> getCloth(String id)
    {
        return Optional.ofNullable(cloths.get(id));
    }
    public List<String> getCollections()
    {
        return new ArrayList<>(collections);
    }
    public List<Cloth> getClothsCollection(String collection)
    {
        return cloths.values().stream().filter(v->collection.equals("all") || v.collection.equals(collection)).toList();
    }
    public List<Cloth> getClothsCollection(String collection,Layer... layers)
    {
        var lays=Arrays.stream(layers).toList();
        return cloths.values().stream().filter(v->(v.collection.equals(collection)|| collection.equals("all")) && lays.contains(v.layer)).toList();
    }
    public List<Cloth> getAvailablesClothsCollectionForPlayer(PlayerEntity ent,String collection, Layer... layers)
    {

        var lays=Arrays.stream(layers).toList();
        if(ent.isCreative())
            return cloths.values().stream().filter(v->(collection.equals("all") || v.collection.equals(collection)) &&  lays.contains(v.layer)).toList();
        else
        {
            var unl=ent.getComponent(Components.PLAYER_DATA).appearance.getUnlockedCloths();
            return cloths.values().stream().filter(v->(collection.equals("all") || v.collection.equals(collection)) &&  lays.contains(v.layer) && unl.contains(v.id)).toList();
        }
    }
    public List<Cloth> getForLayers(Layer... layers)
    {
        List<Cloth> res=new ArrayList<>();
        var ls=Arrays.stream(layers).toList();
        cloths.forEach((k,v)->{

            if(ls.contains(v.layer))
            {
                res.add(v);
            }
        });
        return res;
    }
    @Override
    public Identifier getFabricId() {
        return GreenResurgence.asRessource("cloths");
    }

    @Override
    public void reload(ResourceManager manager) {
        cloths=new HashMap<>();
        collections.clear();
        var id=GreenResurgence.asRessource("cloths.json");
        var file = manager.getResource(id);
        if(file.isPresent()) {
            try {
                BufferedReader reader = file.get().getReader();
                try {
                    JsonArray jsonElement = JsonHelper.deserialize(GSON, (Reader) reader, JsonArray.class);
                    jsonElement.forEach(v->{
                       var ob= v.getAsJsonObject();
                       if(ob.has("id"))
                       {
                           if(!cloths.containsKey(ob.get("id").getAsString()))
                           {
                               if(ob.has("layer") && ob.has("name")) {
                                   try {
                                       String col="default";
                                       if(ob.has("collection"))
                                           col=ob.get("collection").getAsString();
                                       Cloth table = new Cloth(ob.get("id").getAsString(),ob.get("name").getAsString(), Layer.valueOf(ob.get("layer").getAsString()),col);
                                       if(!collections.contains(col))
                                           collections.add(col);
                                       cloths.put(ob.get("id").getAsString(),table);
                                   }catch (IllegalArgumentException e)
                                   {
                                       LOGGER.error("Layer for clothing with id: {} can't be parsed (layer {})", ob.get("id"),ob.get("layer"));
                                   }
                               }
                               else
                                   LOGGER.error("Missing name of layer for clothing with id: {}", ob.get("id"));
                           }
                           else
                               LOGGER.error("Duplicate id for clothing: {}", ob.get("id"));
                       }
                       else
                           LOGGER.error("Clothing is missing ID!");
                    });
                } finally {
                    ((Reader) reader).close();
                    shouldSync=true;
                }
            } catch (JsonParseException | IOException | IllegalArgumentException exception) {
                LOGGER.error("Couldn't parse data file {} from {}", id, getFabricId(), exception);
            }
        }
    }
    public void worldTick(MinecraftServer server)
    {
        if(shouldSync)
        {
            shouldSync=false;
            Channels.MAIN.serverHandle(server).send(new DictionaryPackets.ClothingList(this));
        }
    }
    public static void serializer(PacketByteBuf write, ClothingLoader val)
    {
        NbtList list =new NbtList();
        NbtList collection =new NbtList();

        val.cloths.forEach((u,v)->{
            var v1=v.toNBT();
            v1.putString("id",u);
            list.add(v1);
        });
        val.collections.forEach(c->{
            var v1=new NbtCompound();
            v1.putString("id",c);
            collection.add(v1);
        });
        var comp=new NbtCompound();
        comp.put("list",list);
        comp.put("collection",collection);
        write.writeNbt(comp);
    }
    public static ClothingLoader unserializer(PacketByteBuf read)
    {

        ClothingLoader loader=new ClothingLoader();
        var comp=read.readNbt();
        var list=comp.getList("list", NbtElement.COMPOUND_TYPE);
        var list1=comp.getList("collection", NbtElement.COMPOUND_TYPE);
        list1.forEach(c->{
           loader.collections.add (((NbtCompound)c).getString("id"));
        });
        list.forEach(el->{
            try {
                var t= Cloth.fromNBT((NbtCompound) el,((NbtCompound) el).getString("id"));
                if(t!=null)
                    loader.cloths.put(((NbtCompound) el).getString("id"),t);
            }catch (Exception e)
            {
                LOGGER.error("Couldn't parse packet data for lootable: {}", el, e);
            }
        });
     return loader;
    }
}
