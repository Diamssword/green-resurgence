package com.diamssword.greenresurgence.containers.player.grids;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.grids.GridContainer;
import com.diamssword.greenresurgence.items.helpers.AbstractBackpackItem;
import com.diamssword.greenresurgence.systems.Components;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.screen.slot.Slot;

public class PlayerGrid extends GridContainer {
	AbstractBackpackItem.IGridContainerRule rules;

	public PlayerGrid(String name, Inventory inv, int width, int height) {
		super(name, inv, width, height);
	}

	public PlayerGrid setRules(AbstractBackpackItem.IGridContainerRule rules) {
		this.rules = rules;
		return this;
	}

	public PlayerGrid(String name, int width, int height) {
		super(name, width, height);
	}

	public static PlayerGrid unserializeWithRules(String name, int width, int height) {
		var grid = new PlayerGrid(name, width, height);
		if(GreenResurgence.clientHelper.isClient()) {
			int slot = switch(name) {
				case "backpack" -> 0;
				case "satchelLeft" -> 1;
				case "satchelRight" -> 2;
				default -> -1;
			};
			if(slot > -1) {
				var g1 = Components.PLAYER_INVENTORY.get(GreenResurgence.clientHelper.getPlayer());
				if(g1.getBag(slot).getItem() instanceof AbstractBackpackItem bi) {
					grid.setRules(bi.getContainerRules());
				}
			}
		}
		return grid;
	}

	@Override
	public Slot createSlotFor(int index, int x, int y) {
		if(rules != null)
			return rules.createSlotFor(getInventory(), index, x, y);
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
		if(rules != null)
			return rules.getQuickSlotPriority(item);
		if(this.getName().equals("hotbar")) {
			var eq = Equipment.fromStack(item);
			if((eq != null && (eq.getSlotType() == EquipmentSlot.MAINHAND || eq.getSlotType() == EquipmentSlot.OFFHAND)) || item.getItem() instanceof ToolItem) {return 10;} else {return 3;}
		} else if(this.getName().equals("player")) {return 2;}
		return 1;
		//return super.getQuickSlotPriority(item);
	}
}