package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.network.StructureSizePacket;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public interface IStructureProvider {
    public BlockPos getPosition(ItemStack stack, World w);
    public Direction getDirection(ItemStack stack, World w);
    public Identifier getStructureName(ItemStack stack, World w);
    public StructureType strutctureType(ItemStack stack, World w);
    public static enum StructureType{
        normal(0),
        centered(1),
        jigsaw(2);

        public final int id;
        private StructureType(int id)
        {
            this.id=id;
        }
        public static StructureType getById(int id)
        {
            for(StructureType t:StructureType.values())
            {
                if(t.id==id)
                    return t;
            }
            return StructureType.values()[0];
        }
    }
}
