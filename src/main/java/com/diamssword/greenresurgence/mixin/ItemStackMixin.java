package com.diamssword.greenresurgence.mixin;

import com.diamssword.greenresurgence.systems.equipement.IUpgradableEquipment;
import com.diamssword.greenresurgence.systems.equipement.utils.StackEquipmentHolder;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Function;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements StackEquipmentHolder {
	@Unique
	private IUpgradableEquipment equipmentCache;

	@Unique
	private boolean dirtyStackbasedEquipment = true;

	@Override
	@Unique
	public IUpgradableEquipment green_resurgence$getEquipment(Function<ItemStack, IUpgradableEquipment> factory) {
		if(dirtyStackbasedEquipment) {
			equipmentCache = factory.apply((ItemStack) (Object) this);
			dirtyStackbasedEquipment = false;
		}
		return equipmentCache;
	}

	@Override
	@Unique
	public void green_resurgence$invalidateEquipment() {
		dirtyStackbasedEquipment = true;
	}

}