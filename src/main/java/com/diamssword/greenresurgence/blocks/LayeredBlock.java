package com.diamssword.greenresurgence.blocks;

import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class LayeredBlock extends Block implements Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected static final VoxelShape[] LAYERS_TO_SHAPE = new VoxelShape[]{VoxelShapes.empty(), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)};

    private static final VoxelShape[][] SHAPES=new VoxelShape[5][6];
    public static final IntProperty HEIGHT = IntProperty.of("layers", 1, 4);
    public static final DirectionProperty SIDE = Properties.FACING;
    static {
        for(int i=0;i<5;i++)
        {
            SHAPES[i]=new VoxelShape[6];
            for(Direction d : Direction.values())
            {
                SHAPES[i][d.getId()]=VoxelShapes.combine(LAYERS_TO_SHAPE[i],getShapeForFacing(d), BooleanBiFunction.AND).simplify();
            }

        }

    }

    public LayeredBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(HEIGHT, 4).with(SIDE, Direction.UP).with(WATERLOGGED,false));

    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return this.getDefaultState().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }
    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }
    public static VoxelShape getShapeForFacing(Direction dir)
    {
        switch (dir)
        {

            case DOWN, UP -> {
                return Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
            }
            case NORTH -> {
                return Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 8.0);
            }
            case SOUTH -> {
                return Block.createCuboidShape(0.0, 0.0, 8.0, 16.0, 16.0, 16.0);
            }
            case WEST -> {
                return Block.createCuboidShape(0.0, 0.0, 0.0, 8.0, 16.0, 16.0);
            }
            case EAST -> {
                return Block.createCuboidShape(8.0, 0.0, 0.0, 16.0, 16.0, 16.0);
            }
        }
        return Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        // With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return BlockRenderType.INVISIBLE;
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {

        return SHAPES[state.get(HEIGHT)][state.get(SIDE).getId()];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(HEIGHT)][ state.get(SIDE).getId()];
    }

    @Override
    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return SHAPES[state.get(HEIGHT)][state.get(SIDE).getId()];
    }

    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(HEIGHT)][state.get(SIDE).getId()];
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1f;
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HEIGHT,SIDE,WATERLOGGED);
    }
    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(SIDE, rotation.rotate(state.get(SIDE)));

    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(SIDE)));
    }
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }
}
