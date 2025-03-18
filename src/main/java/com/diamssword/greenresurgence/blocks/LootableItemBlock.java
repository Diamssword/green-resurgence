package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.MBlockEntities;
import com.diamssword.greenresurgence.blockEntities.GeneratorBlockEntity;
import com.diamssword.greenresurgence.blockEntities.ItemBlockEntity;
import com.diamssword.greenresurgence.blockEntities.LootableItemBlockEntity;
import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.containers.IGridContainer;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
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
import net.minecraft.item.BundleItem;
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
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LootableItemBlock extends BlockWithEntity implements Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final DirectionProperty FACING=Properties.HORIZONTAL_FACING;
    public static final VoxelShape SHAPE= Block.createCuboidShape(0,0,0,16,5,16);
    public LootableItemBlock(Settings settings) {
        super(settings);
        this.getDefaultState().with(FACING,Direction.NORTH).with(WATERLOGGED,false);
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, MBlockEntities.LOOT_ITEM_BLOCK, LootableItemBlockEntity::tick);
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
        if(nbtCompound !=null) {
            if(nbtCompound.contains("item"))
            {
                var item= ItemStack.fromNbt(nbtCompound.getCompound("item"));
                tooltip.add(Text.literal("Item: ").append(item.getName()));
            }
            if (nbtCompound.contains("positionX")) {
                tooltip.add(Text.literal("Position: "+nbtCompound.getInt("positionX")+","+nbtCompound.getInt("positionY")+","+nbtCompound.getInt("positionZ")));
            }
            if (nbtCompound.contains("rotationX")) {
                tooltip.add(Text.literal("Rotation: "+nbtCompound.getInt("rotationX")+","+nbtCompound.getInt("rotationY")+","+nbtCompound.getInt("rotationZ")));
            }
            if (nbtCompound.contains("size.xml")) {
                tooltip.add(Text.literal("Taille: "+nbtCompound.getDouble("size")));
            }
        }

    }
    @Override
    @Deprecated
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return this.getDefaultState().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER).with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
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
        return new LootableItemBlockEntity(pos,state);
    }
    public LootableItemBlockEntity getBlockEntity(BlockPos pos, BlockView world)
    {
        return (LootableItemBlockEntity) world.getBlockEntity(pos);
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
            Containers.createHandler(player,pos,(sync,inv,p1)-> new ItemBlock.ScreenHandler( sync,player, LootableItemBlock.this.getBlockEntity(pos,world).getContainer()));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
    @Deprecated
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }
}
