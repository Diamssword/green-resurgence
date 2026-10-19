package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.items.materials.Materials;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class MedicalSatchelItem extends SatchelItem {

	public MedicalSatchelItem(Settings settings, int w, int h) {
		super(settings, w, h);

	}

	@Override
	public IGridContainerRule getContainerRules() {
		return new IGridContainerRule() {
			@Override
			public Slot createSlotFor(Inventory inventory, int index, int x, int y) {
				return new Slot(inventory, index, x, y) {
					@Override
					public boolean canInsert(ItemStack stack) {
						return Materials.medicine.getItems().contains(stack.getItem());
					}
				};
			}

			@Override
			public int getQuickSlotPriority(ItemStack item) {
				return Materials.medicine.getItems().contains(item.getItem()) ? 10000 : 1;
			}
		};
	}

	@Override
	public String getGuiTexture() {
		return "medical_pouch";
	}
}
