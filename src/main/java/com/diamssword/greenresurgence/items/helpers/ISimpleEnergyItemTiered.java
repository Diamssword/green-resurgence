package com.diamssword.greenresurgence.items.helpers;

import com.diamssword.greenresurgence.materials.BatteryTiers;
import net.minecraft.item.ItemStack;
import team.reborn.energy.api.base.SimpleEnergyItem;

public interface ISimpleEnergyItemTiered extends SimpleEnergyItem {
	BatteryTiers getBatteryTier(ItemStack stack);

}
