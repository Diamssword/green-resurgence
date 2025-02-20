package com.diamssword.greenresurgence.systems.character.customPoses;

import com.diamssword.greenresurgence.items.weapons.ICustomPoseWeapon;
import net.minecraft.entity.player.PlayerEntity;

public class TwoHandWield implements IPlayerCustomPose{

    public TwoHandWield(PlayerEntity player) {

    }


    @Override
    public boolean shouldExitPose(PlayerEntity player) {
        return !(player.getMainHandStack().getItem() instanceof ICustomPoseWeapon);
    }
}
