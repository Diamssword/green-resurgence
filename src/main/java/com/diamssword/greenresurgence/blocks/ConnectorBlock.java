package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.MBlocks;
import com.diamssword.greenresurgence.blockEntities.ConnectorBlockEntity;
import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ConnectorBlock extends Block  implements BlockEntityProvider,IDisplayOffset {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private static final VoxelShape S_S=Block.createCuboidShape(4,10,-4,12,14,10);
    private static final VoxelShape N_S= Block.createCuboidShape(4,10,6,12,14,20);
    private static final VoxelShape W_S=Block.createCuboidShape(6,10,4,20,14,12);
    private static final VoxelShape E_S=Block.createCuboidShape(-4,10,4,10,14,12);
    public ConnectorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING))
        {
            case SOUTH -> {
                return S_S;
            }
            case WEST -> {
                return W_S;
            }
            case EAST -> {
                return E_S;
            }
            default->{
               return N_S;
            }
        }
    }
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ConnectorBlockEntity(pos,state);
    }
    public ConnectorBlockEntity getBlockEntity(BlockPos pos, BlockView world)
    {
        return (ConnectorBlockEntity) world.getBlockEntity(pos);
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

    @Override
    public Vec3d getOffset(BlockState st, World w) {
        return new Vec3d(0,0.25,0);
    }

    @Override
    public float getScale(BlockState st, World w) {
        return 0.5f;
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
