package com.diamssword.greenresurgence.items.helpers;

import com.diamssword.greenresurgence.containers.FilteredInventory;
import com.diamssword.greenresurgence.items.BatteryItem;
import com.diamssword.greenresurgence.materials.BatteryTiers;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ClickType;
import net.minecraft.util.collection.DefaultedList;
import team.reborn.energy.api.base.SimpleEnergyItem;

public class BatteryStorageHelper implements SimpleEnergyItem {

	public final int maxBatteries;
	public final BatteryTiers minTier;
	public final BatteryTiers maxTier;

	public record BatteryHolderTooltipData(DefaultedList<ItemStack> stacks, BatteryTiers min, BatteryTiers max) implements TooltipData {
	}

	public BatteryStorageHelper(int maxBatteries, BatteryTiers minTier) {
		this.maxBatteries = maxBatteries;
		this.minTier = minTier;
		this.maxTier = minTier;
	}

	public BatteryStorageHelper(int maxBatteries, BatteryTiers minTier, BatteryTiers maxTier) {
		this.maxBatteries = maxBatteries;
		this.minTier = minTier;
		this.maxTier = maxTier;
	}

	public TooltipData getTooltipData(ItemStack stack) {
		return new BatteryHolderTooltipData(this.getAsInventory(stack).stacks, this.minTier, this.maxTier);
	}

	public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
		if(clickType != ClickType.RIGHT) {
			return false;
		} else {
			ItemStack itemStack = slot.getStack();
			if(itemStack.isEmpty()) {
				this.playRemoveOneSound(player);
				addToBundle(stack, slot.insertStack(removeFirstStack(stack)));
			} else if(this.isItemCompatible(itemStack)) {
				int i = getEmptySlots(this.getAsInventory(stack));
				int j = addToBundle(stack, slot.takeStackRange(itemStack.getCount(), i, player));
				if(j > 0) {
					this.playInsertSound(player);
				}
			}
			return true;
		}
	}

	public boolean isItemCompatible(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof BatteryItem bat && bat.getEnergyMaxOutput(stack) >= this.minTier.maxIO && bat.getEnergyMaxOutput(stack) <= this.maxTier.maxIO;
	}

	public SimpleInventory getAsInventory(ItemStack bundle) {
		var inv = new FilteredInventory(this.maxBatteries, (t, s) -> true).setSingleItem(true);

		inv.addListener(c -> bundle.setSubNbt("Batteries", inv.toNbtList()));
		NbtCompound nbtCompound = bundle.getOrCreateNbt();
		if(nbtCompound.contains("Batteries")) {
			NbtList nbtList = nbtCompound.getList("Batteries", NbtElement.COMPOUND_TYPE);
			inv.readNbtList(nbtList);
		}
		return inv;
	}

	public static int getEmptySlots(Inventory inv) {
		if(inv.isEmpty())
			return inv.size();
		int res = 0;
		for(int i = 0; i < inv.size(); i++) {
			if(inv.getStack(i).isEmpty())
				res++;
		}
		return res;
	}

	private int addToBundle(ItemStack bundle, ItemStack stack) {
		var inv = this.getAsInventory(bundle);
		if(this.isItemCompatible(stack)) {
			var maxInsert = Math.min(getEmptySlots(inv), stack.getCount());
			for(int i = 0; i < maxInsert; i++) {
				var c = stack.copy();
				c.setCount(1);
				inv.addStack(c);
			}
			stack.decrement(maxInsert);
			inv.markDirty();
			return maxInsert;
		} else {
			return 0;
		}
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

	private ItemStack removeFirstStack(ItemStack stack) {
		var inv = this.getAsInventory(stack);
		int sl = getMostDischargedSlot(inv);
		var res = inv.removeStack(sl);
		inv.markDirty();
		return res;
	}

	private void playRemoveOneSound(Entity entity) {
		entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
	}

	private void playInsertSound(Entity entity) {
		entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
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
		var btc = this.maxBatteries - getEmptySlots(inv);
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
