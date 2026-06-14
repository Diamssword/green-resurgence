package com.diamssword.greenresurgence.gui.playerContainers;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blockEntities.ArmorTinkererBlockEntity;
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
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public class ArmorEquipmentTinkererContainerGui extends PlayerBasedGui<ArmorTinkererBlockEntity.Container> {
	private static final EquipmentSlot[] SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
	public ItemStack stack;
	private @Nullable BetterEntityComponent<ArmorStandEntity> stackDp;

	public ArmorEquipmentTinkererContainerGui(ArmorTinkererBlockEntity.Container handler, PlayerInventory inv, Text title) {
		super(handler, "survival/armor_tinkerer");
		this.setSubScreenSize(50);
	}

	@Override
	protected void handledScreenTick() {
		super.handledScreenTick();
		if(handler.isReady() && stackDp != null) {
			var inv1 = handler.getInventory("tool_slot").getInventory();
			for(int i = 0; i < SLOTS.length; i++) {
				var s = inv1.getStack(i);
				if(s.getItem() instanceof IEquipmentBlueprint bp) {
					s = new ItemStack(bp.getEquipment().getEquipmentItem(), 1);
					var inv = handler.getInventory(i + "_equipment_" + Equipments.P_SKIN);
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
				stackDp.entity().equipStack(SLOTS[i], s);
			}
			stackDp.entity().setShowArms(true);
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
			var g = simpleGridSlotSetup(handler.getEquipments());
			panel.child(contL);
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
					if(Utils.arrayContains(eq.slots(handler.getEquipments()[0]), name.substring("0_equipment_".length())))
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
					context.drawTooltip(this.textRenderer, Text.translatable("equipment." + GreenResurgence.ID + ".gui." + name.substring("0_equipment_".length())).formatted(Formatting.GRAY, Formatting.ITALIC), x, y);
			}
		}
	}

	private static GridLayout simpleGridSlotSetup(IEquipmentDef[] equipments) {

		var grid = Containers.grid(Sizing.content(), Sizing.fixed(80), 4, 4);
		grid.padding(Insets.of(2));

		for(int i = 0; i < equipments.length; i++) {
			if(equipments[i] != null) {
				int j = 0;
				for(String slot : equipments[i].getSlots()) {
					InventoryComponent comp = new InventoryComponent(i + "_equipment_" + slot, 1, 1, "disabled");
					comp.margins(Insets.of(1));
					comp.setIcon(0, GreenResurgence.asRessource("textures/gui/slots/indicators/equipment_" + slot + ".png"));
					grid.child(comp, i, j);
					j++;
				}
			}
		}
		return grid;
	}
}

