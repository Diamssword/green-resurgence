package com.diamssword.greenresurgence.systems.equipement;

import com.diamssword.greenresurgence.items.SimpleEnergyItemTiered;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

import java.util.Optional;

public class ElectricStackBasedEquipment extends StackBasedEquipment {
	public ElectricStackBasedEquipment(String category, String subcategory, ItemStack stack) {
		super(category, subcategory, stack);
	}

	public Optional<Pair<SimpleEnergyItemTiered, ItemStack>> getBattery() {
		var st = getUpgradeItem(Equipments.P_BATTERY);
		if(st.getItem() instanceof SimpleEnergyItemTiered tiered) {
			return Optional.of(new Pair<>(tiered, st));
		}
		return Optional.empty();
	}

	@Override
	public float getDurabilityProgress() {
		return getBattery().map(pair -> {
			var max = pair.getLeft().getEnergyCapacity(pair.getRight());
			if(max <= 0)
				return 0f;
			return (pair.getLeft().getStoredEnergy(pair.getRight()) / (float) max);
		}).orElse(0f);
	}
}
