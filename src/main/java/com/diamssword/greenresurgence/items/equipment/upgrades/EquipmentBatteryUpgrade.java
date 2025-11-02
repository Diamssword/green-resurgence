package com.diamssword.greenresurgence.items.equipment.upgrades;

import com.diamssword.greenresurgence.items.SimpleEnergyItemTiered;
import com.diamssword.greenresurgence.items.equipment.EquipmentUpgradeItem;
import com.diamssword.greenresurgence.materials.BatteryTiers;
import com.diamssword.greenresurgence.systems.equipement.Equipments;
import net.minecraft.item.ItemStack;

public class EquipmentBatteryUpgrade extends EquipmentUpgradeItem implements SimpleEnergyItemTiered {
	public final BatteryTiers tier;

	public EquipmentBatteryUpgrade(String allowed, BatteryTiers tier) {
		super(allowed, Equipments.P_BATTERY, -1);
		this.tier = tier;
	}

	@Override
	public long getEnergyCapacity(ItemStack itemStack) {
		return tier.capacity;
	}

	@Override
	public long getEnergyMaxInput(ItemStack itemStack) {
		return tier.maxIO;
	}

	@Override
	public long getEnergyMaxOutput(ItemStack itemStack) {
		return 0;
	}


	@Override
	public BatteryTiers getBatteryTier(ItemStack var1) {
		return tier;
	}

	public boolean isItemBarVisible(ItemStack stack) {
		return true;
	}

	@Override
	public int getItemBarStep(ItemStack stack) {

		return (int) ((this.getStoredEnergy(stack) / (float) this.getEnergyCapacity(stack)) * 13);
	}

	@Override
	public int getItemBarColor(ItemStack stack) {
		return 0xff53ccea;
	}

	@Override
	public ItemStack getDefaultStack() {
		ItemStack stack = new ItemStack(this);
		setStoredEnergy(stack, this.getEnergyCapacity(stack));
		return stack;
	}
}
