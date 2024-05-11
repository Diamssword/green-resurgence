package com.diamssword.greenresurgence.containers;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;

public class GridContainer implements IGridContainer{
    private final Inventory inventory;
    private final String name;
    private final int width,height,index;
    public GridContainer(String name, int width, int height)
    {
        this(name,new SimpleInventory(width*height),width,height);
    }
    public GridContainer(String name,Inventory inv, int width, int height)
    {
        this(name,inv,width,height,0);
    }
    public GridContainer(String name,Inventory inv, int width, int height, int index)
    {
        this.inventory=inv;
        this.width=width;
        this.height=height;
        this.index=index;
        this.name=name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getStartIndex() {
        return index;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
