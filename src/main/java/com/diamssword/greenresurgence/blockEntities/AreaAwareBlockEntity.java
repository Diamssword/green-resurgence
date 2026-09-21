package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.perimeter.FactionArea;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.IGridDependant;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public abstract class AreaAwareBlockEntity extends BlockEntity implements IGridDependant {
	private FactionArea cachedArea;

	public AreaAwareBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public Optional<FactionArea> getArea() {
		return Optional.ofNullable(cachedArea);
	}

	public void setCachedArea(FactionArea area) {
		this.cachedArea = area;
	}

	protected void checkForArea(boolean force) {
		if(world != null && !world.isClient && (force || world.getTime() % 40 == 0)) {
			cachedArea = Components.BASE_LIST.get(world).getAreaAt(pos).orElse(null);
		}
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		checkForArea(true);
	}

	@Override
	public void updateGrid() {

		getArea().ifPresent(s -> {
			s.getEnergyStorage().editCapacity(pos, getCapacity());
			s.getEnergyStorage().editIOPerSecond(pos, getIO());
		});
	}
}
