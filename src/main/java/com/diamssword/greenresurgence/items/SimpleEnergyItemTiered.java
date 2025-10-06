package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.materials.BatteryTiers;
import net.minecraft.item.ItemStack;
import team.reborn.energy.api.base.SimpleEnergyItem;

public interface SimpleEnergyItemTiered extends SimpleEnergyItem {
	BatteryTiers getBatteryTier(ItemStack var1);


}
