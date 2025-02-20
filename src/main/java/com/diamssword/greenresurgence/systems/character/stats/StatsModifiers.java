package com.diamssword.greenresurgence.systems.character.stats;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.events.PlayerTickEvent;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.PosesPackets;
import com.diamssword.greenresurgence.systems.Components;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Debug;

import java.util.UUID;
import java.util.function.BiConsumer;

public class StatsModifiers {
    private static UUID parkourSpeedUUID= UUID.fromString("0a3bd7bf-25af-4c5d-b19a-d7d23cd77119");


    public static void init() {

    /*    PlayerTickEvent.onTick.register((pl,end)-> {
            if (!end) {
                pl.getComponent(Components.PLAYER_DATA).stats.get(StatsDef.STAT.aventure, StatsDef.parkour).ifPresent((st) -> {
                    if(st.getLevel()>0)
                    {
                        var max = pl.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                        if(pl.isSprinting())
                        {
                            pl.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST,5, (int) Math.ceil((st.getLevel()-1)*0.25),false,false));
                            if(max.getModifier(parkourSpeedUUID)==null)
                                max.addTemporaryModifier(new EntityAttributeModifier(parkourSpeedUUID,GreenResurgence.ID + ".StatSystem.ParkourSpeed",st.getLevel()*0.05f, EntityAttributeModifier.Operation.MULTIPLY_BASE));
                        }
                        else
                            max.removeModifier(parkourSpeedUUID);
                        if(pl.isSneaking())
                        {
                            pl.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST,5, (int) Math.ceil((st.getLevel()-1)*0.5),false,false));
                        }
                    }
                });
            }
        });
        UseBlockCallback.EVENT.register((p,w,h,r)->{
            if(p.hasPassengers())
            {
               p.getFirstPassenger().dismountVehicle();
               return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
        UseEntityCallback.EVENT.register((pl,world,hand,ent,hit)->{

            if(!world.isClient && pl.isSneaking() && ent instanceof PlayerEntity pl1)
            {
                var st=pl.getComponent(Components.PLAYER_DATA).stats.get(StatsDef.STAT.aventure, StatsDef.constitution);
                if(st.isPresent() && st.get().getLevel()>2)
                {
                    pl1.startRiding(pl,true);
                    Channels.MAIN.serverHandle(pl).send(new PosesPackets.LiftOtherPlayer(pl1.getUuid(),false));
                    Channels.MAIN.serverHandle(pl1).send(new PosesPackets.LiftOtherPlayer(pl.getUuid(),true));
                    pl1.getComponent(Components.PLAYER_DATA).setCustomPose("carried");
                    return  ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });

     */
    }
}
