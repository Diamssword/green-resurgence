package com.diamssword.greenresurgence.systems.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public interface IResource {
    public Identifier getID();
    public boolean drawAsItem();
    public ItemStack asItem();
    public int getAmount();
    public Identifier getSpriteId();
    public Text getName();
    default NbtCompound extra()
    {
        return new NbtCompound();
    }
}
