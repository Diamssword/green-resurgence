package com.diamssword.greenresurgence.containers;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;

import java.util.function.Consumer;

public interface IGridContainer {
    public String getName();
    default public Slot createSlotFor(int index, int x, int y)
    {
        return new Slot(this.getInventory(), index, x , y);
    }
    default public boolean revert(){return false;}
    public int getWidth();
    public int getHeight();
    public int getStartIndex();
    default public int getSize()
    {
        return getWidth()*getHeight();
    }
    public Inventory getInventory();
    public boolean isPlayerContainer();
    public void onContentChange(Runnable onChange);
}
