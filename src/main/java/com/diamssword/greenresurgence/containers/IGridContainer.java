package com.diamssword.greenresurgence.containers;

import net.minecraft.inventory.Inventory;

public interface IGridContainer {
    public String getName();
    public int getWidth();
    public int getHeight();
    public int getStartIndex();
    default public int getSize()
    {
        return getWidth()*getHeight();
    }
    public Inventory getInventory();
}
