package com.diamssword.greenresurgence.systems.faction.perimeter.components;

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.List;

public class TerrainEnergyStorage extends SnapshotParticipant<Long> implements EnergyStorage {
	public long amount = 0;
	private long capacity=0;
	private List<Long> poses=new ArrayList<>();
	public TerrainEnergyStorage() {

	}
	public void addCapacity(int amount)
	{
		capacity=Math.max(0,capacity+amount);
		if(this.amount>capacity)
			this.amount=capacity;
	}
	@Override
	protected Long createSnapshot() {
		return amount;
	}

	@Override
	protected void readSnapshot(Long snapshot) {
		amount = snapshot;
	}

    @Override
	public long insert(long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notNegative(maxAmount);
		long inserted =  Math.min(maxAmount, capacity - amount);

		if (inserted > 0) {
			updateSnapshots(transaction);
			amount += inserted;
			System.out.println(inserted+";"+amount);
			return inserted;
		}
		return 0;
	}

    @Override
	public long extract(long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notNegative(maxAmount);

		long extracted = Math.min(maxAmount, amount);

		if (extracted > 0) {
			updateSnapshots(transaction);
			amount -= extracted;
			return extracted;
		}

		return 0;
	}

	@Override
	public long getAmount() {
		return amount;
	}

	@Override
	public long getCapacity() {
		return capacity;
	}
	public void toNBT(NbtCompound tag)
	{
		tag.putLong("amount",this.amount);
		tag.putLong("capacity",this.capacity);
	}
	public void fromNBT(NbtCompound tag)
	{
		this.capacity=tag.getLong("capacity");
		this.amount=tag.getLong("amount");
	}
}
