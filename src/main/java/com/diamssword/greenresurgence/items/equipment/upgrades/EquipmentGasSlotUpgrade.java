package com.diamssword.greenresurgence.items.equipment.upgrades;

import com.diamssword.greenresurgence.items.helpers.GasStorageHelper;
import com.diamssword.greenresurgence.items.helpers.ISimpleGasCanisterHolder;
import com.diamssword.greenresurgence.systems.equipement.Equipments;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class EquipmentGasSlotUpgrade extends EquipmentUpgradeItem implements ISimpleGasCanisterHolder {
	private final GasStorageHelper battery;

	public EquipmentGasSlotUpgrade(String allowed, int slots, Identifier... allowedGas) {
		super(allowed, Equipments.P_GAS, -1);

		this.battery = new GasStorageHelper(slots, allowedGas);
	}


	@Override
	public GasStorageHelper getGasStorage() {
		return battery;
	}

	public boolean isItemBarVisible(ItemStack stack) {
		return true;
	}

	@Override
	public int getItemBarStep(ItemStack stack) {

		return (int) ((this.getStoredGasAmount(stack) / (float) this.getGasCapacity(stack)) * 13);
	}

	@Override
	public int getItemBarColor(ItemStack stack) {
		return getGasStorage().getMainGas(stack).color();
	}

	@Override
	public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
		if(this.getGasStorage().onStackClicked(stack, slot, clickType, player))
			return true;
		return super.onStackClicked(stack, slot, clickType, player);
	}

	@Override
	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		return Optional.of(this.getGasStorage().getTooltipData(stack));
	}

}
