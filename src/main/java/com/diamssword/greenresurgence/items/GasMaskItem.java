package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.items.helpers.DurabilityStorageHelper;
import com.diamssword.greenresurgence.systems.attributs.Attributes;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
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
import java.util.UUID;

public class GasMaskItem extends Item implements Equipment, FabricItem {

	public final DurabilityStorageHelper tank = new DurabilityStorageHelper(2, MItems.AIR_FILTER);
	private static final UUID uuid = UUID.fromString("6f96635f-d58c-4383-a06e-752821523c0b");
	private Multimap<EntityAttribute, EntityAttributeModifier> map;

	public GasMaskItem(Settings settings) {
		super(settings);
		ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.CONTAMINATION_REDUCTION, new EntityAttributeModifier(uuid, "Contamination modifier", 30f, EntityAttributeModifier.Operation.ADDITION));
		map = builder.build();
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

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
		return slot == EquipmentSlot.HEAD && isFiltering(stack) ? map : ImmutableMultimap.of();
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

}
