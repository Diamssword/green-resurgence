package com.diamssword.greenresurgence.items.helpers;

import com.diamssword.greenresurgence.items.materials.BatteryItem;
import com.diamssword.greenresurgence.items.materials.BatteryTiers;
import net.minecraft.client.item.TooltipData;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import team.reborn.energy.api.base.SimpleEnergyItem;

public class BatteryStorageHelper extends ItemStorageHelper implements SimpleEnergyItem {

	public final BatteryTiers minTier;
	public final BatteryTiers maxTier;

	public record BatteryHolderTooltipData(DefaultedList<ItemStack> stacks, BatteryTiers min, BatteryTiers max) implements TooltipData {
	}

	public BatteryStorageHelper(int maxBatteries, BatteryTiers minTier) {
		super(maxBatteries);
		this.minTier = minTier;
		this.maxTier = minTier;
	}

	public BatteryStorageHelper(int maxBatteries, BatteryTiers minTier, BatteryTiers maxTier) {
		super(maxBatteries);
		this.minTier = minTier;
		this.maxTier = maxTier;
	}

	public TooltipData getTooltipData(ItemStack stack) {
		return new BatteryHolderTooltipData(this.getAsInventory(stack).stacks, this.minTier, this.maxTier);
	}

	@Override
	public boolean isItemCompatible(ItemStack container, ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof BatteryItem bat && bat.getEnergyMaxOutput(stack) >= this.minTier.maxIO && bat.getEnergyMaxOutput(stack) <= this.maxTier.maxIO;
	}

	public static int getMostDischargedSlot(Inventory inv) {
		int index = 0;
		long energy = Long.MAX_VALUE;
		for(int i = 0; i < inv.size(); i++) {
			var st = inv.getStack(i);
			if(st.getItem() instanceof ISimpleEnergyItemTiered ti) {
				var l = ti.getStoredEnergy(st);
				if(l < energy) {
					energy = l;
					index = i;
					if(energy == 0)
						return index;
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

	public long getEnergyCapacity(ItemStack stack) {
		var inv = this.getAsInventory(stack);
		long cap = 0;
		for(int i = 0; i < inv.size(); i++) {
			var st = inv.getStack(i);
			if(st.getItem() instanceof SimpleEnergyItem it) {
				cap += it.getEnergyCapacity(st);
			}
		}
		return cap;
	}

	@Override
	public long getEnergyMaxInput(ItemStack stack) {
		if(this.getAsInventory(stack).isEmpty())
			return 0;
		return this.minTier.maxIO;
	}

	@Override
	public long getEnergyMaxOutput(ItemStack stack) {
		return 0;
	}

	@Override
	public long getStoredEnergy(ItemStack stack) {
		var inv = this.getAsInventory(stack);
		long cap = 0;
		for(int i = 0; i < inv.size(); i++) {
			var st = inv.getStack(i);
			if(st.getItem() instanceof SimpleEnergyItem it) {
				cap += it.getStoredEnergy(st);
			}
		}
		return cap;
	}

	@Override
	public void setStoredEnergy(ItemStack stack, long newAmount) {
		var inv = this.getAsInventory(stack);
		long reste = 0;
		var btc = this.maxItems - getEmptySlots(inv);
		if(btc <= 0)
			return;
		var splited = newAmount / btc;
		for(int i = 0; i < inv.size(); i++) {
			var st = inv.getStack(i);
			if(st.getItem() instanceof SimpleEnergyItem it) {
				var max = it.getEnergyCapacity(st);
				if(splited > max) {
					reste += splited - max;
					it.setStoredEnergy(st, max);
				} else
					it.setStoredEnergy(st, splited);
			}
		}
		if(reste > 0) {
			for(int i = 0; i < inv.size(); i++) {
				var st = inv.getStack(i);
				if(st.getItem() instanceof SimpleEnergyItem it) {
					var s = it.getStoredEnergy(st);
					var max = it.getEnergyCapacity(st);
					if(s < max) {
						var r = max - s;
						reste -= r;
						it.setStoredEnergy(st, r);
						if(reste == 0)
							break;
					}
				}
			}
		}
		inv.markDirty();
	}

}
