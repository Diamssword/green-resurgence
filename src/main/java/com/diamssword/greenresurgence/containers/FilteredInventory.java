package com.diamssword.greenresurgence.containers;

import net.minecraft.item.ItemStack;

import java.util.function.BiFunction;

public class FilteredInventory extends SlotedSimpleInventory {

	private BiFunction<FilteredInventory, ItemStack, Boolean> filter;
	private boolean singleItem = false;

	public FilteredInventory(int size, BiFunction<FilteredInventory, ItemStack, Boolean> filter) {
		super(size);
		this.filter = filter;
	}

	public FilteredInventory(BiFunction<FilteredInventory, ItemStack, Boolean> filter, ItemStack... items) {
		super(items);
		this.filter = filter;
	}

	public FilteredInventory setSingleItem(boolean single) {
		this.singleItem = single;
		return this;
	}

	public boolean getSingleItem() {
		return singleItem;
	}

	public void setFilter(BiFunction<FilteredInventory, ItemStack, Boolean> filter) {
		this.filter = filter;
	}

	public BiFunction<FilteredInventory, ItemStack, Boolean> getFilter() {
		return filter;
	}


	@Override
	public boolean canInsert(ItemStack stack) {
		return filter.apply(this, stack);
	}

	@Override
	public ItemStack addStack(ItemStack stack) {

		if(stack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {

			ItemStack itemStack = stack.copy();

			if(filter.apply(this, itemStack)) {
				if(!singleItem)
					this.addToExistingSlot(itemStack);
				if(itemStack.isEmpty()) {
					return ItemStack.EMPTY;
				} else {
					this.addToNewSlot(itemStack);
					return itemStack.isEmpty() ? ItemStack.EMPTY : itemStack;
				}
			}
			return itemStack;
		}
	}

	private void addToExistingSlot(ItemStack stack) {
		for(int i = 0; i < this.size(); i++) {
			ItemStack itemStack = this.getStack(i);
			if(ItemStack.canCombine(itemStack, stack)) {
				this.transfer(stack, itemStack);
				if(stack.isEmpty()) {
					return;
				}
			}
		}
	}

	@Override
	public int getMaxCountPerStack() {
		return this.singleItem ? 1 : 64;
	}

	private void transfer(ItemStack source, ItemStack target) {
		int i = Math.min(this.getMaxCountPerStack(), target.getMaxCount());
		int j = Math.min(source.getCount(), i - target.getCount());
		if(j > 0) {
			target.increment(j);
			source.decrement(j);
			this.markDirty();
		}
	}

	private void addToNewSlot(ItemStack stack) {
		for(int i = 0; i < this.size(); i++) {
			ItemStack itemStack = this.getStack(i);
			if(itemStack.isEmpty()) {
				this.setStack(i, stack.copyAndEmpty());
				return;
			}
		}
	}
}
