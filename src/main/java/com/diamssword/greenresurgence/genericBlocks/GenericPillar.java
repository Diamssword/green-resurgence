package com.diamssword.greenresurgence.genericBlocks;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
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

public class GenericPillar extends GlazedTerracottaBlock implements IChairable, Waterloggable {

	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	private final GenericBlockSet.Transparency transparency;
	private final VoxelShape[] boxes;
	private final boolean noHitbox;
	private final float damage;
	private final boolean isChair;
	private final float chairLvl;

	public GenericPillar(Settings settings, GenericBlockSet.GenericBlockProp props) {
		super(settings);
		boxes = CompileRotatedVoxels(props.hitbox.shape, true, props.hitbox.needRotate, props.hitbox.horizontal);
		this.transparency = props.transparency;
		this.noHitbox = !props.solid;
		this.damage = props.damage;
		this.isChair = props.isSeat;
		this.chairLvl = props.seatLevel;
		this.setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, false));
	}

	@Override
	@Nullable
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		return super.getPlacementState(ctx).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		if (state.get(WATERLOGGED)) {
			return Fluids.WATER.getStill(false);
		}
		return super.getFluidState(state);
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
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (noHitbox)
			return VoxelShapes.empty();
		return super.getCollisionShape(state, world, pos, context);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return boxes[state.get(FACING).getId()];
	}

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
	public float sittingHeight() {
		return chairLvl;
	}

	@Deprecated
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient && canUse()) {
			this.sit(player, pos);
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}

	@Override
	public boolean canUse() {
		return isChair;
	}

	public static Box rotateBox(Direction dir, Box base) {
		switch (dir) {
			case DOWN -> {
				return base;
			}
			case UP -> {
				return new Box(base.minX, 1f - base.maxY, base.minZ, base.maxX, 1f - base.minY, base.maxZ);
			}
			case NORTH -> {
				return new Box(base.minX, base.minZ, base.minY, base.maxX, base.maxZ, base.maxY);
			}
			case SOUTH -> {
				return new Box(base.minX, 1f - base.maxZ, 1f - base.maxY, base.maxX, 1f - base.minZ, 1f - base.minY);
			}
			case WEST -> {
				return new Box(base.minY, base.minX, base.minZ, base.maxY, base.maxX, base.maxZ);
			}
			case EAST -> {
				return new Box(1f - base.maxY, 1f - base.maxX, base.minZ, 1f - base.minY, 1f - base.minX, base.maxZ);
			}
		}
		return base;
	}

	public static Box rotateBoxHorizontal(Direction dir, Box base) {
		switch (dir) {
			case NORTH -> {
				return new Box(base.minX, base.minY, base.minZ, base.maxX, base.maxY, base.maxZ);
			}
			case SOUTH -> {
				return new Box(1f - base.minX, base.minY, 1f - base.minZ, 1f - base.maxX, base.maxY, 1f - base.maxZ);
			}
			case WEST -> {
				return new Box(base.minZ, base.minY, base.minX, base.maxZ, base.maxY, base.maxX);
			}
			case EAST -> {
				return new Box(1f - base.minZ, base.minY, 1f - base.minX, 1f - base.maxZ, base.maxY, 1f - base.maxX);
			}
		}
		return base;
	}

	public static VoxelShape rotateVoxel(Direction dir, VoxelShape base, boolean horizontalOnly) {

		VoxelShape n = null;

		for (Box b : base.getBoundingBoxes()) {
			var b1 = VoxelShapes.cuboid(horizontalOnly ? rotateBoxHorizontal(dir, b) : rotateBox(dir, b));
			if (n == null)
				n = b1;
			else
				n = VoxelShapes.union(n, b1);
		}
		if (n != null)
			n = n.simplify();
		return n;
	}

	public static VoxelShape[] CompileRotatedVoxels(VoxelShape base, boolean inverted, boolean needed, boolean horizontalOnly) {
		var res = new VoxelShape[6];
		for (Direction dir : Direction.values()) {
			if (!needed)
				res[dir.getId()] = base;
			else if (inverted)
				res[dir.getId()] = rotateVoxel(dir.getOpposite(), base, horizontalOnly);
			else
				res[dir.getId()] = rotateVoxel(dir, base, horizontalOnly);
		}
		return res;
	}

}
