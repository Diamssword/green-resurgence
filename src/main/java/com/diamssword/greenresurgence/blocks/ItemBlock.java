package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.blockEntities.ItemBlockEntity;
import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.containers.IGridContainer;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class ItemBlock extends Block implements BlockEntityProvider,Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final DirectionProperty FACING=Properties.HORIZONTAL_FACING;
    public static BooleanProperty COLLISION= BooleanProperty.of("collision");
    public ItemBlock(Settings settings) {
        super(settings);
        this.getDefaultState().with(COLLISION,true).with(FACING,Direction.NORTH).with(WATERLOGGED,false);
    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return this.getDefaultState().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER).with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(COLLISION)?VoxelShapes.fullCube():VoxelShapes.empty();
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(COLLISION,FACING, WATERLOGGED);
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
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ItemBlockEntity(pos,state);
    }
    public ItemBlockEntity getBlockEntity(BlockPos pos, BlockView world)
    {
        return (ItemBlockEntity) world.getBlockEntity(pos);
    }
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(!world.isClient && player.isCreative())
        {
            createHandler(player,pos,world);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
    @Deprecated
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }
    public void createHandler(PlayerEntity player,BlockPos pos,World world)
    {
        NamedScreenHandlerFactory screen=new NamedScreenHandlerFactory() {
            @Nullable
            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                ScreenHandler scr=new ScreenHandler( syncId,playerInventory,ItemBlock.this.getBlockEntity(pos,world).getContainer());
                scr.setPos(pos);
                return scr;
            }
            @Override
            public Text getDisplayName() {
                return Text.of("Inventaire");
            }
        };
        player.openHandledScreen(screen);
    }
    public static class ScreenHandler extends MultiInvScreenHandler {

        public ScreenHandler(int syncId, PlayerInventory playerInventory) {
            super(syncId, playerInventory);
        }

        public ScreenHandler( int syncId, PlayerInventory playerInventory, IGridContainer... inventories) {
            super( syncId, playerInventory, inventories);
        }

        @Override
        public ScreenHandlerType<ScreenHandler> type() {
            return Containers.ITEMBLOCK;
        }

    }
}
