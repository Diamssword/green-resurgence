package com.diamssword.greenresurgence.systems.crafting;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public interface IResource {
    /**
     * @return ID of the resource
     */
    public Identifier getID();

    /**
     * @return Array of variants that are valid as this resource
     */
    public Identifier[] getAllPossibleIds();

    /**
     * @return amount of resource
     */
    public int getAmount();
    /**
     * @return a display name
     */
    public Text getName();
    /**
     * @return extra data attached to the resource
     */
    default NbtCompound extra()
    {
        return new NbtCompound();
    }
}
