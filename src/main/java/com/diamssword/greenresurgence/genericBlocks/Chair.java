package com.diamssword.greenresurgence.genericBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Chair extends GenericPillar implements IChairable {
    public Chair(Settings settings, GenericBlockSet.Transparency transp) {
        super(settings,transp);
    }
    @Deprecated
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(!world.isClient)
            this.sit(player,pos);
        return ActionResult.SUCCESS;
    }
    @Override
    public float sittingHeight() {
        return -0.1f;
    }
}
