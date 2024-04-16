package com.diamssword.greenresurgence.blocks;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;

public class MainStructureBlock extends LayeredBlock{
    public MainStructureBlock(Settings settings) {
        super(settings);
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
