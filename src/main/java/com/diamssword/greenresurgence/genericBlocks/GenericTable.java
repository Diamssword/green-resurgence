package com.diamssword.greenresurgence.genericBlocks;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Map;

public class GenericTable extends Block implements Waterloggable {
	protected static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES
			.entrySet()
			.stream()
			.filter(entry -> entry.getKey().getAxis().isHorizontal())
			.collect(Util.toMap());
	private final GenericBlockSet.Transparency transparency;
	private final VoxelShape boxe;
	private final boolean noHitbox;
	private final float damage;
	public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
	public static final BooleanProperty EAST = ConnectingBlock.EAST;
	public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
	public static final BooleanProperty WEST = ConnectingBlock.WEST;
	public static final BooleanProperty NORTH_EAST = BooleanProperty.of("north_east");
	public static final BooleanProperty NORTH_WEST = BooleanProperty.of("north_west");
	public static final BooleanProperty SOUTH_EAST = BooleanProperty.of("south_east");
	public static final BooleanProperty SOUTH_WEST = BooleanProperty.of("south_west");
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	public GenericTable(Settings settings, GenericBlockSet.GenericBlockProp props) {
		super(settings);
		boxe = props.hitbox.shape;
		this.transparency = props.transparency;
		this.noHitbox = !props.solid;
		this.damage = props.damage;
		this.setDefaultState(
				this.stateManager
						.getDefaultState()
						.with(NORTH, Boolean.FALSE)
						.with(EAST, Boolean.FALSE)
						.with(SOUTH, Boolean.FALSE)
						.with(WEST, Boolean.FALSE)
						.with(NORTH_EAST, Boolean.FALSE)
						.with(NORTH_WEST, Boolean.FALSE)
						.with(SOUTH_EAST, Boolean.FALSE)
						.with(SOUTH_WEST, Boolean.FALSE)
						.with(WATERLOGGED, Boolean.FALSE)
		);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (noHitbox)
			return VoxelShapes.empty();
		return super.getCollisionShape(state, world, pos, context);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return boxe;
	}

	@Override
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		if (transparency == GenericBlockSet.Transparency.UNDEFINED || transparency == GenericBlockSet.Transparency.OPAQUE)
			return super.getAmbientOcclusionLightLevel(state, world, pos);
		return 1.0F;
	}

	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (damage > 0)
			entity.damage(world.getDamageSources().cactus(), damage);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, WEST, SOUTH, NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST, WATERLOGGED);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockView blockView = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		BlockPos blockPos2 = blockPos.north();
		BlockPos blockPos3 = blockPos.east();
		BlockPos blockPos4 = blockPos.south();
		BlockPos blockPos5 = blockPos.west();
		BlockState blockState = blockView.getBlockState(blockPos2);
		BlockState blockState2 = blockView.getBlockState(blockPos3);
		BlockState blockState3 = blockView.getBlockState(blockPos4);
		BlockState blockState4 = blockView.getBlockState(blockPos5);

		BlockState blockState5 = blockView.getBlockState(blockPos.north().east());
		BlockState blockState6 = blockView.getBlockState(blockPos3.north().west());
		BlockState blockState7 = blockView.getBlockState(blockPos4.south().east());
		BlockState blockState8 = blockView.getBlockState(blockPos5.south().west());
		return super.getPlacementState(ctx)
				.with(NORTH, this.canConnectToFence(blockState))
				.with(EAST, this.canConnectToFence(blockState2))
				.with(SOUTH, this.canConnectToFence(blockState3))
				.with(WEST, this.canConnectToFence(blockState4))

				.with(NORTH_EAST, this.canConnectToFence(blockState5))
				.with(NORTH_WEST, this.canConnectToFence(blockState6))
				.with(SOUTH_EAST, this.canConnectToFence(blockState7))
				.with(SOUTH_WEST, this.canConnectToFence(blockState8))
				.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
	}

	private boolean canConnectToFence(BlockState state) {
		return state.getBlock() instanceof GenericTable;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		if (direction.getAxis().getType() == Direction.Type.HORIZONTAL) {
			var st = state.with(FACING_PROPERTIES.get(direction), this.canConnectToFence(neighborState));
			switch (direction) {
				case SOUTH ->
						st = st.with(SOUTH_WEST, this.canConnectToFence(world.getBlockState(pos.south().west()))).with(SOUTH_EAST, this.canConnectToFence(world.getBlockState(pos.south().east())));
				case NORTH ->
						st = st.with(NORTH_WEST, this.canConnectToFence(world.getBlockState(pos.north().west()))).with(NORTH_EAST, this.canConnectToFence(world.getBlockState(pos.north().east())));
				case EAST ->
						st = st.with(NORTH_EAST, this.canConnectToFence(world.getBlockState(pos.north().east()))).with(SOUTH_EAST, this.canConnectToFence(world.getBlockState(pos.south().east())));
				case WEST ->
						st = st.with(NORTH_WEST, this.canConnectToFence(world.getBlockState(pos.north().west()))).with(SOUTH_WEST, this.canConnectToFence(world.getBlockState(pos.south().west())));
			}
			return st;
		}
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				return state.with(NORTH, state.get(SOUTH))
						.with(EAST, state.get(WEST))
						.with(SOUTH, state.get(NORTH))
						.with(WEST, state.get(EAST));
			case COUNTERCLOCKWISE_90:
				return state.with(NORTH, state.get(EAST))
						.with(EAST, state.get(SOUTH))
						.with(SOUTH, state.get(WEST))
						.with(WEST, state.get(NORTH));
			case CLOCKWISE_90:
				return state.with(NORTH, state.get(WEST))
						.with(EAST, state.get(NORTH))
						.with(SOUTH, state.get(EAST))
						.with(WEST, state.get(SOUTH));
			default:
				return state;
		}
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
			case FRONT_BACK:
				return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
			default:
				return super.mirror(state, mirror);
		}
	}
}
