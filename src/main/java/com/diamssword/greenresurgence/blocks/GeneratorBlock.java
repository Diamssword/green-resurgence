package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.blockEntities.GeneratorBlockEntity;
import com.diamssword.greenresurgence.blockEntities.ModBlockEntity;
import com.diamssword.greenresurgence.systems.Components;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GeneratorBlock extends ModBlockEntity<GeneratorBlockEntity> {
    public GeneratorBlock(Settings settings) {
        super(settings);
        this.setTickerFactory((p,w)->GeneratorBlockEntity::tick);
    }

    @Override
    public Class<GeneratorBlockEntity> getBlockEntityClass() {
        return GeneratorBlockEntity.class;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            var ls=world.getComponent(Components.BASE_LIST);
            var terr=ls.getTerrainAt(pos);
            if (blockEntity instanceof GeneratorBlockEntity gen) {
                terr.ifPresent(terrainInstance -> terrainInstance.getOwner().energyStorage.addCapacity(-gen.rfGen));
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world,pos,state,placer,itemStack);
        var ls=world.getComponent(Components.BASE_LIST);
        var terr=ls.getTerrainAt(pos);
        if(terr.isPresent())
        {
            var te=world.getBlockEntity(pos);
            if(te instanceof GeneratorBlockEntity te1)
                terr.get().getOwner().energyStorage.addCapacity(te1.rfGen);
        }
    }
}
