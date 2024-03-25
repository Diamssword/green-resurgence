package com.diamssword.greenresurgence.items;



import com.diamssword.greenresurgence.blockEntities.ConnectorBlockEntity;
import com.diamssword.greenresurgence.blocks.ConnectorBlock;
import com.diamssword.greenresurgence.structure.JigsawHelper;
import io.wispforest.owo.nbt.NbtKey;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.*;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;

import java.util.Optional;


public class CableItem extends Item {

    public CableItem(Settings properties) {
        super(properties);
    }
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState st=context.getWorld().getBlockState(context.getBlockPos());
        if(st.getBlock() instanceof ConnectorBlock)
        {
            if(context.getWorld().isClient)
                return ActionResult.SUCCESS;
            if(context.getStack().hasNbt() && context.getStack().getNbt().contains("connector"))
            {
                ConnectorBlockEntity te=  ((ConnectorBlock) st.getBlock()).getBlockEntity(context.getBlockPos(), context.getWorld());
                BlockPos p=BlockPos.fromLong(context.getStack().getNbt().getLong("connector"));
                te.addConnection(p);
                context.getStack().getNbt().remove("connector");
            }
            else
            {
                context.getStack().getOrCreateNbt().putLong("connector",context.getBlockPos().asLong());
            }
        }
        return ActionResult.PASS;
    }

}