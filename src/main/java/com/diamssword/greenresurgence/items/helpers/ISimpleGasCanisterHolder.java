package com.diamssword.greenresurgence.items.helpers;

import com.diamssword.greenresurgence.MGas;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public interface ISimpleGasCanisterHolder extends SimpleGasItem {

	GasStorageHelper getGasStorage();

	default long getGasCapacity(ItemStack stack) {
		return getGasStorage().getGasCapacity(stack);
	}

	@Override
	default Identifier getStoredGasType(ItemStack stack) {
		return getStoredGas(stack).id();
	}

	@Override
	default MGas.GasInfos getStoredGas(ItemStack stack) {
		return getGasStorage().getMainGas(stack);
	}

	@Override
	default boolean canInput(ItemStack stack) {
		return false;
	}

	@Override
	default long getStoredGasAmount(ItemStack stack) {
		return getGasStorage().getStoredGasAmount(stack);
	}

	@Override
	default boolean setStoredGas(ItemStack stack, long newAmount) {
		return false;
	}

	@Override
	default boolean setStoredGas(ItemStack stack, Identifier gas, long newAmount) {
		return false;
	}

	@Override
	default boolean tryUseGas(ItemStack stack, long amount) {
		return getGasStorage().tryConsumeGas(stack, amount);
	}
}
