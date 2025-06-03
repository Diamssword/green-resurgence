package com.diamssword.greenresurgence.containers.grids;

import com.diamssword.greenresurgence.containers.player.grids.PlayerGrid;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class ContainerArmorGrid extends PlayerGrid {
	static final Identifier[] EMPTY_ARMOR_SLOT_TEXTURES = new Identifier[]{
			PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE
	};
	private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};


	public ContainerArmorGrid(String name, int width, int height) {
		super(name, width, height);
	}

	public ContainerArmorGrid(String name, Inventory inv, int width, int height) {
		super(name, inv, width, height, 0);
	}

	@Override
	public boolean revert() {
		return false;
	}

	@Override
	public Slot createSlotFor(int index, int x, int y) {

		return new Slot(this.getInventory(), index, x, y) {
			@Override
			public int getMaxItemCount() {
				return 1;
			}

			@Override
			public boolean canInsert(ItemStack stack) {
				return EQUIPMENT_SLOT_ORDER[index] == MobEntity.getPreferredEquipmentSlot(stack);
			}

			@Override
			public Pair<Identifier, Identifier> getBackgroundSprite() {
				return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, EMPTY_ARMOR_SLOT_TEXTURES[index]);
			}
		};
	}
}