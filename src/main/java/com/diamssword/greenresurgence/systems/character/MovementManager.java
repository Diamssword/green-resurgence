package com.diamssword.greenresurgence.systems.character;

import com.diamssword.greenresurgence.systems.Components;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;

public class MovementManager {

    public static void toggleCrawl(PlayerEntity entity)
    {

        var comp=entity.getComponent(Components.PLAYER_DATA);
        comp.setForcedPose(comp.getPose() ==EntityPose.SWIMMING?EntityPose.STANDING:EntityPose.SWIMMING);

    }
}
