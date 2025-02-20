package com.diamssword.greenresurgence.systems.character.customPoses;

import com.diamssword.greenresurgence.items.weapons.KnuckleItem;
import com.diamssword.greenresurgence.items.weapons.ICustomPoseWeapon;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class KnucklesHandWield implements IPlayerCustomPose{

    public KnucklesHandWield(PlayerEntity player) {

    }
    private boolean nextSwingLeft;
    private float lastProg;
    @Override
    public void tick(PlayerEntity player) {
        if(player.getWorld().isClient && player.getOffHandStack().getItem() instanceof KnuckleItem)
        {
            if(player.handSwinging)
            {
                lastProg=0;
            }
            else if(player.handSwingProgress==0 && player.lastHandSwingProgress >0 && player.lastHandSwingProgress !=lastProg)
            {
                lastProg=player.lastHandSwingProgress;
                nextSwingLeft=!nextSwingLeft;
            }
            if(nextSwingLeft)
            {
                player.preferredHand= Hand.OFF_HAND;
            }
        }
    }

    @Override
    public boolean shouldExitPose(PlayerEntity player) {
        return !(player.getMainHandStack().getItem() instanceof ICustomPoseWeapon);
    }
}
