package com.diamssword.greenresurgence.genericBlocks;

import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class ConnectedBlock extends GlazedTerracottaBlock implements Waterloggable {
	private final boolean horizontal;
	public static final BooleanProperty LEFT = BooleanProperty.of("left");
	public static final BooleanProperty RIGHT = BooleanProperty.of("right");
	public static final BooleanProperty BOTTOM = BooleanProperty.of("bottom");
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	public ConnectedBlock(Settings settings, boolean horizontal) {
		super(settings);
		this.horizontal = horizontal;
		var st = this.getDefaultState();
		if (horizontal)
			st = st.with(LEFT, false).with(RIGHT, false);
		else
			st = st.with(BOTTOM, false);
		this.setDefaultState(st.with(WATERLOGGED, false));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		if (horizontal)
			builder.add(LEFT, RIGHT, WATERLOGGED);
		else
			builder.add(BOTTOM, WATERLOGGED);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		World blockView = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		var facing = ctx.getHorizontalPlayerFacing().getOpposite();
		boolean flag = false;
		BlockState bl = blockView.getBlockState(blockPos.offset(Direction.DOWN));
		if (bl.getBlock() == this) {
			if (bl.get(HorizontalFacingBlock.FACING) == facing)
				flag = true;
		}
		return this.getDefaultState().with(HorizontalFacingBlock.FACING, facing).with(BOTTOM, flag).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		boolean d = state.get(BOTTOM);
		if (direction == Direction.DOWN) {
			if (neighborState.getBlock() == this) {
				var face = neighborState.get(HorizontalFacingBlock.FACING);
				if (face == state.get(HorizontalFacingBlock.FACING)) {
					d = true;
				}
			} else {
				d = false;
			}
		}
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos).with(BOTTOM, d);
	}
}
