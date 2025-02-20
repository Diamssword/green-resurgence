package com.diamssword.greenresurgence.systems.character.stats;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.character.stats.classes.ClasseBrute;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Debug;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class StatsDef {

    private static UUID healthUUID= UUID.fromString("c4a02e55-3b2b-4d9b-abbc-436041d1710f");

    private static final Map<String,StatsRole> roles=new HashMap<>();
    static {
        createRole("brute",ClasseBrute::new);
        initEvents();
        StatsModifiers.init();
    }
    private static void createRole(String name, Function<String,? extends StatsRole> factory)
    {
        var f=factory.apply(name);
        roles.put(name,f);
        f.init();
    }
    public static List<String> getRoles() {
        return roles.keySet().stream().toList();
    }

    private static void initEvents()
    {
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (!alive) {
                newPlayer.getComponent(Components.PLAYER_DATA).stats.onPlayerRespawn();
            }
        });
        ServerLivingEntityEvents.AFTER_DEATH.register((e,i)->{
            if (e instanceof ServerPlayerEntity player) {
                player.getComponent(Components.PLAYER_DATA).placeCarriedEntity();
            }

        });
    }

    public static void onLevelChange(PlayerEntity pl,String role,int level)
    {
        var r=roles.get(role);
        if(r!=null)
        {
            r.onLevelChange(pl,level);
        }
    }
}
