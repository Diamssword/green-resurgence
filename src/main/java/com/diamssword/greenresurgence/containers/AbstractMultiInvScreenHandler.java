package com.diamssword.greenresurgence.containers;

import com.diamssword.greenresurgence.containers.player.CustomPlayerInventory;
import com.diamssword.greenresurgence.systems.Components;
import io.wispforest.owo.client.screens.SyncedProperty;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public abstract class AbstractMultiInvScreenHandler<T extends AbstractMultiInvScreenHandler<T>> extends ScreenHandler {

    protected AbstractMultiInvScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    public abstract List<String> getInventoriesNames();
    public abstract IGridContainer[] containersFromProps(GridContainerSyncer prop);


    public abstract T setPos(BlockPos pos);
    public abstract BlockPos getPos();
    public abstract ScreenHandlerType<? extends AbstractMultiInvScreenHandler<T>> type();

    /**
     * @return if the containers have been received and are ready to display
     */
    abstract public boolean isReady();

    /**
     *
     * Called when the containers have been received and are ready to display
     * You should check  isReady() before using the callback
     * @param consumer a callback
     */
    abstract public void onReady(Consumer<T> consumer);

   abstract public List<Slot> getSlotForInventory(String name);
    abstract public String getInventoryForSlot(Slot s);
    abstract public String getInventoryForSlot(int slotID);
    abstract public IGridContainer getInventory(String name);
    abstract public IGridContainer getContainerFor(int slot);

    /**
     * A version that can handle the multiple inventories
     * @param player
     * @param containerID
     */
    abstract boolean canUse(PlayerEntity player,String containerID);
    abstract public int totalSize();

}