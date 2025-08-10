package com.diamssword.greenresurgence.genericBlocks;

import com.google.common.collect.ImmutableMap;
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
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AllDirectionVine extends Block implements Waterloggable {
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final BooleanProperty DOWN = ConnectingBlock.DOWN;
	public static final BooleanProperty UP = ConnectingBlock.UP;
	public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
	public static final BooleanProperty EAST = ConnectingBlock.EAST;
	public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
	public static final BooleanProperty WEST = ConnectingBlock.WEST;
	public static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES
			.entrySet()
			.stream()
			.collect(Util.toMap());
	private static final VoxelShape UP_SHAPE = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape DOWN_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
	private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
	private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
	private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
	private final Map<BlockState, VoxelShape> shapesByState;
	private final GenericBlockSet.Transparency transparency;
	private final boolean noHitbox;
	private final float damage;

	public AllDirectionVine(AbstractBlock.Settings settings, GenericBlockSet.GenericBlockProp props) {
		super(settings);
		this.setDefaultState(
				this.stateManager
						.getDefaultState()
						.with(UP, Boolean.FALSE)
						.with(DOWN, Boolean.FALSE)
						.with(NORTH, Boolean.FALSE)
						.with(EAST, Boolean.FALSE)
						.with(SOUTH, Boolean.FALSE)
						.with(WEST, Boolean.FALSE)
						.with(WATERLOGGED, false)
		);
		this.noHitbox = !props.solid;
		this.shapesByState = ImmutableMap.copyOf(this.stateManager.getStates().stream().collect(Collectors.toMap(Function.identity(), AllDirectionVine::getShapeForState)));
		this.transparency = props.transparency;
		this.damage = props.damage;
	}

	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (damage > 0)
			entity.damage(world.getDamageSources().cactus(), damage);
	}

	private static VoxelShape getShapeForState(BlockState state) {
		VoxelShape voxelShape = VoxelShapes.empty();
		if (state.get(UP)) {
			voxelShape = UP_SHAPE;
		}
		if (state.get(DOWN)) {
			voxelShape = VoxelShapes.union(voxelShape, DOWN_SHAPE);
		}
		if (state.get(NORTH)) {
			voxelShape = VoxelShapes.union(voxelShape, SOUTH_SHAPE);
		}
		if (state.get(SOUTH)) {
			voxelShape = VoxelShapes.union(voxelShape, NORTH_SHAPE);
		}

		if (state.get(EAST)) {
			voxelShape = VoxelShapes.union(voxelShape, WEST_SHAPE);
		}

		if (state.get(WEST)) {
			voxelShape = VoxelShapes.union(voxelShape, EAST_SHAPE);
		}
		return voxelShape.isEmpty() ? VoxelShapes.fullCube() : voxelShape;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return this.shapesByState.get(state);
	}

	@Override
	public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return this.hasAdjacentBlocks(this.getPlacementShape(state, world, pos));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (noHitbox)
			return VoxelShapes.empty();
		return super.getCollisionShape(state, world, pos, context);
	}

	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		if (transparency == GenericBlockSet.Transparency.UNDEFINED || transparency == GenericBlockSet.Transparency.OPAQUE)
			return super.getAmbientOcclusionLightLevel(state, world, pos);
		return 1.0F;
	}

	private boolean hasAdjacentBlocks(BlockState state) {
		return this.getAdjacentBlockCount(state) > 0;
	}

	private int getAdjacentBlockCount(BlockState state) {
		int i = 0;

		for (BooleanProperty booleanProperty : FACING_PROPERTIES.values()) {
			if (state.get(booleanProperty)) {
				i++;
			}
		}

		return i;
	}

	private boolean shouldHaveSide(BlockView world, BlockPos pos, Direction side) {
		BlockPos blockPos = pos.offset(side);
		if (shouldConnectTo(world, blockPos, side)) {
			return true;
		} else if (side.getAxis() == Direction.Axis.Y) {
			return false;
		} else {
			BooleanProperty booleanProperty = FACING_PROPERTIES.get(side);
			BlockState blockState = world.getBlockState(pos.up());
			return blockState.isOf(this) && blockState.get(booleanProperty);
		}
	}

	public static boolean shouldConnectTo(BlockView world, BlockPos pos, Direction direction) {
		return MultifaceGrowthBlock.canGrowOn(world, direction, pos, world.getBlockState(pos));
	}

	private BlockState getPlacementShape(BlockState state, BlockView world, BlockPos pos) {
		BlockPos blockPos = pos.up();
		BlockState blockState = null;
		for (Direction direction : Direction.values()) {
			BooleanProperty booleanProperty = getFacingProperty(direction);
			if (state.get(booleanProperty)) {
				boolean bl = this.shouldHaveSide(world, pos, direction);
				if (!bl) {
					if (blockState == null) {
						blockState = world.getBlockState(blockPos);
					}

					bl = blockState.isOf(this) && blockState.get(booleanProperty);
				}

				state = state.with(booleanProperty, bl);
			}
		}
		return state;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
			BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if (state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		BlockState blockState = this.getPlacementShape(state, world, pos);
		return !this.hasAdjacentBlocks(blockState) ? Blocks.AIR.getDefaultState() : blockState;
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		if (state.get(WATERLOGGED)) {
			return Fluids.WATER.getStill(false);
		}
		return super.getFluidState(state);
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
		if (context.getStack().getItem() == this.asItem())
			return this.getAdjacentBlockCount(blockState) < FACING_PROPERTIES.size();
		return super.canReplace(state, context);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
		boolean bl = blockState.isOf(this);
		BlockState blockState2 = bl ? blockState : this.getDefaultState();
		for (Direction direction : ctx.getPlacementDirections()) {
			BooleanProperty booleanProperty = getFacingProperty(direction);
			boolean bl2 = bl && blockState.get(booleanProperty);
			if (!bl2 && this.shouldHaveSide(ctx.getWorld(), ctx.getBlockPos(), direction)) {
				return blockState2.with(booleanProperty, Boolean.TRUE);
			}
		}
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		return bl ? blockState2.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER) : null;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST, WATERLOGGED);
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

	public static BooleanProperty getFacingProperty(Direction direction) {
		return FACING_PROPERTIES.get(direction);
	}
}
