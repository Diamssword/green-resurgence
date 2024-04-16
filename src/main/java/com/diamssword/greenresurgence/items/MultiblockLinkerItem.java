package com.diamssword.greenresurgence.items;


import com.diamssword.greenresurgence.blockEntities.ConnectorBlockEntity;
import com.diamssword.greenresurgence.blockEntities.SmartStructureEntity;
import com.diamssword.greenresurgence.blocks.ConnectorBlock;
import com.diamssword.greenresurgence.blocks.MainStructureBlock;
import com.diamssword.greenresurgence.blocks.SmartStructureBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class MultiblockLinkerItem extends Item {

    public MultiblockLinkerItem(Settings properties) {
        super(properties);
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(stack.hasNbt() )
        {
            if(stack.getNbt().contains("pos"))
            {
                BlockPos p=BlockPos.fromLong(stack.getNbt().getLong("pos"));
                tooltip.add(Text.of("Liée à "+p.getX()+" "+p.getY()+" "+p.getZ()));
            }
        }
    }
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState st=context.getWorld().getBlockState(context.getBlockPos());
        if(st.getBlock() instanceof MainStructureBlock)
        {
            if(context.getWorld().isClient)
                return ActionResult.SUCCESS;
            context.getStack().getOrCreateNbt().putLong("pos",context.getBlockPos().asLong());
        }
        else if(st.getBlock() instanceof SmartStructureBlock)
        {
            if(context.getWorld().isClient)
                return ActionResult.SUCCESS;
            if(context.getStack().hasNbt() && context.getStack().getNbt().contains("pos")) {
                SmartStructureEntity te = ((SmartStructureBlock) st.getBlock()).getBlockEntity(context.getBlockPos(), context.getWorld());
                BlockPos p = BlockPos.fromLong(context.getStack().getNbt().getLong("pos"));
                te.setOffset(p.subtract(context.getBlockPos()));
                return ActionResult.SUCCESS;
            }
            else
            {
                return ActionResult.FAIL;
            }
        }
        return ActionResult.PASS;
    }

}