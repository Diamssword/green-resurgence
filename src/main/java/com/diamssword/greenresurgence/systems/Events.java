package com.diamssword.greenresurgence.systems;

import com.diamssword.greenresurgence.network.AdventureInteract;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.DictionaryPackets;
import com.diamssword.greenresurgence.systems.clothing.ClothingLoader;
import com.diamssword.greenresurgence.systems.crafting.Recipes;
import com.diamssword.greenresurgence.systems.faction.BaseInteractions;
import com.diamssword.greenresurgence.systems.lootables.LootableLogic;
import com.diamssword.greenresurgence.systems.lootables.Lootables;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class Events {
    public static void init() {
        ServerPlayConnectionEvents.JOIN.register((h, s, serv)->{
            Channels.MAIN.serverHandle(h.player).send(new DictionaryPackets.LootableList(Lootables.loader));
            Channels.MAIN.serverHandle(h.player).send(new DictionaryPackets.ClothingList(ClothingLoader.instance));
            Channels.MAIN.serverHandle(h.player).send(BaseInteractions.getPacket());
        });
        ServerTickEvents.END_SERVER_TICK.register(Lootables.loader::worldTick);
        ServerTickEvents.END_SERVER_TICK.register(Recipes.loader::worldTick);
        ServerTickEvents.END_SERVER_TICK.register(ClothingLoader.instance::worldTick);

        UseBlockCallback.EVENT.register(LootableLogic::onRightClick);
    }
}
