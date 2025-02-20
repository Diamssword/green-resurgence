package com.diamssword.greenresurgence.blocks;

import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class LayerModelBlock extends Block {
    private IntProperty LAYERS;
    public static final DirectionProperty FACING= Properties.HORIZONTAL_FACING;
    protected final VoxelShape[] LAYERS_TO_SHAPE;
    public LayerModelBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(LAYERS, 1));
       // LAYERS=IntProperty.of("layers", 1, layers);
        LAYERS_TO_SHAPE=new VoxelShape[layers()];
        for(int i=0;i<layers();i++)
        {
            LAYERS_TO_SHAPE[i]=Block.createCuboidShape(2,0,2,14,1+(((double) i /(double)layers())*15),14);
        }
    }
    public abstract int layers();
    private IntProperty genProp()
    {
        if(LAYERS ==null)
            LAYERS=IntProperty.of("layers", 1, layers());
        return LAYERS;
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        switch (type) {
            case LAND:
                return (Integer)state.get(LAYERS) < 5;
            default:
                return false;
        }
    }
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
    }
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return LAYERS_TO_SHAPE[(Integer)state.get(LAYERS)-1];
    }

    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return LAYERS_TO_SHAPE[(Integer)state.get(LAYERS) - 1];
    }

    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return LAYERS_TO_SHAPE[(Integer)state.get(LAYERS)-1];
    }

    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return LAYERS_TO_SHAPE[(Integer)state.get(LAYERS)-1];
    }

    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1f;
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.down());
        if(blockState.isOf(this) && (Integer) blockState.get(LAYERS) != layers()) {
            return false;
        }
        return super.canPlaceAt(state,world,pos);

    }
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        int i = (Integer)state.get(LAYERS);
        if (context.getStack().isOf(this.asItem()) && i < layers()) {
            if (context.canReplaceExisting()) {
                return context.getSide() == Direction.UP;
            } else {
                return true;
            }
        } else {
            return i == 1;
        }
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState.isOf(this)) {
            int i = (Integer)blockState.get(LAYERS);
            return (BlockState)blockState.with(LAYERS, Math.min(layers(), i + 1));
        } else {
            return super.getPlacementState(ctx).with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
        }
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(genProp(),FACING);
    }
}
