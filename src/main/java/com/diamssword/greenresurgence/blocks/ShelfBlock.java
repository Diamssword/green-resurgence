package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blockEntities.LootableShelfEntity;
import com.diamssword.greenresurgence.blockEntities.ModBlockEntity;
import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.containers.CreativeMultiInvScreenHandler;
import com.diamssword.greenresurgence.containers.IGridContainer;
import net.minecraft.block.*;
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
import net.minecraft.util.*;
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

public class ShelfBlock extends ModBlockEntity<LootableShelfEntity> implements Waterloggable {
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final BooleanProperty BOTTOM = Properties.BOTTOM;
	private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 3, 16);
	private static final VoxelShape[] SHAPES = {

			VoxelShapes.union(SHAPE, Block.createCuboidShape(0, 0, 15, 16, 16, 16)),
			VoxelShapes.union(SHAPE, Block.createCuboidShape(0, 0, 0, 16, 16, 1)),
			VoxelShapes.union(SHAPE, Block.createCuboidShape(15, 0, 0, 16, 16, 16)),
			VoxelShapes.union(SHAPE, Block.createCuboidShape(0, 0, 0, 1, 16, 16)),
	};
	private final boolean fullHitbox;

	public ShelfBlock(Settings settings, boolean fullHitBox) {
		super(settings);
		this.fullHitbox = fullHitBox;
		this.getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false);
		this.setTickerFactory((w, p) -> LootableShelfEntity::tick);
	}

	public boolean hasBottomLogic() {
		return false;
	}

	@Override
	public Identifier getCustomBlockEntityName() {
		return GreenResurgence.asRessource("lootable_shelf");
	}

	@Override
	public Class<LootableShelfEntity> getBlockEntityClass() {
		return LootableShelfEntity.class;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
		NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
		if (nbtCompound != null) {
			if (nbtCompound.contains("item")) {
				var item = ItemStack.fromNbt(nbtCompound.getCompound("item"));
				tooltip.add(Text.literal("Item: ").append(item.getName()));
			}
		}

	}

	@Override
	@Deprecated
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (fullHitbox)
			return super.getOutlineShape(state, world, pos, context);
		return SHAPES[state.get(FACING).getId() - 2];
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		var bst = ctx.getWorld().getBlockState(ctx.getBlockPos().down());

		var st = this.getDefaultState().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER).with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
		if (hasBottomLogic()) {
			var bot = bst.isSideSolid(ctx.getWorld(), ctx.getBlockPos().down(), Direction.UP, SideShapeType.FULL) && bst.getBlock() != this;
			st = st.with(BOTTOM, bot);
		}
		return st;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		if (hasBottomLogic())
			builder.add(FACING, BOTTOM, WATERLOGGED);
		else
			builder.add(FACING, WATERLOGGED);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		if (hasBottomLogic() && direction == Direction.DOWN) {
			var bot = neighborState.isSideSolid(world, pos.down(), Direction.UP, SideShapeType.FULL) && neighborState.getBlock() != this;
			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos).with(BOTTOM, bot);
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

	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient && player.isCreative()) {
			Containers.createHandler(player, pos, (sync, inv, p1) -> new ShelfBlock.ScreenHandler(sync, p1, ShelfBlock.this.getBlockEntity(pos, world).getContainer()));
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}

	@Deprecated
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	public static class ScreenHandler extends CreativeMultiInvScreenHandler {

		public ScreenHandler(int syncId, PlayerInventory playerInventory) {
			super(syncId, playerInventory);
		}

		public ScreenHandler(int syncId, PlayerEntity player, IGridContainer... inventories) {
			super(syncId, player.getInventory(), inventories);
		}

		@Override
		public ScreenHandlerType<ShelfBlock.ScreenHandler> type() {
			return Containers.ITEMBLOCKSIMPLE;
		}
	}
}
