package com.diamssword.greenresurgence.systems.equipement;

import com.diamssword.greenresurgence.systems.equipement.utils.StackEquipmentHolder;
import net.minecraft.item.ItemStack;

@FunctionalInterface
public interface IEquipementItem {
	default IUpgradableEquipment getEquipment(ItemStack stack) {
		return ((StackEquipmentHolder) (Object) stack).green_resurgence$getEquipment(this::createEquipmentInstance);
	}

	IUpgradableEquipment createEquipmentInstance(ItemStack stack);
}
