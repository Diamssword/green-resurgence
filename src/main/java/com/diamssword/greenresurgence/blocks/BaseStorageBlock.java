package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.MBlocks;
import com.diamssword.greenresurgence.blockEntities.GenericStorageBlockEntity;
import com.diamssword.greenresurgence.blockEntities.ModBlockEntity;
import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import com.diamssword.greenresurgence.containers.grids.GridContainer;
import com.diamssword.greenresurgence.containers.grids.IGridContainer;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionZone;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.SpecialPlacement;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

public class BaseStorageBlock extends ModBlockEntity<GenericStorageBlockEntity> {
	public final int inventorySize;
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

	public BaseStorageBlock(Settings settings, int inventorySize) {
		super(settings);
		this.inventorySize = inventorySize;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(!world.isClient) {
			NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
			if(screenHandlerFactory != null) {
				player.openHandledScreen(screenHandlerFactory);
			}
		}
		return ActionResult.SUCCESS;
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if(state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			var ls = world.getComponent(Components.BASE_LIST);
			var terr = ls.getTerrainAt(pos);
			terr.ifPresent(terrainInstance -> terrainInstance.getOwner().storage.removeInventory(pos));
			if(blockEntity instanceof GenericStorageBlockEntity) {
				ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
				((GenericStorageBlockEntity) blockEntity).clear();
				world.updateComparators(pos, this);
			}
			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
	}

	@Deprecated
	public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		var te = world.getBlockEntity(pos);
		if(te instanceof Inventory inv) {
			return new NamedScreenHandlerFactory() {
				@Override
				public Text getDisplayName() {
					return BaseStorageBlock.this.getName();
				}

				@Override
				public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {

					return new ScreenHandler(syncId, player, new GridContainer("storage", inv, inv.size() <= 9 ? 3 : 6, inv.size() <= 9 ? 3 : inv.size() / 6));
				}
			};
		}
		return null;
	}

	@Override
	protected GenericStorageBlockEntity createBlockEntity(BlockEntityType<GenericStorageBlockEntity> type, BlockPos pos, BlockState state) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		return new GenericStorageBlockEntity(type, pos, state, inventorySize);
	}

	@Override
	public Class<GenericStorageBlockEntity> getBlockEntityClass() {
		return GenericStorageBlockEntity.class;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		var ls = world.getComponent(Components.BASE_LIST);
		var terr = ls.getTerrainAt(pos);
		if(terr.isPresent()) {
			var te = world.getBlockEntity(pos);
			if(te instanceof GenericStorageBlockEntity te1) {terr.get().getOwner().storage.addIfMissing(pos, te1);}
		}
	}

	public static class ScreenHandler extends MultiInvScreenHandler {

		public ScreenHandler(int syncId, PlayerInventory playerInventory) {
			super(syncId, playerInventory);
		}

		public ScreenHandler(int syncId, PlayerEntity player, IGridContainer... containers) {
			super(syncId, player, containers);
		}

		@Override
		public ScreenHandlerType<? extends MultiInvScreenHandler> type() {
			return Containers.FAC_CHEST;
		}
	}

	static {
		var i = new SpecialPlacement() {
			@Override
			public boolean onPlacement(PlayerEntity player, FactionZone terrain, BlockPos pos) {
				return true;
			}

			@Override
			public boolean onBreak(PlayerEntity player, FactionZone terrain, BlockPos pos) {
				terrain.getOwner().storage.removeInventory(pos);
				return true;
			}
		};
		SpecialPlacement.REGISTRY.put(MBlocks.BASE_CRATE_T1, i);
		SpecialPlacement.REGISTRY.put(MBlocks.BASE_CRATE_T2, i);
	}
}
