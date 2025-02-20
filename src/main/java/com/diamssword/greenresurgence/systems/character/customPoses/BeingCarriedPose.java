package com.diamssword.greenresurgence.systems.character.customPoses;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class BeingCarriedPose implements IPlayerCustomPose{

    public BeingCarriedPose(PlayerEntity player) {

    }

    @Override
    public void tick(PlayerEntity player) {

        var vh=player.getVehicle();
        if(vh instanceof PlayerEntity pl1)
        {

            player.bodyYaw=pl1.headYaw;
            player.prevYaw=pl1.prevYaw;
            player.setYaw(pl1.getYaw());
            player.prevBodyYaw=pl1.prevHeadYaw;

        }
    }

    @Override
    public EntityDimensions changeHitBox(PlayerEntity player, EntityDimensions baseDimension) {

        return EntityDimensions.fixed(0.1f,0.1f);
    }


    @Override
    public boolean shouldExitPose(PlayerEntity player) {

        return  player.getVehicle()==null;
    }
}
