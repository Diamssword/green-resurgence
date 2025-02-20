package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.MBlocks;
import com.diamssword.greenresurgence.blocks.DeployableLadderBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class DeployableLadder extends Item {
    public DeployableLadder(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if(context.getWorld().getBlockState(context.getBlockPos()).getBlock() == MBlocks.DEPLOYABLE_LADDER)
            return ActionResult.FAIL;
        var pos= context.getBlockPos();
        var dir=context.getHorizontalPlayerFacing().getOpposite();
        if(context.getSide()== Direction.DOWN)
        {
            dir=Direction.DOWN;
            pos=pos.down();
        }
        else if(context.getSide()== Direction.UP)
        {

            pos=pos.offset(context.getHorizontalPlayerFacing());
        }
        else
        {
            pos=pos.offset(context.getSide());
            dir=context.getSide().getOpposite();
        }
        if(Deploy(context.getWorld(),pos,dir))
        {
            if(context.getPlayer() == null || !context.getPlayer().getAbilities().creativeMode)
                context.getStack().decrement(1);
            BlockSoundGroup blockSoundGroup = MBlocks.DEPLOYABLE_LADDER.getDefaultState().getSoundGroup();
            context.getWorld().playSound(context.getPlayer(), pos, blockSoundGroup.getPlaceSound(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F);
            context.getWorld().emitGameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Emitter.of(context.getPlayer(),MBlocks.DEPLOYABLE_LADDER.getDefaultState()));
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }
    private boolean Deploy(World world, BlockPos pos,Direction orientation)
    {
        var state=world.getBlockState(pos);
        if(state.isFullCube(world,pos) ||pos.getY()<-63 || state.getBlock()==MBlocks.DEPLOYABLE_LADDER)
            return false;
        world.setBlockState(pos, MBlocks.DEPLOYABLE_LADDER.getDefaultState().with(DeployableLadderBlock.FACING,orientation).with(DeployableLadderBlock.MASTER,true));
        MBlocks.DEPLOYABLE_LADDER.getBlockEntity(pos,world).setOriginalState(state);
        return true;
    }
}
