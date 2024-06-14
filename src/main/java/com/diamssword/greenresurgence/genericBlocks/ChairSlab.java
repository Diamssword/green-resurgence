package com.diamssword.greenresurgence.genericBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChairSlab extends PillarSlab implements IChairable {
    public ChairSlab(Settings settings) {
        super(settings);
    }
    @Deprecated
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(!world.isClient)
            this.sit(player,pos);
        return ActionResult.SUCCESS;
    }
    @Override
    public float sittingHeight() {
        return -0.35f;
    }
}
