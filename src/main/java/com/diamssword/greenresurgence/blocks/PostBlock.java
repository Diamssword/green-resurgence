package com.diamssword.greenresurgence.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class PostBlock extends Block {

    private final boolean large;
    public static final VoxelShape BIG= Block.createCuboidShape(5,0,5,11,16,11);
    public static final VoxelShape SMALL= Block.createCuboidShape(6,0,6,10,16,10);
    public PostBlock(Settings settings,boolean large) {
        super(settings);
        this.large=large;
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
           return large? BIG:SMALL;
    }
    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }
    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1f;
    }

}
