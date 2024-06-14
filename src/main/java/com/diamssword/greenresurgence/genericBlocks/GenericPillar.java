package com.diamssword.greenresurgence.genericBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GlazedTerracottaBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class GenericPillar extends GlazedTerracottaBlock {
    private final GenericBlockSet.Transparency transparency;
    public GenericPillar(Settings settings, GenericBlockSet.Transparency transparency) {
        super(settings);this.transparency=transparency;
    }
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        if(transparency== GenericBlockSet.Transparency.UNDEFINED||transparency== GenericBlockSet.Transparency.OPAQUE)
            return  super.getAmbientOcclusionLightLevel(state,world,pos);
        return 1.0F;
    }
}
