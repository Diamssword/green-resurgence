package com.diamssword.greenresurgence.genericBlocks;

import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class OmniSlabBlock extends Block  implements Waterloggable {
    public static final EnumProperty<Direction> TYPE = Properties.FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    protected static final VoxelShape TOP_SHAPE = Block.createCuboidShape(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 8.0);
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 8.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 8.0, 16.0, 16.0);
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(8.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape BOTTOM_SHAPE1 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    protected static final VoxelShape TOP_SHAPE1 = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape NORTH_SHAPE1 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    protected static final VoxelShape SOUTH_SHAPE1 = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape WEST_SHAPE1 = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    protected static final VoxelShape EAST_SHAPE1 = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    public final boolean carpet;
    public final boolean passtrough;
    public OmniSlabBlock(AbstractBlock.Settings settings,boolean carpet, boolean passthrough) {
        super(settings);
        this.carpet =carpet;
        this.passtrough=passthrough;
        this.setDefaultState((BlockState)((BlockState)this.getDefaultState().with(TYPE, Direction.NORTH)).with(WATERLOGGED, false));
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TYPE, WATERLOGGED);
    }
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return !this.passtrough ? state.getOutlineShape(world, pos) : VoxelShapes.empty();
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {

        Direction slabType = state.get(TYPE);
        switch (slabType) {

            case DOWN -> {
                return carpet ?BOTTOM_SHAPE1: BOTTOM_SHAPE;
            }
            case UP -> {
                return carpet ?TOP_SHAPE1: TOP_SHAPE;
            }
            case NORTH -> {
                return carpet ?NORTH_SHAPE1:NORTH_SHAPE;
            }
            case SOUTH -> {
                return carpet ?SOUTH_SHAPE1:SOUTH_SHAPE;
            }
            case WEST -> {
                return carpet ?WEST_SHAPE1:WEST_SHAPE;
            }
            case EAST -> {
                return carpet ?EAST_SHAPE1:EAST_SHAPE;
            }
        }
        return TOP_SHAPE;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
        return this.getDefaultState().with(TYPE, ctx.getPlayerLookDirection()).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }



    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
            return Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState);
    }

    @Override
    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
            return Waterloggable.super.canFillWithFluid(world, pos, state, fluid);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED).booleanValue()) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        switch (type) {
            case LAND, AIR: {
                return false;
            }
            case WATER: {
                return world.getFluidState(pos).isIn(FluidTags.WATER);
            }
        }
        return false;
    }
    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(TYPE, rotation.rotate(state.get(TYPE)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(TYPE)));
    }
}

