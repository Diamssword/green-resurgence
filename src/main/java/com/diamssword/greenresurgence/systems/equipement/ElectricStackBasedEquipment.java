package com.diamssword.greenresurgence.systems.equipement;

import com.diamssword.greenresurgence.items.equipment.EquipmentToolElectric;
import com.diamssword.greenresurgence.items.helpers.ISimpleBatteryHolder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public class ElectricStackBasedEquipment extends StackBasedEquipment {
	public ElectricStackBasedEquipment(String category, String subcategory, ItemStack stack) {
		super(category, subcategory, stack);
	}

	public ElectricStackBasedEquipment(String category, String subcategory, ItemStack stack, Map<String, EffectLevel> upgrades) {
		super(category, subcategory, stack, upgrades);
	}

	@Override
	protected UpgradeActionContext createContext(@Nullable LivingEntity player, @Nullable LivingEntity target, UpgradeActionContext.ItemContext context, AdvEquipmentSlot slot) {
		var b = super.createContext(player, target, context, slot);
		if(stack.getItem() instanceof EquipmentToolElectric pe)
			b.setPowered(pe.isActivated(stack));
		return b;
	}

	public Optional<Pair<ISimpleBatteryHolder, ItemStack>> getBattery() {
		var st = getUpgradeItem(Equipments.P_BATTERY);
		if(st.getItem() instanceof ISimpleBatteryHolder tiered) {
			return Optional.of(new Pair<>(tiered, st));
		}
		return Optional.empty();
	}

	public float getBatteryProgress() {
		return getBattery().map(pair -> {
			var max = pair.getLeft().getEnergyCapacity(pair.getRight());
			if(max <= 0)
				return 0f;
			return (pair.getLeft().getStoredEnergy(pair.getRight()) / (float) max);
		}).orElse(0f);
	}
}
