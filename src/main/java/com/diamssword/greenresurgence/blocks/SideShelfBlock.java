package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.containers.Containers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class SideShelfBlock extends ShelfBlock {

	public enum Model implements StringIdentifiable {
		SINGLE("single"),
		LEFT("left"),
		RIGHT("right"),
		CENTER("center");
		private final String name;

		Model(String name) {
			this.name = name;
		}

		public String toString() {
			return this.name;
		}

		public String asString() {
			return this.name;
		}
	}

	public static final EnumProperty<Model> MODEL = EnumProperty.of("model", Model.class);

	public SideShelfBlock(Settings settings) {
		super(settings, false);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, MODEL, WATERLOGGED);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		return this.getDefaultState().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER).with(MODEL, getModelFor(ctx.getBlockPos(), ctx.getWorld(), ctx.getHorizontalPlayerFacing().getOpposite())).with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}

	private Model getModelFor(BlockPos pos, WorldAccess world, Direction facing) {
		var axis = facing.getAxis() == Direction.Axis.Z ? Direction.Axis.X : Direction.Axis.Z;
		var left = pos;
		var right = pos;
		if (facing == Direction.SOUTH) {
			left = pos.east();
			right = pos.west();
		} else if (facing == Direction.NORTH) {
			left = pos.west();
			right = pos.east();
		} else if (facing == Direction.EAST) {
			left = pos.north();
			right = pos.south();
		} else {
			left = pos.south();
			right = pos.north();
		}

		var st1 = world.getBlockState(left);
		var bleft = st1.getBlock() == this && st1.get(FACING) == facing;
		st1 = world.getBlockState(right);
		var bright = st1.getBlock() == this && st1.get(FACING) == facing;
		return bleft && bright ? Model.CENTER : (bleft ? Model.LEFT : (bright ? Model.RIGHT : Model.SINGLE));
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient && player.isCreative()) {
			Containers.createHandler(player, pos, (sync, inv, p1) -> new ShelfBlock.ScreenHandler(sync, p1, SideShelfBlock.this.getBlockEntity(pos, world).getContainer()));
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}

	@Override
	@Deprecated
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.fullCube();
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		if (direction.getHorizontal() > -1) {
			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos).with(MODEL, getModelFor(pos, world, state.get(FACING)));
		}
		return state;
	}
}
