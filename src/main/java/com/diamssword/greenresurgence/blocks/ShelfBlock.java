package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.MBlockEntities;
import com.diamssword.greenresurgence.blockEntities.LootableItemBlockEntity;
import com.diamssword.greenresurgence.blockEntities.LootableShelfEntity;
import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.containers.IGridContainer;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import com.diamssword.greenresurgence.genericBlocks.GenericPillar;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShelfBlock extends BlockWithEntity implements Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final DirectionProperty FACING=Properties.HORIZONTAL_FACING;
    public static final BooleanProperty BOTTOM=Properties.BOTTOM;
    private static final VoxelShape SHAPE= Block.createCuboidShape(0,0,0,16,3,16);
    private static VoxelShape[] SHAPES={

            VoxelShapes.union(SHAPE,Block.createCuboidShape(0,0,15,16,16,16)),
            VoxelShapes.union(SHAPE, Block.createCuboidShape(0,0,0,16,16,1)),
            VoxelShapes.union(SHAPE,Block.createCuboidShape(15,0,0,16,16,16)),
            VoxelShapes.union(SHAPE,Block.createCuboidShape(0,0,0,1,16,16)),
    };
    static {

        ;
    }
    public ShelfBlock(Settings settings) {
        super(settings);
        this.getDefaultState().with(FACING,Direction.NORTH).with(WATERLOGGED,false);
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, MBlockEntities.LOOTABLE_SHELF, LootableShelfEntity::tick);
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
        if(nbtCompound !=null) {
            if(nbtCompound.contains("item")) {
                var item = ItemStack.fromNbt(nbtCompound.getCompound("item"));
                tooltip.add(Text.literal("Item: ").append(item.getName()));
            }
        }

    }
    @Override
    @Deprecated
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return  SHAPES[state.get(FACING).getId()-2];
    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        var bst=ctx.getWorld().getBlockState(ctx.getBlockPos().down());
        var bot=true;
        if(!bst.isSideSolid(ctx.getWorld(),ctx.getBlockPos().down(),Direction.UP,SideShapeType.FULL) || bst.getBlock() == this)
            bot=false;
        return this.getDefaultState().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER).with(BOTTOM,bot).with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, BOTTOM,WATERLOGGED);
    }
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if(direction==Direction.DOWN)
        {
            var bot=true;
            if(!neighborState.isSideSolid(world,pos.down(),Direction.UP,SideShapeType.FULL) || neighborState.getBlock() == this)
                bot=false;
            return  super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos).with(BOTTOM,bot);
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
        return new LootableShelfEntity(pos,state);
    }
    public LootableShelfEntity getBlockEntity(BlockPos pos, BlockView world)
    {
        return (LootableShelfEntity) world.getBlockEntity(pos);
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
            Containers.createHandler(player,pos,(sync,inv,p1)-> new ShelfBlock.ScreenHandler( sync,inv, ShelfBlock.this.getBlockEntity(pos,world).getContainer()));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
    @Deprecated
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    public static class ScreenHandler extends MultiInvScreenHandler {

        public ScreenHandler(int syncId, PlayerInventory playerInventory) {
            super(syncId, playerInventory);
        }

        public ScreenHandler( int syncId, PlayerInventory playerInventory, IGridContainer... inventories) {
            super( syncId, playerInventory, inventories);
        }

        @Override
        public ScreenHandlerType<ShelfBlock.ScreenHandler> type() {
            return Containers.ITEMBLOCKSIMPLE;
        }
    }
}
