package com.diamssword.greenresurgence.genericBlocks;

import com.diamssword.greenresurgence.entities.ChairEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public interface IChairable {
    public float sittingHeight();
    public boolean canUse();
    default public void sit(PlayerEntity entity, BlockPos pos){
        var c=new ChairEntity(entity.getWorld(),pos,this);
        entity.getWorld().spawnEntity(c);
        entity.startRiding(c);
    }
}
