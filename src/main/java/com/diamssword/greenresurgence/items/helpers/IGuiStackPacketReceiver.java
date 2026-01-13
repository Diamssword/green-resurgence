package com.diamssword.greenresurgence.items.helpers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public interface IGuiStackPacketReceiver {
	public void receiveGuiPacket(ServerPlayerEntity player, ItemStack handStack, NbtCompound received);
}