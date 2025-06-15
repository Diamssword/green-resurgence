package com.diamssword.greenresurgence.materials;

public enum BatteryTiers {
	BATTERY(100, 60000, 5);

	public final int maxIO;
	public final float dischargeInMinutes;
	public final long capacity;

	BatteryTiers(int maxIO, int capacity, int dischargeInMinutes) {
		this.maxIO = maxIO;
		this.capacity = capacity;
		this.dischargeInMinutes = dischargeInMinutes;
	}

	public int recommendeDischargeRate() {
		return (int) ((int) capacity / (dischargeInMinutes * 60 * 20));
	}

	public int capacity(float scaling) {
		return (int) (capacity * scaling);
	}

	public int maxIO(float scaling) {
		return (int) (maxIO * scaling);
	}
}
