package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.MBlocks;
import com.diamssword.greenresurgence.blockEntities.GenericStorageBlockEntity;
import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.containers.GridContainer;
import com.diamssword.greenresurgence.containers.IGridContainer;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.perimeter.TerrainInstance;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.SpecialPlacement;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.GlazedTerracottaBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BaseStorageBlock extends GlazedTerracottaBlock implements BlockEntityProvider {
    public final int inventorySize;
    public BaseStorageBlock(Settings settings, int inventorySize) {
        super(settings);
        this.inventorySize = inventorySize;
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            var ls=world.getComponent(Components.BASE_LIST);
            var terr=ls.getTerrainAt(pos);
            terr.ifPresent(terrainInstance -> terrainInstance.storage.removeInventory(pos));
            if (blockEntity instanceof GenericStorageBlockEntity) {
                ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
                ((GenericStorageBlockEntity) blockEntity).clear();
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }
    @Deprecated
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        var te=world.getBlockEntity(pos);
        if(te instanceof Inventory inv) {
            return new NamedScreenHandlerFactory() {
                @Override
                public Text getDisplayName() {
                    return BaseStorageBlock.this.getName();
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {

                    return new ScreenHandler(syncId, playerInventory, new GridContainer("storage", inv, inv.size() <= 9 ? 3 : 9, inv.size() <= 9 ? 3 : inv.size() / 9));
                }
            };
        }
        return null;
    }
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {

        return new GenericStorageBlockEntity(pos,state,inventorySize);
    }
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world,pos,state,placer,itemStack);
        var ls=world.getComponent(Components.BASE_LIST);
        var terr=ls.getTerrainAt(pos);
        if(terr.isPresent())
        {
            var te=world.getBlockEntity(pos);
            if(te instanceof GenericStorageBlockEntity te1)
                terr.get().storage.addIfMissing(pos,te1);
        }
    }
    public static class ScreenHandler extends MultiInvScreenHandler {

        public ScreenHandler(int syncId, PlayerInventory playerInventory) {
            super(syncId, playerInventory);
        }

        public ScreenHandler(int syncId, PlayerInventory playerInventory, IGridContainer... containers) {
            super(syncId, playerInventory, containers);
        }
        @Override
        public ScreenHandlerType<? extends MultiInvScreenHandler> type() {
            return Containers.FAC_CHEST;
        }
    }
    static {
        var i=new SpecialPlacement(){
            @Override
            public boolean onPlacement(PlayerEntity player, TerrainInstance terrain, BlockPos pos) {
                return true;
            }

            @Override
            public boolean onBreak(PlayerEntity player, TerrainInstance terrain, BlockPos pos) {
                    terrain.storage.removeInventory(pos);
                return true;
            }
        };
        SpecialPlacement.REGISTRY.put(MBlocks.BASE_CRATE_T1,i);
        SpecialPlacement.REGISTRY.put(MBlocks.BASE_CRATE_T2,i);
    }
}
