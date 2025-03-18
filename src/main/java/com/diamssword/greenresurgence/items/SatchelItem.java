package com.diamssword.greenresurgence.items;

import net.minecraft.item.ItemStack;

public class SatchelItem extends AbstractBackpackItem{
    private final int w,h;
    public SatchelItem(Settings settings,int w,int h) {
        super(PackSlot.Satchel, settings);
        this.w=w;
        this.h=h;
    }

    @Override
    public int inventoryWidth(ItemStack stack) {
        return w;
    }

    @Override
    public int inventoryHeight(ItemStack stack) {
        return h;
    }
}
