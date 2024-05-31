package com.diamssword.greenresurgence.systems.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemResource implements IResource{
    private final int count;
    private final NbtCompound data;
    private final Identifier item;
    public ItemResource(ItemStack stack)
    {
        count=stack.getCount();
        data=stack.getNbt();
        item= Registries.ITEM.getId(stack.getItem());
    }
    public ItemResource(Identifier item,int count)
    {
        this.count=count;
        data=null;
        this.item=item;
    }
    @Override
    public Identifier getID() {
        return item;
    }

    @Override
    public boolean drawAsItem() {
        return true;
    }

    @Override
    public ItemStack asItem() {
        var i=new ItemStack(Registries.ITEM.get(item),count);
        if(this.data!=null)
            i.setNbt(data);
        return i;
    }

    @Override
    public int getAmount() {
        return count;
    }

    @Override
    public Identifier getSpriteId() {
        return item;
    }

    @Override
    public Text getName() {
        return asItem().getName();
    }

    @Override
    public NbtCompound extra() {
        return this.data;
    }

}
