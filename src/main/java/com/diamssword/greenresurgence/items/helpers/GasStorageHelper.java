package com.diamssword.greenresurgence.items.helpers;

import com.diamssword.greenresurgence.MGas;
import com.diamssword.greenresurgence.items.GasTankItem;
import net.minecraft.client.item.TooltipData;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GasStorageHelper extends ItemStorageHelper {

	public final List<Identifier> allowedGas;


	public record GasHolderTooltipData(DefaultedList<ItemStack> stacks, MGas.GasInfos gas, long capacity, long max, Identifier... allowedGas) implements TooltipData {
	}

	public GasStorageHelper(int maxTanks, Identifier... allowedGas) {
		super(maxTanks);
		this.allowedGas = List.of(allowedGas);
	}

	public GasStorageHelper(int maxTanks) {
		super(maxTanks);
		this.allowedGas = List.of();
	}

	@Override
	public TooltipData getTooltipData(ItemStack stack) {
		return new GasHolderTooltipData(this.getAsInventory(stack).stacks, getMainGas(stack), getGasCapacity(stack), getStoredGasAmount(stack), this.allowedGas.toArray(new Identifier[0]));
	}

	@Override
	public boolean isItemCompatible(ItemStack container, ItemStack stack) {
		var gas = getMainGas(container);
		if(stack.getItem() instanceof GasTankItem it) {
			return !stack.isEmpty() && stack.getItem() instanceof GasTankItem bat && (gas == MGas.EMPTY || gas == it.getStoredGas(stack)) && (allowedGas.isEmpty() || allowedGas.contains(bat.getStoredGasType(stack)));
		}
		return false;
	}

	public static int getMostDischargedSlot(Inventory inv) {
		int index = 0;
		long energy = Long.MAX_VALUE;
		for(int i = 0; i < inv.size(); i++) {
			var st = inv.getStack(i);
			if(st.getItem() instanceof SimpleGasItem ti) {
				var l = ti.getStoredGasAmount(st);
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

	@Nullable
	public MGas.GasInfos getMainGas(ItemStack stack) {
		var inv = this.getAsInventory(stack);
		for(int i = 0; i < inv.size(); i++) {
			var st = inv.getStack(i);
			if(st.getItem() instanceof SimpleGasItem it) {
				var str = it.getStoredGas(st);
				if(str != MGas.EMPTY)
					return str;
			}
		}
		return MGas.EMPTY;
	}

	public long getGasCapacity(ItemStack stack) {
		var inv = this.getAsInventory(stack);
		long cap = 0;
		for(int i = 0; i < inv.size(); i++) {
			var st = inv.getStack(i);
			if(st.getItem() instanceof SimpleGasItem it) {
				cap += it.getGasCapacity(st);
			}
		}
		return cap;
	}

	public long getStoredGasAmount(ItemStack stack) {
		var inv = this.getAsInventory(stack);
		long cap = 0;
		for(int i = 0; i < inv.size(); i++) {
			var st = inv.getStack(i);
			if(st.getItem() instanceof SimpleGasItem it) {
				cap += it.getStoredGasAmount(st);
			}
		}
		return cap;
	}

	public boolean tryConsumeGas(ItemStack stack, long amount) {
		var inv = this.getAsInventory(stack);
		var tot = getStoredGasAmount(stack);
		var btc = this.maxItems - getEmptySlots(inv);
		if(btc == 0)
			return false;
		var res = tot - amount;
		if(res < 0)
			return false;
		var splited = res / btc;
		for(int i = 0; i < inv.size(); i++) {
			var st = inv.getStack(i);
			if(st.getItem() instanceof SimpleGasItem it) {
				it.setStoredGas(st, splited);
			}
		}
		inv.markDirty();
		return true;
	}


}
