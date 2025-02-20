package com.diamssword.greenresurgence.systems.character.stats.classes;

import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.PosesPackets;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.character.PosesManager;
import com.diamssword.greenresurgence.systems.character.stats.StatsDef;
import com.diamssword.greenresurgence.systems.character.stats.StatsRole;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

import static com.diamssword.greenresurgence.systems.character.stats.ModifiersList.*;

public class ClasseBrute extends StatsRole {

    public static final int PALIER1=10;
    public static final int PALIER2=20;

    public ClasseBrute(String name) {
        super(name);
    }

    @Override
    public void init() {
        create(PALIER1,(t)->{});
        create(PALIER2,(t)->{
            t.addModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE,ATTACK_DAMAGE_ID,2, EntityAttributeModifier.Operation.MULTIPLY_BASE);
        });
        addGlobalModifier(EntityAttributes.GENERIC_MAX_HEALTH,
                (l)-> StatsRole.modifier(EntityAttributes.GENERIC_MAX_HEALTH,HEALTH_ID,l, EntityAttributeModifier.Operation.ADDITION));
        eventsRegister();
    }


    private void eventsRegister()
    {
        UseBlockCallback.EVENT.register((p, w, h, r)->{
            if(p.hasPassengers())
            {
                p.getFirstPassenger().dismountVehicle();
                return ActionResult.SUCCESS;
            }
            else if(!w.isClient) {
                var d = p.getComponent(Components.PLAYER_DATA);
                if (d.isCarryingEntity())
                    d.placeCarriedEntity();
            }
            return ActionResult.PASS;
        });
        UseEntityCallback.EVENT.register((pl, world, hand, ent, hit)->{

            if(!world.isClient && pl.isSneaking())
            {
                var st=pl.getComponent(Components.PLAYER_DATA).stats.getLevel(name);
                if(st>=PALIER1 )
                {
                    if(ent instanceof PlayerEntity pl1)
                    {
                        pl1.startRiding(pl,true);
                        Channels.MAIN.serverHandle(pl).send(new PosesPackets.LiftOtherPlayer(pl1.getUuid(),false));
                        Channels.MAIN.serverHandle(pl1).send(new PosesPackets.LiftOtherPlayer(pl.getUuid(),true));
                        pl1.getComponent(Components.PLAYER_DATA).setCustomPose("carried");
                        return  ActionResult.SUCCESS;
                    }
                    else if(ent.getWidth()<1f && ent instanceof LivingEntity li)
                    {
                        if(li.getGroup() == EntityGroup.DEFAULT ||li.getGroup()==EntityGroup.AQUATIC) {
                            var c = pl.getComponent(Components.PLAYER_DATA);
                            c.setCarriedEntity(ent);
                            c.setCustomPose(PosesManager.CARRYINGENTITY);
                            return ActionResult.SUCCESS;
                        }
                    }
                }
            }
            return ActionResult.PASS;
        });
    }
}
