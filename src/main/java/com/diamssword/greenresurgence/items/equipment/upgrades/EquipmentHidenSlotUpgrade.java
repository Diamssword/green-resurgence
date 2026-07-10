package com.diamssword.greenresurgence.items.equipment.upgrades;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.items.equipment.EquipmentUpgradeItem;
import com.diamssword.greenresurgence.items.helpers.HiddenStorageHelper;
import com.diamssword.greenresurgence.systems.equipement.utils.IOnUpgradeBreak;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EquipmentHidenSlotUpgrade extends EquipmentUpgradeItem implements IOnUpgradeBreak {
	private final HiddenStorageHelper storage;

	public EquipmentHidenSlotUpgrade(String allowed, String slot, int durability, float wheight, int slots) {
		super(allowed, slot, durability, wheight);

		this.storage = new HiddenStorageHelper(slots);
	}

	@Override
	public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
		if(this.storage.onStackClicked(stack, slot, clickType, player))
			return true;
		return super.onStackClicked(stack, slot, clickType, player);
	}

	@Override
	public void onUpgradeBreak(LivingEntity player, ItemStack toolStack, ItemStack upgradeStack) {
		var st = this.storage.getAsInventory(upgradeStack);
		for(int i = 0; i < st.size(); i++) {
			var st1 = st.getStack(i);
			if(!st1.isEmpty())
				player.dropStack(st1);
		}

	}

	/*
		@Override
		public Optional<TooltipData> getTooltipData(ItemStack stack) {
			return Optional.of(this.storage.getTooltipData(stack));
		}
	*/
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".upgrade.hidden_compartment", this.storage.maxItems));
	}
}
