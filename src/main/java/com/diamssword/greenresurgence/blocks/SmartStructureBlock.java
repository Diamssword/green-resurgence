package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.blockEntities.ConnectorBlockEntity;
import com.diamssword.greenresurgence.blockEntities.SmartStructureEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class SmartStructureBlock extends LayeredBlock implements BlockEntityProvider {
    public SmartStructureBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SmartStructureEntity(pos,state);
    }
    public SmartStructureEntity getBlockEntity(BlockPos pos, BlockView world)
    {
        return (SmartStructureEntity) world.getBlockEntity(pos);
    }
}
