package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrafterBlock extends Block {
    public CrafterBlock() {
        super(Settings.create().sounds(BlockSoundGroup.METAL).strength(5).mapColor(MapColor.BLACK));
    }
    @Deprecated
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(!world.isClient )
        {
            Containers.createHandler(player,pos,(sync, inv, p1)-> new CrafterBlock.ScreenHandler( sync,inv,true).setPos(pos));
            return ActionResult.SUCCESS;
        }
        return ActionResult.SUCCESS;
    }

    public static class ScreenHandler extends MultiInvScreenHandler {

        public ScreenHandler(int syncId, PlayerInventory playerInventory) {
            super(syncId, playerInventory);
        }

        public ScreenHandler( int syncId, PlayerInventory playerInventory, boolean unused) {
            super( syncId, playerInventory, true);
        }

        @Override
        public ScreenHandlerType<CrafterBlock.ScreenHandler> type() {
            return Containers.CRAFTER;
        }

    }
}
