package com.diamssword.greenresurgence.systems.clothing;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.DictionaryPackets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class ClothingLoader implements SimpleSynchronousResourceReloadListener {

    public static ClothingLoader instance=new ClothingLoader();

    public static enum Layer{
        body(0,1),
        underware(2),
        teeshirt(4,5),
        pants(3,6),
        shoes(7,8),
        jacket(9,10),
        hair(11,12),
        full(13,14),
        hat(15,16);
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
    }
    public static record Item(String name,Layer layer){
        public NbtCompound toNBT()
        {
            var res=new NbtCompound();
            res.putString("name",name);
            res.putString("layer",layer.toString());
            return res;
        }
        public static Item fromNBT(NbtCompound comp)
        {
            try {
                return new Item(comp.getString("name"), Layer.valueOf(comp.getString("layer")));
            }catch (IllegalArgumentException e)
            {
                return null;
            }
        }
    };
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private Map<String, Item> cloths=new HashMap<>();
    private boolean shouldSync=false;
    public ClothingLoader() {
    }

    public Optional<Item> getCloth(String id)
    {
        return Optional.ofNullable(cloths.get(id));
    }
    public Optional<Pair<String,Item>> getFirstForLayer(Layer layer)
    {
        return getForLayer(layer).stream().findFirst();
    }
    public List<Pair<String, Item>> getForLayer(Layer layer)
    {
        List<Pair<String,Item>> res=new ArrayList<>();
        cloths.forEach((k,v)->{
            if(v.layer==layer)
            {
                res.add(new Pair<>(k,v));
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
                                       Item table = new Item(ob.get("name").getAsString(), Layer.valueOf(ob.get("layer").getAsString()));
                                       cloths.put(ob.get("id").getAsString(),table);
                                   }catch (IllegalArgumentException e)
                                   {
                                       LOGGER.error("Layer for clothing with id: {} can't be parsed", id);
                                   }
                               }
                               else
                                   LOGGER.error("Missing name of layer for clothing with id: {}", id);
                           }
                           else
                               LOGGER.error("Duplicate id for clothing: {}", id);
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

        val.cloths.forEach((u,v)->{
            var v1=v.toNBT();
            v1.putString("id",u);
            list.add(v1);
        });
        var comp=new NbtCompound();
        comp.put("list",list);
        write.writeNbt(comp);
    }
    public static ClothingLoader unserializer(PacketByteBuf read)
    {

        ClothingLoader loader=new ClothingLoader();
        var comp=read.readNbt();
        var list=comp.getList("list", NbtElement.COMPOUND_TYPE);
        list.forEach(el->{
            try {
                var t=Item.fromNBT((NbtCompound) el);
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
