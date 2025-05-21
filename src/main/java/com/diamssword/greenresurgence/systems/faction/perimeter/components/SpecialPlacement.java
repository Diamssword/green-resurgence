package com.diamssword.greenresurgence.systems.faction.perimeter.components;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public interface SpecialPlacement {
	Map<Block, SpecialPlacement> REGISTRY = new HashMap<>();

	boolean onPlacement(PlayerEntity player, FactionZone terrain, BlockPos pos);

	boolean onBreak(PlayerEntity player, FactionZone terrain, BlockPos pos);
}
