package com.diamssword.greenresurgence.genericBlocks;

import net.minecraft.block.*;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class DoorDoubleBlock extends DoorBlock implements Waterloggable {
	protected static final VoxelShape CULLING = Block.createCuboidShape(13.0, -16.0, 0.0, 16.0, 16.0, 16.0);

	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	public DoorDoubleBlock(Settings settings, BlockSetType blockSetType) {
		super(settings, blockSetType);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(OPEN, false).with(HINGE, DoorHinge.LEFT).with(POWERED, false).with(HALF, DoubleBlockHalf.LOWER).with(WATERLOGGED, false));

	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		if (state.get(HALF) == DoubleBlockHalf.UPPER)
			return BlockRenderType.INVISIBLE;
		return BlockRenderType.MODEL;

	}

	@Override
	@Nullable
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockPos blockPos = ctx.getBlockPos();
		World world = ctx.getWorld();
		if (blockPos.getY() < world.getTopY() - 1 && world.getBlockState(blockPos.up()).canReplace(ctx)) {

			FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
			boolean bl = world.isReceivingRedstonePower(blockPos) || world.isReceivingRedstonePower(blockPos.up());
			return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite()).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER)
					.with(HINGE, getHinge(ctx)).with(POWERED, bl).with(OPEN, bl).with(HALF, DoubleBlockHalf.LOWER);
		} else {
			return null;
		}
	}

	private DoorHinge getHinge(ItemPlacementContext ctx) {

		BlockPos blockPos = ctx.getBlockPos();
		Vec3d vec3d = ctx.getHitPos();
		double d = vec3d.x - (double) blockPos.getX();
		double e = vec3d.z - (double) blockPos.getZ();
		var a = ctx.getHorizontalPlayerFacing().getDirection() == Direction.AxisDirection.NEGATIVE ? DoorHinge.LEFT : DoorHinge.RIGHT;
		var b = ctx.getHorizontalPlayerFacing().getDirection() == Direction.AxisDirection.NEGATIVE ? DoorHinge.RIGHT : DoorHinge.LEFT;
		if (ctx.getHorizontalPlayerFacing().getAxis() == Direction.Axis.X)
			return e > 0.5f ? b : a;
		else
			return d > 0.5f ? a : b;
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		if (state.get(WATERLOGGED)) {
			return Fluids.WATER.getStill(false);
		}
		return super.getFluidState(state);
	}

	@Override
	public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
		return CULLING;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {

		world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER).with(WATERLOGGED, world.getFluidState(pos.up()).getFluid() == Fluids.WATER), Block.NOTIFY_ALL);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		DoubleBlockHalf doubleBlockHalf = state.get(HALF);
		if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.UPPER == (direction == Direction.UP)) {
			return state;
		}
		if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
			if (neighborState.isOf(this) && neighborState.get(HALF) != doubleBlockHalf) {
				return state.with(FACING, neighborState.get(FACING)).with(OPEN, neighborState.get(OPEN)).with(HINGE, neighborState.get(HINGE)).with(POWERED, neighborState.get(POWERED));
			}
			return Blocks.AIR.getDefaultState();
		}
		if (doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
			return Blocks.AIR.getDefaultState();
		}
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(HALF, FACING, OPEN, HINGE, POWERED, WATERLOGGED);

	}

	@Override
	public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}


}
