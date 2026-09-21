package com.diamssword.greenresurgence.systems.faction.perimeter.components;

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import team.reborn.energy.api.EnergyStorage;

import java.util.HashMap;
import java.util.Map;

public class TerrainEnergyStorage extends SnapshotParticipant<Long> implements EnergyStorage {

	/**
	 * Right now the areas tick only every 2 seconds, so we have to twist the values a little.
	 */
	private final static int valueModifier = 2;
	public long amount = 0;
	private long capacity = 0;
	private int secondsInDeficit;
	private int producedLastSecond;
	private int consumedLastSecond;
	private boolean lastExtractFailed = false;
	private final Map<BlockPos, Integer> IOs = new HashMap<>();
	private final Map<BlockPos, Long> storages = new HashMap<>();

	public TerrainEnergyStorage() {

	}


	public void editCapacity(BlockPos pos, long amount) {
		var v = storages.get(pos);

		var mod = amount;
		if(v != null) {
			if(v == amount)
				return;
			mod -= v;
		}
		if(amount == 0)
			storages.remove(pos);
		else
			storages.put(pos, amount);
		capacity = Math.max(0, capacity + mod);
		if(this.amount > capacity)
			this.amount = capacity;
	}

	public long getIOAt(BlockPos pos) {
		return IOs.getOrDefault(pos, 0);
	}

	public int getConsumedLastSecond() {
		return consumedLastSecond;
	}

	public int getProducedLastSecond() {
		return producedLastSecond;
	}

	public int getRateLastSecond() {
		return getProducedLastSecond() - getConsumedLastSecond();
	}

	public int getSecondsInDeficit() {
		return secondsInDeficit;
	}

	public long getCapacityAt(BlockPos pos) {
		return storages.getOrDefault(pos, 0L);
	}

	public void editIOPerSecond(BlockPos pos, int amount) {
		if(amount == 0)
			IOs.remove(pos);
		else
			IOs.put(pos, amount);
	}

	public void tick() {
		lastExtractFailed = false;
		producedLastSecond = 0;
		consumedLastSecond = 0;
		var tot = 0;
		for(Integer value : IOs.values()) {
			value = value * valueModifier;
			if(value > 0)
				producedLastSecond += value;
			else
				consumedLastSecond -= value;
			tot += value;
		}
		try(var tr = Transaction.openOuter()) {
			if(tot > 0) {
				insert(tot, tr);
				secondsInDeficit = 0;
			} else if(extract(-tot, tr) < -tot) {
				if(secondsInDeficit < 1000)
					secondsInDeficit++;
				lastExtractFailed = true;
			} else if(secondsInDeficit > 0) {
				secondsInDeficit /= 2;
				if(secondsInDeficit < 10)
					secondsInDeficit = 0;
			}
			tr.commit();
		}
	}

	public int secondsInDeficit() {
		return secondsInDeficit;
	}

	public boolean isEnergyEmpty() {
		return lastExtractFailed || getAmount() <= 0;
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
		long inserted = Math.min(maxAmount, capacity - amount);

		if(inserted > 0) {
			updateSnapshots(transaction);
			amount += inserted;
			return inserted;
		}
		return 0;
	}

	@Override
	public long extract(long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notNegative(maxAmount);

		long extracted = Math.min(maxAmount, amount);
		if(extracted > 0) {
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

	public void toNBT(NbtCompound tag) {
		var ls = new NbtList();
		storages.forEach((k, v) -> {
			var mp = new NbtCompound();
			mp.putLong("pos", k.asLong());
			mp.putLong("value", v);
			ls.add(mp);
		});
		var ls1 = new NbtList();
		IOs.forEach((k, v) -> {
			var mp = new NbtCompound();
			mp.putLong("pos", k.asLong());
			mp.putInt("value", v);
			ls1.add(mp);
		});
		tag.putLong("amount", this.amount);
		tag.put("storages", ls);
		tag.put("ios", ls1);
	}

	public void fromNBT(NbtCompound tag) {
		this.amount = tag.getLong("amount");

		if(tag.contains("storages")) {
			this.storages.clear();
			var t = tag.getList("storages", NbtElement.COMPOUND_TYPE);
			for(NbtElement nbtElement : t) {
				storages.put(BlockPos.fromLong(((NbtCompound) nbtElement).getLong("pos")), ((NbtCompound) nbtElement).getLong("value"));
			}
			this.capacity = 0;
			storages.values().forEach(k -> this.capacity += k);
		}
		if(amount > capacity)
			amount = capacity;
		if(tag.contains("ios")) {
			this.IOs.clear();
			var t = tag.getList("ios", NbtElement.COMPOUND_TYPE);
			for(NbtElement nbtElement : t) {
				IOs.put(BlockPos.fromLong(((NbtCompound) nbtElement).getLong("pos")), ((NbtCompound) nbtElement).getInt("value"));
			}
		}

	}

	public void absorb(TerrainEnergyStorage energyStorage) {
		energyStorage.IOs.forEach(this::editIOPerSecond);
		energyStorage.storages.forEach(this::editCapacity);
		try(var r = Transaction.openOuter()) {
			this.insert(energyStorage.getAmount(), r);
			r.commit();
		}
	}
}
