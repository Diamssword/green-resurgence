package com.diamssword.greenresurgence.systems.equipement.utils;

import com.diamssword.greenresurgence.systems.equipement.IUpgradableEquipment;
import net.minecraft.item.ItemStack;

import java.util.function.Function;

public interface StackEquipmentHolder {
	public IUpgradableEquipment green_resurgence$getEquipment(Function<ItemStack, IUpgradableEquipment> factory);

	public void green_resurgence$invalidateEquipment();
}
