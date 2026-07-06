package com.diamssword.greenresurgence.items.helpers;

import com.diamssword.greenresurgence.containers.FilteredInventory;
import com.diamssword.greenresurgence.items.materials.BatteryTiers;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ClickType;
import net.minecraft.util.collection.DefaultedList;

public class ItemStorageHelper {

	public int maxItems;

	public record BatteryHolderTooltipData(DefaultedList<ItemStack> stacks, BatteryTiers min, BatteryTiers max) implements TooltipData {
	}

	public ItemStorageHelper(int maxItems) {
		this.maxItems = maxItems;
	}

	public record BaseItemStorageTooltip(DefaultedList<ItemStack> stacks) implements TooltipData {
	}

	public TooltipData getTooltipData(ItemStack stack) {
		return new ItemStorageHelper.BaseItemStorageTooltip(this.getAsInventory(stack).stacks);
	}

	public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
		if(clickType != ClickType.RIGHT) {
			return false;
		} else {
			ItemStack itemStack = slot.getStack();
			if(itemStack.isEmpty()) {
				this.playRemoveOneSound(player);
				addToBundle(stack, slot.insertStack(removeFirstStack(stack)));
			} else if(this.isItemCompatible(stack, itemStack)) {
				int i = getEmptySlots(this.getAsInventory(stack));
				int j = addToBundle(stack, slot.takeStackRange(itemStack.getCount(), i, player));
				if(j > 0) {
					this.playInsertSound(player);
				}
			}
			return true;
		}
	}

	public boolean isItemCompatible(ItemStack container, ItemStack stack) {
		return true;
	}

	public SimpleInventory getAsInventory(ItemStack bundle) {
		var inv = new FilteredInventory(this.maxItems, (t, s) -> true).setSingleItem(true);
		inv.addListener(c -> bundle.setSubNbt("Sotrage", inv.toNbtList()));
		NbtCompound nbtCompound = bundle.getOrCreateNbt();
		if(nbtCompound.contains("Sotrage")) {
			NbtList nbtList = nbtCompound.getList("Sotrage", NbtElement.COMPOUND_TYPE);
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
		if(this.isItemCompatible(bundle, stack)) {
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

	public static int getSlotToEmpty(Inventory inv) {
		for(int i = 0; i < inv.size(); i++) {
			var st = inv.getStack(i);
			if(st.getItem() != Items.AIR) {
				return i;
			}
		}
		return 0;
	}

	protected ItemStack removeFirstStack(ItemStack stack) {
		var inv = this.getAsInventory(stack);
		int sl = getSlotToEmpty(inv);
		var res = inv.removeStack(sl);
		inv.markDirty();
		return res;
	}

	protected void playRemoveOneSound(Entity entity) {
		entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
	}

	protected void playInsertSound(Entity entity) {
		entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
	}


}
