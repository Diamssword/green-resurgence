package com.diamssword.greenresurgence.containers.player.compatibility;

import com.diamssword.greenresurgence.containers.player.CustomPlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Pair;

public class RoutedSlot extends Slot {
	private final boolean isDisplay;
	private static final ItemStack DISPLAY = Items.RED_STAINED_GLASS_PANE.getDefaultStack();

	public RoutedSlot(Inventory inventory, int index, int x, int y, boolean isDisplay) {
		super(inventory, index, x, y);
		this.isDisplay = isDisplay;
	}

	public static RoutedSlot createSlot(CustomPlayerInventory customInv, Inventory playerInv, Inventory overflow, int index, int x, int y) {
		var m = indexToInventoryIndex(customInv, overflow, index, playerInv.size());
		return new RoutedSlot(m.getLeft(), m.getRight(), x, y, m.getLeft() == overflow);
	}

	/**
	 * Map vanilla player slot to the different available inventories.
	 */
	public static Pair<Inventory, Integer> indexToInventoryIndex(CustomPlayerInventory router, Inventory overflow, int index, int vanillaSize) {
		if(index < 9) {
			if(index < CustomPlayerInventory.getHotbarSlotCount(router.getPlayer()))
				return new Pair<>(router.getHotBar(), index);
			return new Pair<>(overflow, index);
		}
		if(router.getMain().size() < vanillaSize - 9) {
			if(index - 9 < router.getMain().size()) {
				return new Pair<>(router.getMain(), index - 9);
			} else {
				var i = router.getMain().size() + 9;
				var bp = router.getBackPack();
				if(bp != null && index - i < bp.size()) {
					return new Pair<>(bp, index - i);
				} else {
					if(bp != null)
						i += bp.size();
					var st = router.getSatchelLeft();
					if(st != null && index - i < st.size()) {
						return new Pair<>(st, index - i);
					} else {
						if(st != null)
							i += st.size();
						var st1 = router.getSatchelRight();
						if(st1 != null && index - i < st1.size()) {
							return new Pair<>(st1, index - i);
						} else {
							return new Pair<>(overflow, index);
						}
					}
				}
			}
		} else
			return new Pair<>(router.getMain(), index - 9);
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return !isDisplay && super.canInsert(stack);
	}

	@Override
	public void setStackNoCallbacks(ItemStack stack) {
		if(!isDisplay)
			super.setStackNoCallbacks(stack);
	}

	@Override
	public boolean canTakeItems(PlayerEntity playerEntity) {
		return !isDisplay && super.canTakeItems(playerEntity);
	}


	public ItemStack getStack() {
		if(isDisplay)
			return DISPLAY;
		return super.getStack();
	}
}
