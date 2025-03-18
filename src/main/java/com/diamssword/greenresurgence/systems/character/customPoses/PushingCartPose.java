package com.diamssword.greenresurgence.systems.character.customPoses;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.player.PlayerEntity;

public class PushingCartPose implements IPlayerCustomPose{

    public PushingCartPose(PlayerEntity player) {

    }

    @Override
    public boolean shouldExitPose(PlayerEntity player) {

        return  player.getVehicle()==null;
    }
}
