package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.MBlockEntities;
import com.diamssword.greenresurgence.MBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class SmartStructureEntity  extends BlockEntity {
    public int offsetX;
    public int offsetY;
    public int offsetZ;
    public SmartStructureEntity(BlockPos pos, BlockState state) {
        super(MBlockEntities.SMART_STRUCTURE_BLOCK, pos, state);
    }
    public void setOffset(Vec3i pos)
    {
        offsetX=pos.getX();
        offsetY=pos.getY();
        offsetZ=pos.getZ();
        this.markDirty();
    }
    @Override
    public void writeNbt(NbtCompound nbt) {
        // Save the current value of the number to the nbt
        nbt.putInt("offsetX", offsetX);
        nbt.putInt("offsetY", offsetY);
        nbt.putInt("offsetZ", offsetZ);
        super.writeNbt(nbt);
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        offsetX = nbt.getInt("offsetX");
        offsetY = nbt.getInt("offsetY");
        offsetZ = nbt.getInt("offsetZ");
    }
}
