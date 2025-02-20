package com.diamssword.greenresurgence.systems.character;

import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.character.customPoses.*;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class PosesManager {

    public static final String CARRIED= "carried";
    public static final String TWOHANDWIELD= "two_hand_wield";
    public static final String KNUCLESHANDWIELD= "knuckles_hand_wield";
    public static final String CARRYINGENTITY= "carrying_entity";
    private static Map<String, Function<PlayerEntity,IPlayerCustomPose>> posesRegister=new HashMap<>();
    static {
        posesRegister.put(CARRIED,BeingCarriedPose::new);
        posesRegister.put(TWOHANDWIELD, TwoHandWield::new);
        posesRegister.put(KNUCLESHANDWIELD, KnucklesHandWield::new);
        posesRegister.put(CARRYINGENTITY, CarryingPose::new);
    }
    public static IPlayerCustomPose createPose(String id,PlayerEntity player)
    {
        if(posesRegister.containsKey(id))
        {
            return posesRegister.get(id).apply(player);
        }
        return null;
    }
    public static void toggleCrawl(PlayerEntity entity)
    {

        var comp=entity.getComponent(Components.PLAYER_DATA);
        comp.setForcedPose(comp.getPose() ==EntityPose.SWIMMING?EntityPose.STANDING:EntityPose.SWIMMING);

    }
}
