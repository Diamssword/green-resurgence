package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.materials.BatteryTiers;
import team.reborn.energy.api.base.SimpleEnergyItem;

public interface SimpleEnergyItemTiered extends SimpleEnergyItem {
	BatteryTiers getBatteryTier();


}
