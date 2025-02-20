package com.diamssword.greenresurgence.systems.character.customPoses;

import com.diamssword.greenresurgence.systems.Components;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.player.PlayerEntity;

public class CarryingPose implements IPlayerCustomPose{


    public CarryingPose(PlayerEntity player) {
    }

    @Override
    public boolean shouldExitPose(PlayerEntity player) {

        return !player.getComponent(Components.PLAYER_DATA).isCarryingEntity();
    }
}
