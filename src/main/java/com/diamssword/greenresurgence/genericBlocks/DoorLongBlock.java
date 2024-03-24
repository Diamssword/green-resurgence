package com.diamssword.greenresurgence.genericBlocks;

import net.minecraft.block.*;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class DoorLongBlock extends DoorBlock {
    public static final BooleanProperty TOP = BooleanProperty.of("top");
    protected static final VoxelShape CULLING = Block.createCuboidShape(13.0, -16.0, 0.0, 16.0, 32.0, 16.0);
    public DoorLongBlock(Settings settings, BlockSetType blockSetType) {
        super(settings, blockSetType);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(OPEN, false).with(HINGE, DoorHinge.LEFT).with(POWERED, false).with(HALF, DoubleBlockHalf.LOWER).with(TOP,false));

    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        if(state.get(TOP)|| state.get(HALF)==DoubleBlockHalf.LOWER)
            return BlockRenderType.INVISIBLE;
        return BlockRenderType.MODEL;

    }
    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return CULLING;
    }
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(), (BlockState)state.with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_ALL);
        world.setBlockState(pos.up(2), (BlockState)state.with(HALF, DoubleBlockHalf.UPPER).with(TOP,true), Block.NOTIFY_ALL);
    }
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf doubleBlockHalf = state.get(HALF);
        boolean doubleBlockTop = state.get(TOP);
        if(doubleBlockTop)
        {
            if(direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP))
            {
                if (neighborState.isOf(this) && !neighborState.get(TOP)) {
                    return (BlockState)((BlockState)((BlockState)((BlockState)state.with(FACING, neighborState.get(FACING))).with(OPEN, neighborState.get(OPEN))).with(HINGE, neighborState.get(HINGE))).with(POWERED, neighborState.get(POWERED));
                }
                return Blocks.AIR.getDefaultState();
            }
        }
        else
        {
            if(direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.UPPER == (direction == Direction.UP))
            {
                if (neighborState.isOf(this) && neighborState.get(TOP)) {
                    return (BlockState)((BlockState)((BlockState)((BlockState)state.with(FACING, neighborState.get(FACING))).with(OPEN, neighborState.get(OPEN))).with(HINGE, neighborState.get(HINGE))).with(POWERED, neighborState.get(POWERED));
                }
                return Blocks.AIR.getDefaultState();
            }
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING, OPEN, HINGE, POWERED,TOP);

    }
    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }


}
