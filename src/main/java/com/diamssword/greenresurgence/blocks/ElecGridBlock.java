package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.blockEntities.ModBlockEntity;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.perimeter.FactionArea;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.IGridDependant;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.TerrainEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class ElecGridBlock<T extends BlockEntity & IGridDependant> extends ModBlockEntity<T> {

	public ElecGridBlock(Settings settings) {
		super(settings);
	}

	public int getIO(World world, BlockPos pos) {
		return getBlockEntity(pos, world).getIO();
	}

	public long getCapacity(World world, BlockPos pos) {
		return getBlockEntity(pos, world).getCapacity();
	}

	public void updateGridStatus(World world, BlockPos pos) {
		getEStorage(world, pos).ifPresent(s -> {
			s.editCapacity(pos, getCapacity(world, pos));
			s.editIOPerSecond(pos, getIO(world, pos));
		});
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		updateGridStatus(world, pos);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		super.randomTick(state, world, pos, random);
		getEStorage(world, pos).ifPresent(s -> {
			var c = getCapacity(world, pos);
			var i = getIO(world, pos);
			if(c > 0)
				s.editCapacity(pos, c);
			if(i != 0)
				s.editIOPerSecond(pos, i);
		});
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return true;
	}

	public Optional<TerrainEnergyStorage> getEStorage(World world, BlockPos pos) {
		return Components.BASE_LIST.get(world).getAreaAt(pos).map(FactionArea::getEnergyStorage);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if(state.getBlock() != newState.getBlock()) {
			getEStorage(world, pos).ifPresent(s -> {
				s.editCapacity(pos, 0);
				s.editIOPerSecond(pos, 0);
			});
		}
		super.onStateReplaced(state, world, pos, newState, moved);
	}

}
