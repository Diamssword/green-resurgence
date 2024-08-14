package com.diamssword.greenresurgence.systems.crafting;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.DictionaryPackets;
import com.diamssword.greenresurgence.systems.faction.BaseInteractions;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.nbt.*;
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

public class RecipeLoader implements SimpleSynchronousResourceReloadListener {

    private final Map<Identifier, RecipeCollection> registry=new HashMap<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private boolean shouldSync=false;
    public RecipeLoader() {
    }
    public Optional<RecipeCollection> getCollection(Identifier id)
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
                                registry.put(id1, new RecipeCollection(id1));
                           recip.blocksResult().ifPresent(v1->BaseInteractions.allowedBlocks.add(v1));
                           var id2=id.getPath().substring(id.getPath().lastIndexOf("/")+1).replace(".json","");
                            registry.get(id1).add(id2,recip);
                       }
                       else if(type.equals("multi"))
                       {
                           var recip=SimpleRecipe.deserializerMulti(jsonElement);
                           var id1=new Identifier(id.getNamespace(),id.getPath().substring(id.getPath().indexOf("/")+1));
                           id1=new Identifier(id1.getNamespace(),id1.getPath().substring(0,id1.getPath().lastIndexOf("/")));
                           if(!registry.containsKey(id1))
                               registry.put(id1, new RecipeCollection(id1));
                           var id2=id.getPath().substring(id.getPath().lastIndexOf("/")+1).replace(".json","");
                           recip.forEach(v-> v.blocksResult().ifPresent(v1->BaseInteractions.allowedBlocks.add(v1)));
                            var m=new HashMap<String,SimpleRecipe>();
                           for (int i = 0; i < recip.size(); i++) {
                               m.put(id2+i,recip.get(i));
                           }
                           registry.get(id1).addAll(m);
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
            Channels.MAIN.serverHandle(server).send(new DictionaryPackets.RecipeList(this));
        }
    }
    public static void serializer(PacketByteBuf write, RecipeLoader val)
    {
        NbtList list =new NbtList();
        val.registry.forEach((k,v)->{
            var tag=new NbtCompound();
            tag.putString("id",k.toString());
            var recs=v.getRecipes(null);

            NbtList list1 =new NbtList();
            recs.forEach(r->{
                    var sr=r.serialize();
                    sr.addProperty("unserial","simple");
                    list1.add(NbtString.of(sr.toString()));
            });
            tag.put("recipes",list1);
            list.add(tag);
        });
        var res=new NbtCompound();
        res.put("list",list);
        write.writeNbt(res);
    }
    public static RecipeLoader unserializer(PacketByteBuf read)
    {

        RecipeLoader loader=new RecipeLoader();
        var comp=read.readNbt();
        var list=comp.getList("list", NbtElement.COMPOUND_TYPE);

        list.forEach(el->{
            var e=(NbtCompound) el;
            var id=new Identifier(e.getString("id"));
            loader.registry.putIfAbsent(id,new RecipeCollection(id));
            if(id !=null)
            {
                var ls1=e.getList("recipes",NbtElement.STRING_TYPE);
                ls1.forEach(el1->{
                   var ob= JsonHelper.deserialize(GSON, el1.asString(), JsonObject.class);
                    if(ob.get("unserial").getAsString().equals("simple"))
                    {
                        try {
                            var r1=SimpleRecipe.deserializer(ob);
                            loader.registry.get(id).add(r1);
                        } catch (Exception ex) {
                           ex.printStackTrace();
                        }
                    }
                });
            }
        });
     return loader;
    }
}
