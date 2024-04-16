package com.diamssword.greenresurgence.items;



import com.diamssword.greenresurgence.blockEntities.ConnectorBlockEntity;
import com.diamssword.greenresurgence.blocks.ConnectorBlock;
import com.diamssword.greenresurgence.structure.JigsawHelper;
import io.wispforest.owo.nbt.NbtKey;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.*;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;


public class CableItem extends Item {

    public CableItem(Settings properties) {
        super(properties);
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(stack.hasNbt() )
        {
            if(stack.getNbt().contains("connector"))
            {
                BlockPos p=BlockPos.fromLong(stack.getNbt().getLong("connector"));
                tooltip.add(Text.of("Liée à "+p.getX()+" "+p.getY()+" "+p.getZ()));
            }
        }
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
                BlockState st1=context.getWorld().getBlockState(p);
                if(st1.getBlock() instanceof ConnectorBlock)
                {
                    ((ConnectorBlock) st1.getBlock()).getBlockEntity(p,context.getWorld()).addConnection(context.getBlockPos());
                }
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