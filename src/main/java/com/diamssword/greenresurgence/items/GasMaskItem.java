package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.items.helpers.DurabilityStorageHelper;
import com.diamssword.greenresurgence.items.helpers.IContaminationMitigator;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.Optional;

public class GasMaskItem extends Item implements Equipment, IContaminationMitigator {

	public final DurabilityStorageHelper tank = new DurabilityStorageHelper(2, MItems.AIR_FILTER);

	public GasMaskItem(Settings settings) {
		super(settings);
	}

	@Override
	public EquipmentSlot getSlotType() {
		return EquipmentSlot.HEAD;
	}

	@Override
	public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
		if(tank.onStackClicked(stack, slot, clickType, player))
			return true;
		return super.onStackClicked(stack, slot, clickType, player);
	}

	public boolean isFiltering(ItemStack stack) {
		return tank.getDurabilityPercent(stack) > 0f;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		if(slot == 3 && world instanceof ServerWorld sw) {
			if(world.getTime() % 80 == 0) {
				boolean flg = false;
				int i = 0;
				for(ItemStack st : entity.getArmorItems()) {
					if(i == 3 && st == stack) {
						flg = true;
						break;
					}
					i++;
				}
				if(flg) {
					this.tank.tryConsumeDurability(stack, 2);
				}

			}
		}

	}

	@Override
	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		return Optional.of(this.tank.getTooltipData(stack));
	}

	@Override
	public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
		return false;
	}

	@Override
	public boolean isItemBarVisible(ItemStack stack) {
		return true;
	}

	@Override
	public int getItemBarStep(ItemStack stack) {

		return (int) (this.tank.getDurabilityPercent(stack) * 13);
	}

	@Override
	public int getItemBarColor(ItemStack stack) {
		return 0x8f009da5;
	}

	@Override
	public float getContaminationMultiplicator(ItemStack stack, double amount) {
		return isFiltering(stack) ? 0.3f : 1f;
	}
}
