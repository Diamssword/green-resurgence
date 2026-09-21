package com.diamssword.greenresurgence.systems.faction.perimeter.components;

import com.diamssword.greenresurgence.blocks.ElecGridBlock;
import net.minecraft.block.entity.BlockEntity;

public interface IGridDependant {
	public int getIO();

	public long getCapacity();

	public default void updateGrid() {
		if(this instanceof BlockEntity be) {
			if(be.getCachedState().getBlock() instanceof ElecGridBlock<?> eb) {
				eb.updateGridStatus(be.getWorld(), be.getPos());
			}
		}
	}

}
