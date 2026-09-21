package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.blockEntities.GeneratorBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;

public class GeneratorBlock extends ElecGridBlock<GeneratorBlockEntity> {
	public GeneratorBlock(Settings settings) {
		super(settings);
		this.setTickerFactory((p, w) -> GeneratorBlockEntity::tick);
	}

	@Override
	public Class<GeneratorBlockEntity> getBlockEntityClass() {
		return GeneratorBlockEntity.class;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

}
