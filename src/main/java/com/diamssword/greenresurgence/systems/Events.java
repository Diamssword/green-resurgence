package com.diamssword.greenresurgence.systems;

import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.containers.player.VanillaPlayerInvMokup;
import com.diamssword.greenresurgence.entities.BackpackEntity;
import com.diamssword.greenresurgence.events.PlayerTickEvent;
import com.diamssword.greenresurgence.items.AbstractBackpackItem;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.DictionaryPackets;
import com.diamssword.greenresurgence.systems.character.PlayerEvents;
import com.diamssword.greenresurgence.systems.clothing.ClothingLoader;
import com.diamssword.greenresurgence.systems.crafting.Recipes;
import com.diamssword.greenresurgence.systems.faction.BaseInteractions;
import com.diamssword.greenresurgence.systems.lootables.LootableLogic;
import com.diamssword.greenresurgence.systems.lootables.Lootables;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Events {
    public static void init() {
        List<PlayerEntity> scheluded=new ArrayList<>();
        ServerPlayConnectionEvents.JOIN.register((h, s, serv)->{
            scheluded.add(h.player);
        });
        ServerTickEvents.END_SERVER_TICK.register(Lootables.loader::worldTick);
        ServerTickEvents.END_SERVER_TICK.register(Recipes.loader::worldTick);
        ServerTickEvents.END_SERVER_TICK.register(ClothingLoader.instance::worldTick);
        ServerTickEvents.START_WORLD_TICK.register(w->{
            w.getEntitiesByType(TypeFilter.instanceOf(ItemEntity.class), i->i.getStack().getItem() instanceof AbstractBackpackItem).forEach(v->{

                if(!((AbstractBackpackItem)v.getStack().getItem()).isInventoryEmpty(v.getStack())) {
                    w.spawnEntity(new BackpackEntity(w, v.getX(), v.getY(), v.getZ(), v.getStack()));
                    v.discard();
                }
            });
        });
        ServerTickEvents.END_SERVER_TICK.register((s)->{
            if(s.getOverworld().getTime()%20==0)
            {
                var ls=new ArrayList<>(scheluded);

                ls.forEach(l1->{//on laisse le temps a MC d'envoyer les tags et autres données avant
                    Channels.MAIN.serverHandle(l1).send(new DictionaryPackets.LootableList(Lootables.loader));
                    Channels.MAIN.serverHandle(l1).send(new DictionaryPackets.ClothingList(ClothingLoader.instance));
                    Channels.MAIN.serverHandle(l1).send(BaseInteractions.getPacket());
                    Channels.MAIN.serverHandle(l1).send(new DictionaryPackets.RecipeList(Recipes.loader));
                });
                scheluded.clear();
            }


        });
        ServerTickEvents.START_SERVER_TICK.register((s)->{
            s.getPlayerManager().getPlayerList().forEach(pl->{
                PlayerTickEvent.onTick.invoker().onTick(pl,false);
            });
        });
        ServerTickEvents.END_SERVER_TICK.register((s)->{
            s.getPlayerManager().getPlayerList().forEach(pl->{
                PlayerTickEvent.onTick.invoker().onTick(pl,true);
            });
        });
        UseBlockCallback.EVENT.register(LootableLogic::onRightClick);
        PlayerEvents.init();

    }
}
