package com.diamssword.greenresurgence.items.helpers;

import com.diamssword.greenresurgence.MGas;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;

public interface SimpleGasItem {
	String GAS_KEY = "gas";

	/**
	 * @param stack Current stack.
	 * @return The max gas that can be stored in this item stack (ignoring current stack size).
	 */
	long getGasCapacity(ItemStack stack);

	default Identifier getStoredGasType(ItemStack stack) {
		return getStoredGasTypeUnchecked(stack);
	}

	default MGas.GasInfos getStoredGas(ItemStack stack) {
		return MGas.getGas(getStoredGasType(stack));
	}

	/**
	 * @param stack Current stack.
	 * @return true if gas can be inserted in this item
	 */
	default boolean canInput(ItemStack stack) {
		return true;
	}

	/**
	 * @param stack Current stack.
	 * @return true if gas can be extracted from this item
	 */
	default boolean canOutput(ItemStack stack) {
		return true;
	}

	/**
	 * @return The gas amount stored in the stack. Count is ignored.
	 */
	default long getStoredGasAmount(ItemStack stack) {
		return getStoredGasAmountUnchecked(stack);
	}

	/**
	 * Directly set the gas stored in the stack. Count is ignored.
	 * It's up to callers to ensure that the new amount is >= 0 and <= capacity.
	 */
	default void setStoredGas(ItemStack stack, long newAmount) {
		setStoredGasUnchecked(stack, getStoredGasType(stack), newAmount);
	}

	/**
	 * Directly set the gas stored in the stack. Count is ignored.
	 * It's up to callers to ensure that the new amount is >= 0 and <= capacity.
	 */
	default void setStoredGas(ItemStack stack, Identifier gas, long newAmount) {
		setStoredGasUnchecked(stack, gas, newAmount);
	}

	/**
	 * Try to use exactly {@code amount} gas if there is enough available and return true if successful,
	 * otherwise do nothing and return false.
	 *
	 * @throws IllegalArgumentException If the count of the stack is not exactly 1!
	 */
	default boolean tryUseGas(ItemStack stack, long amount) {
		if(stack.getCount() != 1) {
			throw new IllegalArgumentException("Invalid count: " + stack.getCount());
		}

		long newAmount = getStoredGasAmount(stack) - amount;

		if(newAmount < 0) {
			return false;
		} else {
			setStoredGas(stack, newAmount);
			return true;
		}
	}

	static Identifier getStoredGasTypeUnchecked(ItemStack stack) {
		return getStoredGasTypeUnchecked(stack.getNbt());
	}

	/**
	 * @return The currently stored energy, ignoring the count and without checking the current item.
	 */
	static long getStoredGasAmountUnchecked(ItemStack stack) {
		return getStoredGasAmountUnchecked(stack.getNbt());
	}

	/**
	 * @return The currently stored energy of this raw tag.
	 */
	static Identifier getStoredGasTypeUnchecked(@Nullable NbtCompound nbt) {
		if(nbt != null) {
			var key = nbt.getCompound(GAS_KEY);
			if(key != null)
				return new Identifier(key.getString("type"));
		}
		return MGas.EMPTY.id();
	}

	static long getStoredGasAmountUnchecked(@Nullable NbtCompound nbt) {
		if(nbt != null) {
			var key = nbt.getCompound(GAS_KEY);
			if(key != null)
				return key.getLong("amount");
		}
		return 0;
	}

	/**
	 * Set the energy, ignoring the count and without checking the current item.
	 */
	static void setStoredGasUnchecked(ItemStack stack, Identifier gas, long newAmount) {
		if(newAmount == 0) {
			// Make sure newly crafted energy containers stack with emptied ones.
			stack.removeSubNbt(GAS_KEY);
		} else {
			var nb = stack.getOrCreateSubNbt(GAS_KEY);
			nb.putLong("amount", newAmount);
			nb.putString("type", gas.toString());
		}
	}
}