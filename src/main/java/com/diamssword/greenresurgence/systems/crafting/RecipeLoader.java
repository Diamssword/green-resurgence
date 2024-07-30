package com.diamssword.greenresurgence.systems.crafting;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.systems.faction.BaseInteractions;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class RecipeLoader implements SimpleSynchronousResourceReloadListener {

    private static final Map<Identifier,Collection<IRecipe<UniversalResource>,UniversalResource>> registry=new HashMap<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private boolean shouldSync=false;
    public RecipeLoader() {
    }
    public Optional<Collection<IRecipe<UniversalResource>, UniversalResource>> getCollection(Identifier id)
    {
        return Optional.ofNullable(registry.get(id));
    }
    @Override
    public Identifier getFabricId() {
        return GreenResurgence.asRessource("grecipes");
    }

    @Override
    public void reload(ResourceManager manager) {
        registry.clear();
        BaseInteractions.registerBlocks();
        manager.findResources("grecipes",v-> v.getPath().endsWith(".json") && v.getPath().split("/").length>2).forEach((id, re)->{
            try {
                BufferedReader reader = re.getReader();
                try {
                    var jsonElement = JsonHelper.deserialize(GSON, reader, JsonObject.class);
                   if(jsonElement.has("type"))
                   {
                       var type=jsonElement.get("type").getAsString();
                       if(type.equals("simple"))
                       {
                            var recip=SimpleRecipe.deserializer(jsonElement);
                            var id1=new Identifier(id.getNamespace(),id.getPath().substring(id.getPath().indexOf("/")+1));
                            id1=new Identifier(id1.getNamespace(),id1.getPath().substring(0,id1.getPath().lastIndexOf("/")));
                            if(!registry.containsKey(id1))
                                registry.put(id1, new Collection<>());
                           recip.blocksResult().ifPresent(v1->BaseInteractions.allowedBlocks.add(v1));
                            registry.get(id1).add(recip);
                       }
                       else if(type.equals("multi"))
                       {
                           var recip=SimpleRecipe.deserializerMulti(jsonElement);
                           var id1=new Identifier(id.getNamespace(),id.getPath().substring(id.getPath().indexOf("/")+1));
                           id1=new Identifier(id1.getNamespace(),id1.getPath().substring(0,id1.getPath().lastIndexOf("/")));
                           if(!registry.containsKey(id1))
                               registry.put(id1, new Collection<>());

                           recip.forEach(v-> v.blocksResult().ifPresent(v1->BaseInteractions.allowedBlocks.add(v1)));

                           registry.get(id1).addAll(recip);
                       }
                       else
                           LOGGER.error("unsupported 'type': '{}' for {} from {}",type, id, getFabricId());
                   }
                   else
                   {
                       LOGGER.error("missing 'type' field for {} from {}", id, getFabricId());
                   }
                } catch (Exception e) {
                    LOGGER.error("Couldn't parse data file {} from {}", id, getFabricId(), e);
                } finally {
                    ((Reader) reader).close();
                }
            } catch (JsonParseException | IOException | IllegalArgumentException exception) {
                LOGGER.error("Couldn't parse data file {} from {}", id, getFabricId(), exception);
            }
        });
        shouldSync=true;
    }
    public void worldTick(MinecraftServer server)
    {
        if(shouldSync)
        {
            shouldSync=false;
            Channels.MAIN.serverHandle(server).send(BaseInteractions.getPacket());
          //  Channels.MAIN.serverHandle(server).send(new DictionaryPackets.ClothingList(this));
        }
    }
 /*   public static void serializer(PacketByteBuf write, RecipeLoader val)
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
    public static RecipeLoader unserializer(PacketByteBuf read)
    {

        RecipeLoader loader=new RecipeLoader();
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
    }*/
}
