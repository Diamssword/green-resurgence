package com.diamssword.greenresurgence.gui.playerContainers;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.EquipmentScreenHandler;
import com.diamssword.greenresurgence.gui.components.BetterEntityComponent;
import com.diamssword.greenresurgence.gui.components.InventoryComponent;
import com.diamssword.greenresurgence.items.equipment.upgrades.EquipmentSkinItem;
import com.diamssword.greenresurgence.systems.equipement.EquipmentSkins;
import com.diamssword.greenresurgence.systems.equipement.Equipments;
import com.diamssword.greenresurgence.systems.equipement.IEquipmentBlueprint;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
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
		var contS = Containers.verticalFlow(Sizing.content(), Sizing.content());

		this.handler.onEquipmentReady(v -> {
			for(String slot : v.getSlots()) {
				contL.child(Components.label(Text.translatable("equipment." + GreenResurgence.ID + ".gui." + slot)));
				contS.child(new InventoryComponent("equipment_" + slot, 1, 1, "disabled"));
			}
			panel.child(contL);
			panel.child(contS);
			findInvComps(rootComponent);
		});

	}
}

