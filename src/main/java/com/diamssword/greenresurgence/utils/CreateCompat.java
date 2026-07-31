package com.diamssword.greenresurgence.utils;

import net.createmod.catnip.levelWrappers.PlacementSimulationServerLevel;
import net.minecraft.block.BlockState;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class CreateCompat {
	public static void init() {
		CompatibilityWarper.getCreateSimulatedWorld = (w) -> {
			var w1 = new PlacementSimulationServerLevel(w) {
				@Override
				public boolean setBlockState(BlockPos pos, BlockState newState, int flags) {
					if(newState.isAir())
						return true;
					blocksAdded.put(pos.toImmutable(), newState);
					return true;
				}
			};
			return Optional.of(new Pair<>(w1.blocksAdded, w1));
		};
	}
}
