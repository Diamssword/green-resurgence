package com.diamssword.greenresurgence.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.Optional;

public class CombinedInventory implements Inventory {

	public final List<Inventory> parents;

	public CombinedInventory(List<Inventory> parents) {
		this.parents = parents;
	}

	@Override
	public int size() {
		var r = 0;
		for(Inventory parent : parents) {
			r += parent.size();
		}
		return r;
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
		return getParentInventory(slot).map(v -> v.getLeft().getStack(v.getRight())).orElse(ItemStack.EMPTY);
	}

	public Optional<Pair<Inventory, Integer>> getParentInventory(int slot) {
		var adv = 0;
		for(Inventory r : parents) {
			if(slot < adv + r.size()) {
				return Optional.of(new Pair<>(r, slot - adv));
			}
			adv += r.size();
		}
		return Optional.empty();
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return getParentInventory(slot).map(v -> v.getLeft().removeStack(v.getRight(), amount)).orElse(ItemStack.EMPTY);
	}

	@Override
	public ItemStack removeStack(int slot) {

		return getParentInventory(slot).map(v -> v.getLeft().removeStack(v.getRight())).orElse(ItemStack.EMPTY);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		getParentInventory(slot).ifPresent(v -> v.getLeft().setStack(v.getRight(), stack));
	}

	@Override
	public void markDirty() {
		parents.forEach(Inventory::markDirty);
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		for(Inventory parent : parents) {
			if(!parent.canPlayerUse(player))
				return false;
		}
		return true;
	}

	@Override
	public void clear() {
		for(Inventory parent : parents) {
			parent.clear();
		}
	}

}
