package com.diamssword.greenresurgence.gui.playerContainers;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.EquipmentScreenHandler;
import com.diamssword.greenresurgence.gui.components.BetterEntityComponent;
import com.diamssword.greenresurgence.gui.components.InventoryComponent;
import com.diamssword.greenresurgence.items.equipment.upgrades.EquipmentSkinItem;
import com.diamssword.greenresurgence.systems.equipement.*;
import com.diamssword.greenresurgence.utils.Utils;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public class EquipmentTinkererContainerGui extends PlayerBasedGui<EquipmentScreenHandler> {

	public ItemStack stack;
	private @Nullable BetterEntityComponent<ItemEntity> stackDp;

	public EquipmentTinkererContainerGui(EquipmentScreenHandler handler, PlayerInventory inv, Text title) {
		super(handler, "survival/equipment_editor");
		this.setSubScreenSize(50);
	}

	@Override
	protected void handledScreenTick() {
		super.handledScreenTick();
		if(handler.isReady() && stackDp != null) {
			var s = handler.getInventory("tool_slot").getInventory().getStack(0);
			if(s.getItem() instanceof IEquipmentBlueprint bp) {
				s = new ItemStack(bp.getEquipment().getEquipmentItem(), 1);
				var inv = handler.getInventory("equipment_" + Equipments.P_SKIN);
				if(inv != null) {
					var d = inv.getInventory().getStack(0);
					if(d.getItem() instanceof EquipmentSkinItem) {
						var sk = EquipmentSkinItem.getSkin(d);
						if(!sk.isEmpty())
							s.getOrCreateNbt().putString("skin", EquipmentSkinItem.getSkin(d));
						else
							s.getOrCreateNbt().putString("skin", EquipmentSkins.getDefault(bp.getEquipment().getEquipmentItem()).orElse(""));
					} else
						s.getOrCreateNbt().putString("skin", EquipmentSkins.getDefault(bp.getEquipment().getEquipmentItem()).orElse(""));
				}

			}
			stackDp.entity().setStack(s);
		}
	}

	@Override
	protected void build(FlowLayout rootComponent) {
		super.build(rootComponent);
		var panel = rootComponent.childById(FlowLayout.class, "upgrades_panel");
		stackDp = rootComponent.childById(BetterEntityComponent.class, "stack_display");

		var contL = Containers.verticalFlow(Sizing.content(), Sizing.content());
		contL.margins(Insets.vertical(5));
		contL.gap(10);
		this.handler.onEquipmentReady(v -> {
			var g = simpleGridSlotSetup(handler.getEquipment(), v.getSlots());
			panel.child(contL);
			if(g != null)
				panel.child(g);
			findInvComps(rootComponent);
		});

	}

	@Override
	protected void drawSlotExtra(DrawContext ctx, int x, int y, Slot slot, String inventory) {
		var st = this.handler.getCursorStack();
		if(!st.isEmpty()) {
			if(st.getItem() instanceof IEquipmentUpgrade eq) {
				var name = this.handler.getInventoryForSlot(slot);
				if(name.contains("equipment_")) {
					if(Utils.arrayContains(eq.slots(handler.getEquipment()), name.replace("equipment_", "")))
						ctx.fill(x, y, x + 16, y + 16, 0x603A6218);
				}

			}
		}

	}

	@Override
	protected void drawMouseoverTooltip(DrawContext context, int x, int y) {
		if(this.handler.getCursorStack().isEmpty() && this.focusedSlot != null) {
			if(this.focusedSlot.hasStack()) {
				ItemStack itemStack = this.focusedSlot.getStack();
				context.drawTooltip(this.textRenderer, this.getTooltipFromItem(itemStack), itemStack.getTooltipData(), x, y);
			} else {
				var name = this.handler.getInventoryForSlot(this.focusedSlot);
				if(name.contains("equipment_"))
					context.drawTooltip(this.textRenderer, Text.translatable("equipment." + GreenResurgence.ID + ".gui." + name.replace("equipment_", "")).formatted(Formatting.GRAY, Formatting.ITALIC), x, y);
			}
		}
	}

	private static GridLayout simpleGridSlotSetup(IEquipmentDef equipment, String[] slots) {
		if(slots.length == 7 || slots.length == 8) {
			var b = slots.length == 7;
			var grid = Containers.grid(Sizing.content(), Sizing.content(), 4, b ? 2 : 3);
			grid.padding(Insets.of(2));
			for(int i = 0; i < slots.length; i++) {
				var slot = slots[i];
				var x = i < 4 ? 0 : (i > 6 ? 2 : 1);
				var y = 0;
				if(i != 0) {
					if(slot.endsWith("head"))
						y = 1;
					else if(slot.endsWith("binding"))
						y = 2;
					else
						y = 3;
				}
				InventoryComponent comp = new InventoryComponent("equipment_" + slot, 1, 1, "disabled");
				comp.margins(Insets.of(1));

				var texture = slot;
				if(slot.startsWith("extra"))
					texture = "skin";
				if(slot.equals(Equipments.P_HEAD))
					texture = equipment.getEquipmentType();
				comp.setIcon(0, GreenResurgence.asRessource("textures/gui/slots/indicators/equipment_" + texture + ".png"));

				grid.child(comp, y, x);
			}
			return grid;
		}
		return null;
	}
}

