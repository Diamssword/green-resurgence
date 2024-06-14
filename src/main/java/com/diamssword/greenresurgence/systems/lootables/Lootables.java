package com.diamssword.greenresurgence.systems.lootables;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.network.AdventureInteract;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.DictionaryPackets;
import com.diamssword.greenresurgence.systems.clothing.ClothingLoader;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.mixin.resource.conditions.DataPackContentsMixin;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.report.DynamicRegistriesProvider;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.SimpleResourceReload;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.command.ReloadCommand;
import net.minecraft.util.Identifier;

import java.time.temporal.Temporal;

public class Lootables {
    /*
    Le temps de refresh d'un block en millisecondes (7J ici)
     */
    public static LootablesReloader loader =new LootablesReloader();
    public static final long refreshPhase =30000; //604_800_000;
    public static final TagKey<Item> WRENCH= createTool("wrench");
    public static final TagKey<Item> HAMMER=createTool("hammer");
    public static final TagKey<Item> HAND=createTool("hand");
    private static TagKey<Item> createTool(String name)
    {
        return TagKey.of(RegistryKeys.ITEM,GreenResurgence.asRessource("lootable/tools/"+name));
    }

    public static boolean isGoodTool(Block b, Identifier tool) {
        return loader.getTable(b).map(lootable -> lootable.asTool(tool)).orElse(false);
    }
    public static Identifier getTableForBlock(Block b, Identifier tool) {
        var val=loader.getTable(b);
        if(val.isPresent() && val.get().asTool(tool))
        {
            return val.get().getLootForTool(tool);
        }
        return null;
    }
    public static Block getEmptyBlock(Block b) {
     return loader.getTable(b).map(Lootable::getEmptyBlock).orElse(Blocks.AIR);

    }

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register((h,s,serv)->{
            Channels.MAIN.serverHandle(h.player).send(new DictionaryPackets.LootableList(loader));
            Channels.MAIN.serverHandle(h.player).send(new DictionaryPackets.ClothingList(ClothingLoader.instance));
        });
        ServerTickEvents.END_SERVER_TICK.register(loader::worldTick);
        ServerTickEvents.END_SERVER_TICK.register(ClothingLoader.instance::worldTick);
    }
}

