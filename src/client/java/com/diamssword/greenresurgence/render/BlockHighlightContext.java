package com.diamssword.greenresurgence.render;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

@FunctionalInterface
public interface BlockHighlightContext {
    boolean shouldHighlight(BlockState state, WorldAccess w, BlockPos pos);
}