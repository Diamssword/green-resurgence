package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blockEntities.ItemBlockEntity;
import com.diamssword.greenresurgence.blockEntities.ModBlockEntity;
import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.containers.CreativeMultiInvScreenHandler;
import com.diamssword.greenresurgence.containers.grids.IGridContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
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

public class ItemBlock extends ModBlockEntity<ItemBlockEntity> implements Waterloggable {
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static BooleanProperty COLLISION = BooleanProperty.of("collision");

	public ItemBlock(Settings settings) {
		super(settings);
		this.getDefaultState().with(COLLISION, true).with(FACING, Direction.NORTH).with(WATERLOGGED, false);
	}

	@Override
	public Class<ItemBlockEntity> getBlockEntityClass() {
		return ItemBlockEntity.class;
	}

	@Override
	public Identifier getCustomBlockEntityName() {
		return GreenResurgence.asRessource("item_block");
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
		NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
		if(nbtCompound != null) {
			if(nbtCompound.contains("item")) {
				var item = ItemStack.fromNbt(nbtCompound.getCompound("item"));
				tooltip.add(Text.literal("Item: ").append(item.getName()));
			}
			if(nbtCompound.contains("positionX")) {
				tooltip.add(Text.literal("Position: " + nbtCompound.getInt("positionX") + "," + nbtCompound.getInt("positionY") + "," + nbtCompound.getInt("positionZ")));
			}
			if(nbtCompound.contains("rotationX")) {
				tooltip.add(Text.literal("Rotation: " + nbtCompound.getInt("rotationX") + "," + nbtCompound.getInt("rotationY") + "," + nbtCompound.getInt("rotationZ")));
			}
			if(nbtCompound.contains("size")) {
				tooltip.add(Text.literal("Taille: " + nbtCompound.getDouble("size")));
			}
		}

	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		return this.getDefaultState().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER).with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return state.get(COLLISION) ? VoxelShapes.fullCube() : VoxelShapes.empty();
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(COLLISION, FACING, WATERLOGGED);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if(state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		if(state.get(WATERLOGGED)) {
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
		if(!world.isClient && player.isCreative()) {
			Containers.createHandler(player, pos, (sync, inv, p1) -> new ScreenHandler(sync, player, ItemBlock.this.getBlockEntity(pos, world).getContainer()));
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}

	public static class ScreenHandler extends CreativeMultiInvScreenHandler {

		public ScreenHandler(int syncId, PlayerInventory playerInventory) {
			super(syncId, playerInventory);
		}

		public ScreenHandler(int syncId, PlayerEntity player, IGridContainer... inventories) {
			super(syncId, player.getInventory(), inventories);
		}

		@Override
		public ScreenHandlerType<ScreenHandler> type() {
			return Containers.ITEMBLOCK;
		}

	}
}
