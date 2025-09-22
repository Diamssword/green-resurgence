package com.diamssword.greenresurgence.containers.player.grids;

import com.diamssword.greenresurgence.containers.grids.GridContainer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.screen.slot.Slot;

public class PlayerGrid extends GridContainer {
	public PlayerGrid(String name, Inventory inv, int width, int height) {
		super(name, inv, width, height);
	}

	public PlayerGrid(String name, int width, int height) {
		super(name, width, height);
	}

	@Override
	public Slot createSlotFor(int index, int x, int y) {
		return new Slot(getInventory(), index, x, y) {
			@Override
			public void setStack(ItemStack stack) {
				this.setStackNoCallbacks(stack);
				if(inventory instanceof PlayerInventory pl) {pl.player.playerScreenHandler.updateToClient();}
			}
		};
	}

	public PlayerGrid(String name, Inventory inv, int width, int height, int index) {
		super(name, inv, width, height, index);
	}

	@Override
	public boolean isPlayerContainer() {
		return true;
	}

	@Override
	public int getQuickSlotPriority(ItemStack item) {
		if(this.getName().equals("hotbar")) {
			var eq = Equipment.fromStack(item);
			if((eq != null && (eq.getSlotType() == EquipmentSlot.MAINHAND || eq.getSlotType() == EquipmentSlot.OFFHAND)) || item.getItem() instanceof ToolItem) {return 10;} else {return 0;}
		} else if(this.getName().equals("player")) {return 0;}
		return 1;
		//return super.getQuickSlotPriority(item);
	}
}