package com.diamssword.greenresurgence.containers;

import com.diamssword.greenresurgence.containers.grids.GridContainer;
import com.diamssword.greenresurgence.containers.grids.GridContainerSyncer;
import com.diamssword.greenresurgence.containers.grids.IGridContainer;
import com.diamssword.greenresurgence.systems.equipement.*;
import com.diamssword.greenresurgence.utils.Utils;
import io.wispforest.owo.client.screens.SyncedProperty;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EquipmentScreenHandler extends MultiInvScreenHandler {
	public Runnable onClosed;
	private IEquipmentDef[] equipments;
	private ItemStack[] toolStacks;
	private final SyncedProperty<EquipmentSync> equipmentProp;
	private Consumer<IEquipmentDef[]> equipmentListener;

	public static record EquipmentSync(String[] types, String[] subtypes) {}

	public EquipmentScreenHandler(int syncId, PlayerInventory playerInventory) {
		super(syncId, playerInventory);
		equipmentProp = this.createProperty(EquipmentSync.class, new EquipmentSync(new String[0], new String[0]));
		this.onReady(v -> {
			if(equipments != null) {
				equipmentListener.accept(equipments);
			}
		});
		equipmentProp.observe(v -> {
			equipments = new IEquipmentDef[v.types.length];
			for(int i = 0; i < v.types.length; i++) {
				equipments[i] = Equipments.getEquipment(v.types[i], v.subtypes[i]).orElse(null);
			}
			if(isReady())
				equipmentListener.accept(equipments);
		});
	}

	public void onEquipmentReady(Consumer<IEquipmentDef[]> listener) {
		this.equipmentListener = listener;
	}

	public EquipmentScreenHandler(int syncId, PlayerEntity player, IGridContainer toolContainer, SimpleInventory... upgrades) {
		super(syncId, player, toolContainer);
		var tool = toolContainer.getInventory();
		toolStacks = new ItemStack[tool.size()];
		for(int i = 0; i < tool.size(); i++) {
			toolStacks[i] = tool.getStack(i);
		}


		equipmentProp = this.createProperty(EquipmentSync.class, new EquipmentSync(new String[0], new String[0]));

		var eq = getEquipments();
		if(eq != null && eq.length > 0) {
			var t = new String[eq.length];
			var st = new String[eq.length];
			for(int i = 0; i < eq.length; i++) {
				if(eq[i] != null) {
					t[i] = eq[i].getEquipmentType();
					st[i] = eq[i].getEquipmentSubtype();
				} else
					t[i] = st[i] = "";
			}

			equipmentProp.set(new EquipmentSync(t, st));
		}
		if(upgrades != null)
			recreateSlots(upgrades);
	}

	@Override
	public void onClosed(PlayerEntity player) {
		super.onClosed(player);
		if(onClosed != null)
			onClosed.run();
	}

	public IEquipmentDef[] getEquipments() {
		if(equipments == null && toolStacks != null) {
			equipments = new IEquipmentDef[toolStacks.length];
			for(int i = 0; i < toolStacks.length; i++) {
				if(toolStacks[i].getItem() instanceof IEquipmentBlueprint bp)
					equipments[i] = bp.getEquipment();
				else if(toolStacks[i].getItem() instanceof IEquipementItem bp)
					equipments[i] = bp.getEquipment(toolStacks[i]).getEquipment();
			}


		}
		if(equipments == null)
			return new IEquipmentDef[0];
		return equipments;
	}

	protected void recreateSlots(SimpleInventory... inv) {
		var list = new ArrayList<IGridContainer>();
		IEquipmentDef[] eq = getEquipments();


		for(int j = 0; j < inv.length; j++) {
			var i = 0;
			if(eq[j] != null && inv[j] != null) {
				for(String slot : eq[j].getSlots()) {
					var off = new OffsetInventory(inv[j], i, 1);
					list.add(new UpgradeGrid(j, slot, eq[j], off, 1, 1));
					i++;
				}
			}
		}


		var ls = new ArrayList<>(List.of(inventories));
		ls.addAll(list);
		inventories = ls.toArray(new IGridContainer[0]);
		list.forEach(this::addSlotsFor);
		this.props.set(new GridContainerSyncer(this.getPos(), inventories));

	}

	@Override
	public ScreenHandlerType<? extends MultiInvScreenHandler> type() {
		return Containers.EQUIPMENT_TINKERER;
	}

	public static class UpgradeGrid extends GridContainer {
		public final IEquipmentDef equipment;
		private final String slot;

		public UpgradeGrid(int upgradeInvIndex, String slot, IEquipmentDef equipment, Inventory inv, int width, int height) {
			super(upgradeInvIndex + "_equipment_" + slot, inv, width, height);
			this.equipment = equipment;
			this.slot = slot;
		}

		@Override
		public Slot createSlotFor(int index, int x, int y) {
			return new UpgradeSlot(equipment, slot, this.getInventory(), index, x, y);
		}
	}


	public static class EquipmentSlot extends Slot {

		public EquipmentSlot(Inventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return stack.getItem() instanceof IEquipmentBlueprint || stack.getItem() instanceof IEquipementItem;
		}

		public int getMaxItemCount() {
			return 1;
		}

		public int getMaxItemCount(ItemStack stack) {
			return 1;
		}
	}

	public static class UpgradeSlot extends Slot {
		public final IEquipmentDef equipment;
		public final String slot;

		public UpgradeSlot(IEquipmentDef equipment, String slot, Inventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
			this.equipment = equipment;
			this.slot = slot;
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			if(stack.getItem() instanceof IEquipmentUpgrade up) {

				if(Utils.arrayContains(up.slots(equipment), slot)) {
					return up.canBeApplied(equipment, stack);
				}
			}
			return false;
		}


		public int getMaxItemCount() {
			return 1;
		}

		public int getMaxItemCount(ItemStack stack) {
			return 1;
		}
	}
}
