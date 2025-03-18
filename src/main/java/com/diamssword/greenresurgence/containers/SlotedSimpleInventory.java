package com.diamssword.greenresurgence.containers;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class SlotedSimpleInventory extends SimpleInventory {
    public SlotedSimpleInventory(int size) {
        super(size);
    }

    public SlotedSimpleInventory(ItemStack... items) {
        super(items);
    }
    @Override
    public void readNbtList(NbtList nbtList) {
        this.clear();

        for (int i = 0; i < nbtList.size(); i++) {
            ItemStack itemStack = ItemStack.fromNbt(nbtList.getCompound(i));
            if (i< this.size()) {
                this.setStack(i,itemStack);
            }
        }
    }
    @Override
    public NbtList toNbtList() {
        NbtList nbtList = new NbtList();

        for (int i = 0; i < this.size(); i++) {
            ItemStack itemStack = this.getStack(i);
          //  if (!itemStack.isEmpty()) {
                nbtList.add(itemStack.writeNbt(new NbtCompound()));
            //}
        }

        return nbtList;
    }
}
