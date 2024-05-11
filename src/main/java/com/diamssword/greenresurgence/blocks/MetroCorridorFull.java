package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.genericBlocks.OmniBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class MetroCorridorFull extends OmniBlock {
    public static final VoxelShape SHAPE_N=Block.createCuboidShape(0,0,0,32,16,16);
    public static final VoxelShape SHAPE_S=Block.createCuboidShape(-16,0,0,16,16,16);
    public static final VoxelShape SHAPE_E=Block.createCuboidShape(0,0,0,16,16,32);
    public static final VoxelShape SHAPE_W=Block.createCuboidShape(0,0,-16,16,16,16);

    public static final VoxelShape SHAPE_N1=Block.createCuboidShape(0,0,0,32,16,2);
    public static final VoxelShape SHAPE_S1=Block.createCuboidShape(-16,0,14,16,16,16);
    public static final VoxelShape SHAPE_E1=Block.createCuboidShape(14,0,0,16,16,32);
    public static final VoxelShape SHAPE_W1=Block.createCuboidShape(0,0,-16,2,16,16);
    public static final VoxelShape SHAPE_N2=Block.createCuboidShape(-16,0,0,16,16,2);
    public static final VoxelShape SHAPE_S2=Block.createCuboidShape(0,0,14,32,16,16);
    public static final VoxelShape SHAPE_E2=Block.createCuboidShape(14,0,-16,16,16,16);
    public static final VoxelShape SHAPE_W2=Block.createCuboidShape(0,0,0,2,16,32);
    public final int type;
    public MetroCorridorFull(Settings settings,int type) {
        super(settings);
        this.type=type;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {

        Direction slabType = state.get(OmniBlock.TYPE);
        switch (slabType) {
            case SOUTH -> {
                return type==0?SHAPE_S:type==1?SHAPE_S1:SHAPE_S2;
            }
            case WEST -> {
                return type==0?SHAPE_W:type==1?SHAPE_W1:SHAPE_W2;
            }
            case EAST -> {
                return type==0?SHAPE_E:type==1?SHAPE_E1:SHAPE_E2;
            }
        }
        return type==0?SHAPE_N:type==1?SHAPE_N1:SHAPE_N2;
    }

}
