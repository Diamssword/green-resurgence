package com.diamssword.greenresurgence.systems.character.customPoses;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public interface IPlayerCustomPose {
    default public void tick(PlayerEntity player)
    {

    }
    default public EntityDimensions changeHitBox(PlayerEntity player,EntityDimensions baseDimension)
    {
        return baseDimension;
    }

    public boolean shouldExitPose(PlayerEntity player);

}
