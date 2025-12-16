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
	private IEquipmentDef equipment;
	private ItemStack toolStack;
	private SyncedProperty<EquipmentSync> equipmentProp;
	private Consumer<IEquipmentDef> equipmentListener;

	public static record EquipmentSync(String type, String subtype) {}

	public EquipmentScreenHandler(int syncId, PlayerInventory playerInventory) {
		super(syncId, playerInventory);
		equipmentProp = this.createProperty(EquipmentSync.class, new EquipmentSync("", ""));
		this.onReady(v -> {
			if(equipment != null) {
				equipmentListener.accept(equipment);
			}
		});
		equipmentProp.observe(v -> {
			if(!v.subtype.isEmpty() && !v.type.isEmpty()) {
				equipment = Equipments.getEquipment(v.type, v.subtype).orElse(null);
				if(isReady())
					equipmentListener.accept(equipment);
			}
		});
	}

	public void onEquipmentReady(Consumer<IEquipmentDef> listener) {
		this.equipmentListener = listener;
	}

	public EquipmentScreenHandler(int syncId, PlayerEntity player, SimpleInventory tool, SimpleInventory upgrades) {
		super(syncId, player, new EquipmentGrid(tool));
		toolStack = tool.getStack(0);


		equipmentProp = this.createProperty(EquipmentSync.class, new EquipmentSync("", ""));

		var eq = getEquipment();
		if(eq != null)
			equipmentProp.set(new EquipmentSync(eq.getEquipmentType(), eq.getEquipmentSubtype()));
		recreateSlots(upgrades);
	}

	@Override
	public void onClosed(PlayerEntity player) {
		super.onClosed(player);
		if(onClosed != null)
			onClosed.run();
	}

	public IEquipmentDef getEquipment() {
		if(equipment == null && toolStack != null) {
			if(toolStack.getItem() instanceof IEquipmentBlueprint bp)
				equipment = bp.getEquipment();
			else if(toolStack.getItem() instanceof IEquipementItem bp)
				equipment = bp.getEquipment(toolStack).getEquipment();
		}
		return equipment;
	}

	protected void recreateSlots(SimpleInventory inv) {
		var list = new ArrayList<IGridContainer>();
		IEquipmentDef eq = getEquipment();

		if(eq != null) {
			var i = 0;
			for(String slot : eq.getSlots()) {
				var off = new OffsetInventory(inv, i, 1);
				list.add(new UpgradeGrid(slot, eq, off, 1, 1));

				i++;
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

		public UpgradeGrid(String slot, IEquipmentDef equipment, Inventory inv, int width, int height) {
			super("equipment_" + slot, inv, width, height);
			this.equipment = equipment;
			this.slot = slot;
		}

		@Override
		public Slot createSlotFor(int index, int x, int y) {
			return new UpgradeSlot(equipment, slot, this.getInventory(), index, x, y);
		}
	}

	public static class EquipmentGrid extends GridContainer {

		public EquipmentGrid(Inventory inv) {
			super("tool_slot", inv, 1, 1);
		}

		@Override
		public Slot createSlotFor(int index, int x, int y) {
			return new EquipmentSlot(this.getInventory(), index, x, y);
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
