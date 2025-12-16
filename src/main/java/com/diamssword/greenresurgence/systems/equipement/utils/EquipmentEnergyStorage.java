package com.diamssword.greenresurgence.systems.equipement.utils;

import com.diamssword.greenresurgence.items.SimpleEnergyItemTiered;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.DelegatingEnergyStorage;

public class EquipmentEnergyStorage implements EnergyStorage {
	public static EnergyStorage createEquipmentStorage(ContainerItemContext ctx, ItemStack stack) {
		if(stack.getItem() instanceof SimpleEnergyItemTiered tiered) {
			var cap = tiered.getEnergyCapacity(stack);
			StoragePreconditions.notNegative(cap);
			var maxI = tiered.getEnergyMaxInput(stack);
			StoragePreconditions.notNegative(maxI);
			var maxO = tiered.getEnergyMaxOutput(stack);
			StoragePreconditions.notNegative(maxO);

			Item startingItem = ctx.getItemVariant().getItem();

			return new DelegatingEnergyStorage(
					new EquipmentEnergyStorage(ctx, cap, maxI, maxO),
					() -> ctx.getItemVariant().isOf(startingItem) && ctx.getAmount() > 0
			);
		} else
			throw new IllegalArgumentException("Item is not a  SimpleEnergyItemTiered: " + stack.getItem());

	}

	private final ContainerItemContext ctx;
	private final long capacity;
	private final long maxInsert, maxExtract;

	private EquipmentEnergyStorage(ContainerItemContext ctx, long capacity, long maxInsert, long maxExtract) {
		this.ctx = ctx;
		this.capacity = capacity;
		this.maxInsert = maxInsert;
		this.maxExtract = maxExtract;
	}

	/**
	 * Try to set the energy of the stack to {@code energyAmountPerCount}, return true if success.
	 */
	private boolean trySetEnergy(long energyAmountPerCount, long count, TransactionContext transaction) {

		ItemStack newStack = ctx.getItemVariant().toStack();

		SimpleEnergyItemTiered tiered = (SimpleEnergyItemTiered) newStack.getItem();
		tiered.setStoredEnergy(newStack, energyAmountPerCount);
		ItemVariant newVariant = ItemVariant.of(newStack);

		// Try to convert exactly `count` items.
		try(Transaction nested = transaction.openNested()) {
			if(ctx.extract(ctx.getItemVariant(), count, nested) == count && ctx.insert(newVariant, count, nested) == count) {
				nested.commit();
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean supportsInsertion() {
		return maxInsert > 0;
	}

	@Override
	public long insert(long maxAmount, TransactionContext transaction) {
		long count = ctx.getAmount();

		long maxAmountPerCount = maxAmount / count;
		long currentAmountPerCount = getAmount() / count;
		long insertedPerCount = Math.min(maxInsert, Math.min(maxAmountPerCount, capacity - currentAmountPerCount));

		if(insertedPerCount > 0) {
			if(trySetEnergy(currentAmountPerCount + insertedPerCount, count, transaction)) {
				return insertedPerCount * count;
			}
		}

		return 0;
	}

	@Override
	public boolean supportsExtraction() {
		return maxExtract > 0;
	}

	@Override
	public long extract(long maxAmount, TransactionContext transaction) {
		long count = ctx.getAmount();

		long maxAmountPerCount = maxAmount / count;
		long currentAmountPerCount = getAmount() / count;
		long extractedPerCount = Math.min(maxExtract, Math.min(maxAmountPerCount, currentAmountPerCount));

		if(extractedPerCount > 0) {
			if(trySetEnergy(currentAmountPerCount - extractedPerCount, count, transaction)) {
				return extractedPerCount * count;
			}
		}

		return 0;
	}

	@Override
	public long getAmount() {

		ItemStack newStack = ctx.getItemVariant().toStack();

		SimpleEnergyItemTiered tiered = (SimpleEnergyItemTiered) newStack.getItem();
		var stored = tiered.getStoredEnergy(newStack);
		return ctx.getAmount() * stored;
	}

	@Override
	public long getCapacity() {
		return ctx.getAmount() * capacity;
	}
}
