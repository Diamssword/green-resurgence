package com.diamssword.greenresurgence.systems.crafting;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface IRecipe<T extends IResource> {
    public T result(PlayerEntity player);
    public List<T> ingredients(PlayerEntity player);

}
