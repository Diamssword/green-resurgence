package com.diamssword.greenresurgence.systems.faction.perimeter.components;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class FormattedInventory implements Inventory {
	public final Inventory parent;
	private final List<ItemStack> items = new ArrayList<>();
	@Nullable
	private List<InventoryChangedListener> listeners;

	public FormattedInventory(Inventory parent) {
		this.parent = parent;
		refresh();
	}

	void refresh() {
		items.clear();
		for (int i = 0; i < parent.size(); i++) {
			var d = parent.getStack(i);
			if (!d.isEmpty()) {
				var d3 = items.stream().filter(v -> ItemStack.canCombine(v, d)).findAny();
				if (d3.isPresent()) {
					d3.get().setCount(d3.get().getCount() + d.getCount());

				} else
					items.add(d.copy());
			}
		}
		items.sort(Comparator.comparingInt(ItemStack::getCount).reversed());
		if (items.isEmpty())
			items.add(ItemStack.EMPTY);
		this.markDirty();
	}

	public void addListener(InventoryChangedListener listener) {
		if (this.listeners == null) {
			this.listeners = Lists.newArrayList();
		}

		this.listeners.add(listener);
	}

	public void removeListener(InventoryChangedListener listener) {
		if (this.listeners != null) {
			this.listeners.remove(listener);
		}

	}

	@Override
	public int size() {
		return items.size();
	}

	@Override
	public boolean isEmpty() {
		return items.isEmpty();
	}

	@Override
	public ItemStack getStack(int slot) {
		return getDisplayInSlot(slot);
	}

	private int findStackInParent(ItemStack stack) {
		for (int i = 0; i < parent.size(); i++) {
			if (ItemStack.canCombine(parent.getStack(i), stack))
				return i;
		}
		return -1;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		var stack = getDisplayInSlot(slot);
		var s1 = findStackInParent(stack);
		var re = parent.removeStack(s1, amount);
		if (re.getCount() < amount) {
			s1 = findStackInParent(stack);
			var re1 = parent.removeStack(s1, amount - re.getCount());
			re.setCount(re.getCount() + re1.getCount());
		}

		return re;
	}

	@Override
	public ItemStack removeStack(int slot) {
		return this.removeStack(slot, getDisplayInSlot(slot).getMaxCount());
	}

	public boolean canInsert(ItemStack stack) {
		for (int i = 0; i < parent.size(); i++) {
			var st1 = parent.getStack(i);
			if (st1.isEmpty())
				return true;
			if (ItemStack.canCombine(st1, stack))
				if (st1.getCount() < st1.getMaxCount())
					//  if(st1.getCount()+stack.getCount()<=st1.getMaxCount())
					return true;
		}
		return false;
	}

	/**
	 * @param stack
	 * @return le nombre d'item ne pouvant pas être inseré
	 */
	public int inserStack(ItemStack stack) {
		var ret = stack.getCount();
		for (int i = 0; i < parent.size(); i++) {
			var st1 = parent.getStack(i);
			if (ItemStack.canCombine(st1, stack)) {
				var ins = Math.min(st1.getMaxCount() - st1.getCount(), stack.getCount());
				if (ins > 0) {
					if (ret < ins)
						ins = ret;
					ret -= ins;
					st1.increment(ins);
					if (ret == 0) {
						refresh();
						return 0;
					}
				}
			}
		}
		for (int i = 0; i < parent.size(); i++) {
			var st1 = parent.getStack(i);
			if (st1.isEmpty()) {
				var s1 = stack.copy();
				s1.setCount(ret);
				parent.setStack(i, s1);
				refresh();
				return 0;
			}
		}
		return ret;
	}

	public ItemStack getDisplayStack(ItemStack stack) {
		var d = this.items.stream().filter(v -> ItemStack.canCombine(stack, v)).findAny();
		return d.orElse(ItemStack.EMPTY);
	}

	public ItemStack getDisplayInSlot(int slot) {
		if (slot >= items.size())
			return ItemStack.EMPTY;
		return items.get(slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		if (stack.isEmpty()) {
			var s1 = findStackInParent(getDisplayInSlot(slot));
			if (s1 > -1) {
				parent.setStack(s1, stack);
				refresh();
			}
			return;
		}
		for (int i = 0; i < parent.size(); i++) {
			var i1 = parent.getStack(i);
			if (ItemStack.canCombine(i1, stack)) {
				var ins = Math.min(i1.getMaxCount() - i1.getCount(), stack.getCount());
				stack.decrement(ins);
				i1.increment(ins);
				if (stack.isEmpty())
					break;
			}

		}
		if (!stack.isEmpty())
			for (int i = 0; i < parent.size(); i++) {
				if (parent.getStack(i).isEmpty()) {
					parent.setStack(i, stack);
					break;
				}
			}
		refresh();
	}

	@Override
	public void markDirty() {
		this.parent.markDirty();
		if (this.listeners != null) {
			for (InventoryChangedListener inventoryChangedListener : this.listeners) {
				inventoryChangedListener.onInventoryChanged(this);
			}
		}
	}

	public int getMaxCountPerStack() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return true;
	}

	@Override
	public void clear() {

	}

	public static class MySlot extends Slot {
		private final FormattedInventory inv;

		public MySlot(FormattedInventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
			this.inv = inventory;
		}

		@Override
		public ItemStack getStack() {
			return inv.getDisplayInSlot(this.getIndex());
		}

		@Override
		public void setStackNoCallbacks(ItemStack stack) {
			if (inv.items.size() <= this.getIndex())
				inv.items.add(stack);
			else
				inv.items.set(this.getIndex(), stack);
//            super.setStackNoCallbacks(stack);
		}

		@Override
		public void setStack(ItemStack stack) {
			inv.setStack(this.getIndex(), stack);
		}

		public ItemStack takeStack(int amount) {
			return inv.removeStack(this.getIndex(), amount);
		}

		public ItemStack getRealStack() {
			return this.inventory.getStack(this.getIndex());
		}

		@Override
		public void onTakeItem(PlayerEntity player, ItemStack stack) {

		}

		@Override
		public int getMaxItemCount(ItemStack stack) {
			return stack.getMaxCount();
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return inv.canInsert(stack);
		}

		@Override
		public ItemStack insertStack(ItemStack stack, int count) {
			var stack1 = stack.copy();
			stack1.setCount(count);
			if (!stack.isEmpty() && this.canInsert(stack)) {
				var r = inv.inserStack(stack1);
				stack.setCount(stack.getCount() - (count - r));
			}
			return stack;
		}

		public Optional<ItemStack> tryTakeStackRange(int min, int max, PlayerEntity player) {
			if (!this.canTakeItems(player)) {
				return Optional.empty();
			} else if (!this.canTakePartial(player) && max < this.getStack().getCount()) {
				return Optional.empty();
			} else {
				min = Math.min(min, Math.min(getStack().getMaxCount(), max));
				ItemStack itemStack = this.takeStack(min);
				if (itemStack.isEmpty()) {
					return Optional.empty();
				} else {
					if (this.getStack().isEmpty()) {
						this.setStack(ItemStack.EMPTY);
					}

					return Optional.of(itemStack);
				}
			}
		}
	}
}
