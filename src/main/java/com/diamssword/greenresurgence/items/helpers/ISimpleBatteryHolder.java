package com.diamssword.greenresurgence.items.helpers;

import com.diamssword.greenresurgence.materials.BatteryTiers;
import net.minecraft.item.ItemStack;

public interface ISimpleBatteryHolder extends ISimpleEnergyItemTiered {

	BatteryStorageHelper getBatteryStorage();

	default BatteryTiers getBatteryTier(ItemStack var1) {
		return getBatteryStorage().minTier;
	}

	/**
	 * @param stack Current stack.
	 * @return The max energy that can be stored in this item stack (ignoring current stack size).
	 */
	default long getEnergyCapacity(ItemStack stack) {
		return getBatteryStorage().getEnergyCapacity(stack);
	}

	/**
	 * @param stack Current stack.
	 * @return The max amount of energy that can be inserted in this item stack (ignoring current stack size) in a single operation.
	 */
	default long getEnergyMaxInput(ItemStack stack) {
		return getBatteryStorage().getEnergyMaxInput(stack);
	}

	/**
	 * @param stack Current stack.
	 * @return The max amount of energy that can be extracted from this item stack (ignoring current stack size) in a single operation.
	 */
	default long getEnergyMaxOutput(ItemStack stack) {
		return getBatteryStorage().getEnergyMaxOutput(stack);
	}

	/**
	 * @return The energy stored in the stack. Count is ignored.
	 */
	default long getStoredEnergy(ItemStack stack) {
		return getBatteryStorage().getStoredEnergy(stack);
	}

	/**
	 * Directly set the energy stored in the stack. Count is ignored.
	 * It's up to callers to ensure that the new amount is >= 0 and <= capacity.
	 */
	default void setStoredEnergy(ItemStack stack, long newAmount) {
		getBatteryStorage().setStoredEnergy(stack, newAmount);
	}
}
