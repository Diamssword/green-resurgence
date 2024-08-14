package com.diamssword.greenresurgence.systems.faction.perimeter.components;

import com.diamssword.greenresurgence.systems.faction.perimeter.TerrainInstance;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SpecialPlacement {
    public static Map<Block,SpecialPlacement> REGISTRY = new HashMap<>();
    public boolean onPlacement(PlayerEntity player, TerrainInstance terrain, BlockPos pos);
    public boolean onBreak(PlayerEntity player, TerrainInstance terrain, BlockPos pos);
}
