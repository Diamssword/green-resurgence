package com.diamssword.greenresurgence.items.equipment.upgrades;

import com.diamssword.greenresurgence.items.equipment.EquipmentUpgradeItem;
import com.diamssword.greenresurgence.items.helpers.BatteryStorageHelper;
import com.diamssword.greenresurgence.items.helpers.ISimpleBatteryHolder;
import com.diamssword.greenresurgence.materials.BatteryTiers;
import com.diamssword.greenresurgence.systems.equipement.Equipments;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;

import java.util.Optional;

public class EquipmentBatteryUpgrade extends EquipmentUpgradeItem implements ISimpleBatteryHolder {
	private final BatteryStorageHelper battery;

	public EquipmentBatteryUpgrade(String allowed, BatteryTiers min, BatteryTiers max, int slots) {
		super(allowed, Equipments.P_BATTERY, -1);

		this.battery = new BatteryStorageHelper(slots, min, max);
	}


	@Override
	public BatteryStorageHelper getBatteryStorage() {
		return battery;
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

	@Override
	public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
		if(this.getBatteryStorage().onStackClicked(stack, slot, clickType, player))
			return true;
		return super.onStackClicked(stack, slot, clickType, player);
	}

	@Override
	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		return Optional.of(this.getBatteryStorage().getTooltipData(stack));
	}

}
