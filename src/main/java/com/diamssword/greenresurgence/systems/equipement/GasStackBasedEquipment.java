package com.diamssword.greenresurgence.systems.equipement;

import com.diamssword.greenresurgence.items.equipment.EquipmentToolGas;
import com.diamssword.greenresurgence.items.helpers.ISimpleGasCanisterHolder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public class GasStackBasedEquipment extends StackBasedEquipment {
	public GasStackBasedEquipment(String category, String subcategory, ItemStack stack) {
		super(category, subcategory, stack);
	}

	public GasStackBasedEquipment(String category, String subcategory, ItemStack stack, Map<String, EffectLevel> upgrades) {
		super(category, subcategory, stack, upgrades);
	}

	@Override
	protected UpgradeActionContext createContext(@Nullable LivingEntity player, @Nullable LivingEntity target, UpgradeActionContext.ItemContext context, AdvEquipmentSlot slot) {
		var b = super.createContext(player, target, context, slot);
		if(stack.getItem() instanceof EquipmentToolGas pe)
			b.setPowered(pe.isActivated(stack));
		return b;
	}

	public Optional<Pair<ISimpleGasCanisterHolder, ItemStack>> getCanister() {
		var st = getUpgradeItem(Equipments.P_GAS);
		if(st.getItem() instanceof ISimpleGasCanisterHolder tiered) {
			return Optional.of(new Pair<>(tiered, st));
		}
		return Optional.empty();
	}

	public float getContainerProgress() {
		return getCanister().map(pair -> {
			var max = pair.getLeft().getGasCapacity(pair.getRight());
			if(max <= 0)
				return 0f;
			return (pair.getLeft().getStoredGasAmount(pair.getRight()) / (float) max);
		}).orElse(0f);
	}
}
