package com.diamssword.greenresurgence.items.helpers;

import com.diamssword.greenresurgence.items.StackableDamagableItem;
import net.minecraft.client.item.TooltipData;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class DurabilityStorageHelper extends ItemStorageHelper {

	public final List<Item> allowedItems;


	public record DurabilityHolderTooltipData(DefaultedList<ItemStack> stacks, float percent, Item... allowedItems) implements TooltipData {
	}

	public DurabilityStorageHelper(int maxSlot, Item... allowedItems) {
		super(maxSlot);
		this.allowedItems = List.of(allowedItems);
	}

	public DurabilityStorageHelper(int maxTanks) {
		super(maxTanks);
		this.allowedItems = List.of();
	}

	@Override
	public TooltipData getTooltipData(ItemStack stack) {
		return new DurabilityHolderTooltipData(this.getAsInventory(stack).stacks, getDurabilityPercent(stack), this.allowedItems.toArray(new Item[0]));
	}

	@Override
	public boolean isItemCompatible(ItemStack container, ItemStack stack) {
		return !stack.isEmpty() && isDamageable(stack) && (allowedItems.isEmpty() || allowedItems.contains(stack.getItem()));

	}

	public static int getMostDischargedSlot(Inventory inv) {
		int index = 0;
		long energy = -1;
		for(int i = 0; i < inv.size(); i++) {
			var st = inv.getStack(i);
			if(!st.isEmpty()) {
				var d = getItemDamage(st);
				if(d > energy) {
					energy = d;
					index = i;
				}
			}
		}
		return index;
	}

	@Override
	public ItemStack removeFirstStack(ItemStack stack) {
		var inv = this.getAsInventory(stack);
		int sl = getMostDischargedSlot(inv);
		var res = inv.removeStack(sl);
		inv.markDirty();
		return res;
	}

	private static boolean isDamageable(ItemStack stack) {
		if(stack.getItem() instanceof StackableDamagableItem) {
			return true;
		}
		return stack.isDamageable();
	}

	private static int getIteMaxDamage(ItemStack stack) {
		if(stack.getItem() instanceof StackableDamagableItem it) {
			return it.getCustomMaxDamage();
		}
		return stack.getMaxDamage();
	}

	private static int getItemDamage(ItemStack stack) {
		if(stack.getItem() instanceof StackableDamagableItem) {
			return StackableDamagableItem.getDamage(stack);
		}
		return stack.getDamage();
	}

	public float getDurabilityPercent(ItemStack stack) {
		var inv = this.getAsInventory(stack);
		long max = 0;
		long curr = 0;
		for(int i = 0; i < inv.size(); i++) {
			var st = inv.getStack(i);
			curr += getItemDamage(st);
			max += getIteMaxDamage(st);

		}
		if(max == 0)
			return 0f;
		return 1f - (curr / (float) max);
	}


	public boolean tryConsumeDurability(ItemStack stack, int amount) {
		var inv = this.getAsInventory(stack);
		var btc = this.maxItems - getEmptySlots(inv);
		if(btc == 0)
			return false;
		int a = amount / btc;
		int reste = amount % btc;
		for(int i = 0; i < inv.size(); i++) {
			var st = inv.getStack(i);
			if(!st.isEmpty()) {
				var d = getItemDamage(st) + a;
				if(reste > 0) {
					d += reste;
					reste = 0;
				}
				if(d > getIteMaxDamage(st)) {
					var m = getIteMaxDamage(st) - getItemDamage(st);
					d = getIteMaxDamage(st);
					reste += a - m;
				}
				st.setDamage(d);
				if(getItemDamage(st) >= getIteMaxDamage(st)) {
					inv.setStack(i, ItemStack.EMPTY);
				}
			}
		}
		inv.markDirty();
		return reste <= 0;
	}


}
