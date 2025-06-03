package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.blockEntities.ArmorTinkererBlockEntity;
import com.diamssword.greenresurgence.items.ModularArmorItem;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class ArmorTinkererContainerGui extends PlayerBasedGui<ArmorTinkererBlockEntity.Container> {
	private final Text title;

	public ArmorTinkererContainerGui(ArmorTinkererBlockEntity.Container handler, PlayerInventory inv, Text title) {
		super(handler, "survival/armor_tinkerer");
		this.title = title;

	}

	@Override
	protected void build(FlowLayout rootComponent) {
		super.build(rootComponent);
		var cont = rootComponent.childById(FlowLayout.class, "menu");
		if (cont != null) {
			for (var d : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
				cont.child(Components.button(Text.literal("Apparence"), (v) -> {
					var stack = handler.getInventory("armor_tinkerer").getInventory().getStack(ArmorTinkererBlockEntity.revertedArmorIndex(d));
					if (stack.getItem() instanceof ModularArmorItem)
						client.setScreen(new ArmorTinkererModelGui(handler.getPos(), stack.getOrCreateNbt().getString("model"), d));
				}).verticalSizing(Sizing.fixed(16)).margins(Insets.of(1)));
			}

			/*for (var s : ModularArmorPackets.modeles) {
				cont.child(Components.button(Text.literal(s), (t) -> {
					Channels.MAIN.clientHandle().send(new ModularArmorPackets.ChangeModel(handler.getPos(), 0, s));
					Channels.MAIN.clientHandle().send(new ModularArmorPackets.ChangeModel(handler.getPos(), 1, s));
					Channels.MAIN.clientHandle().send(new ModularArmorPackets.ChangeModel(handler.getPos(), 2, s));
					Channels.MAIN.clientHandle().send(new ModularArmorPackets.ChangeModel(handler.getPos(), 3, s));
				}));
			}*/

		}
	}
}
