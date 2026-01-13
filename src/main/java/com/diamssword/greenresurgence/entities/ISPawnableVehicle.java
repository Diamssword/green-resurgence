package com.diamssword.greenresurgence.entities;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public interface ISPawnableVehicle {
	public void writeStackData(NbtCompound nbt);

	public void readStackData(NbtCompound nbt);

	public Item asItem();

	public default ItemStack getVehicleItemStack() {
		var nbt = new NbtCompound();
		this.writeStackData(nbt);
		var st = new ItemStack(this.asItem());
		st.setNbt(nbt);
		return st;
	}
}
