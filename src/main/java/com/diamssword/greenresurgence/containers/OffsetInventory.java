package com.diamssword.greenresurgence.containers;

import com.diamssword.greenresurgence.utils.TriConsumer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OffsetInventory implements Inventory {

	public final Inventory parent;
	private final int offset;
	private final int length;
	List<TriConsumer<Integer, ItemStack, ItemStack>> listeners = new ArrayList<>();

	public OffsetInventory(Inventory parent, int offset, int length) {
		this.length = length;
		this.offset = offset;
		this.parent = parent;
	}

	@Override
	public int size() {
		return length;
	}

	@Override
	public boolean isEmpty() {
		for(int i = 0; i < size(); i++) {
			if(!getStack(i).isEmpty()) {return false;}
		}
		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		return parent.getStack(slot + offset);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		var r = parent.removeStack(slot + offset, amount);
		triggerChange(slot, r);
		return r;
	}

	@Override
	public ItemStack removeStack(int slot) {
		var r = parent.removeStack(slot + offset);
		triggerChange(slot, r);
		return r;
	}

	private void triggerChange(int slot, ItemStack old) {
		listeners.forEach(c -> c.accept(slot, old, this.getStack(slot)));
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		var old = getStack(slot);
		parent.setStack(slot + offset, stack);
		triggerChange(slot, old);
	}

	@Override
	public void markDirty() {
		parent.markDirty();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return parent.canPlayerUse(player);
	}

	@Override
	public void clear() {
		parent.clear();
	}

	/**
	 *
	 * @param listener called with the slot id and the old stack and the new stack
	 */
	public void onSlotChange(TriConsumer<Integer, ItemStack, ItemStack> listener) {
		this.listeners.add(listener);

	}
}