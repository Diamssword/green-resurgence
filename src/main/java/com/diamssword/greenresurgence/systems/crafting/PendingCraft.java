package com.diamssword.greenresurgence.systems.crafting;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.impl.transfer.item.InventoryStorageImpl;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PendingCraft {

    public final SimpleInventory stacks= new SimpleInventory(100);
    public final SimpleRecipe recipe;
    public PendingCraft(SimpleRecipe recipe)
    {
        this.recipe=recipe;
    }
    public Storage<ItemVariant> getAsStorage()
    {
        return InventoryStorageImpl.of(stacks,null);
    }


}
