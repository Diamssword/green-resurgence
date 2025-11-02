package com.diamssword.greenresurgence.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CameleonBlock extends Block {
	private final boolean vertical;

	public CameleonBlock(boolean vertical) {
		super(FabricBlockSettings.create());
		this.vertical = vertical;
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		world.scheduleBlockTick(pos, this, 2);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
			BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		world.scheduleBlockTick(pos, this, 2);
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		List<Direction> dirs = Arrays.stream(Direction.values()).filter(v -> (vertical && v.getAxis().isVertical()) || (!vertical && v.getAxis().isHorizontal())).toList();
		var lsh = new ArrayList<>(dirs);
		Collections.shuffle(lsh);
		for(var l : lsh) {
			var b = world.getBlockState(pos.offset(l));
			if(!b.isAir()) {
				world.setBlockState(pos, b);
				return;
			}
		}
		world.setBlockState(pos, Blocks.AIR.getDefaultState());
	}
}
