package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.MBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class NanoGeneratorBlock extends Block {
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final VoxelShape POLE = Block.createCuboidShape(6, 0, 6, 10, 16, 10);
	public static final VoxelShape SMALL = Block.createCuboidShape(3, 0, 3, 13, 10, 13);

	public static final VoxelShape SIDE_E = Block.createCuboidShape(0, 0, 0, 16, 16, 8);
	public static final VoxelShape SIDE_W = Block.createCuboidShape(0, 0, 8, 16, 16, 16);
	public static final VoxelShape SIDE_N = Block.createCuboidShape(0, 0, 0, 8, 16, 16);
	public static final VoxelShape SIDE_S = Block.createCuboidShape(8, 0, 0, 16, 16, 16);

	public enum HITBOX {
		big,
		small,
		pole,
		side,
		side_revert
	}

	public final HITBOX hitbox;

	public NanoGeneratorBlock(Settings settings, HITBOX hitbox) {
		super(settings.strength(-1.0f, 3600000.0f).dropsNothing().allowsSpawning(Blocks::never));
		this.hitbox = hitbox;
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

	@Deprecated
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		switch(hitbox) {
			case big -> {
				return VoxelShapes.fullCube();
			}
			case pole -> {
				return POLE;
			}
			case small -> {
				return SMALL;
			}
			case side -> {
				return switch(state.get(FACING)) {
					case EAST -> SIDE_E;
					case WEST -> SIDE_W;
					case NORTH -> SIDE_N;
					default -> SIDE_S;
				};
			}
			case side_revert -> {
				return switch(state.get(FACING)) {
					case EAST -> SIDE_W;
					case WEST -> SIDE_E;
					case NORTH -> SIDE_S;
					default -> SIDE_N;
				};
			}
		}
		return VoxelShapes.fullCube();
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(this == MBlocks.NANOTEK_GENERATOR_COMPUTER)
			world.getBlockState(pos.up()).onUse(world, player, hand, hit.withBlockPos(pos.up()));
		return super.onUse(state, world, pos, player, hand, hit);
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean hasSidedTransparency(BlockState state) {
		return true;
	}

	@Override
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		return 1f;
	}
}
