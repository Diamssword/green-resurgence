package com.diamssword.greenresurgence.utils;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

public class CompatibilityWarper {

	public static Function<ServerWorld, Optional<Pair<HashMap<BlockPos, BlockState>, ServerWorld>>> getCreateSimulatedWorld = (w) -> Optional.empty();
}
