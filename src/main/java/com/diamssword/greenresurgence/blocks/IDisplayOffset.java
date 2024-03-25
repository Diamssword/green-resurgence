package com.diamssword.greenresurgence.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface IDisplayOffset {

    public Vec3d getOffset(BlockState st, World w);
    public float getScale(BlockState st, World w);
}
