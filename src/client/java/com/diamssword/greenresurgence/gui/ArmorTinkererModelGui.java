package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.blockEntities.ArmorTinkererBlockEntity;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.ModularArmorPackets;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class ArmorTinkererModelGui extends BaseUIModelScreen<FlowLayout> {
	BlockPos tilePos;
	String model;
	EquipmentSlot slot;
	ItemStack stack;

	public ArmorTinkererModelGui(BlockPos tilePos, String model, EquipmentSlot slot) {
		super(FlowLayout.class, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource("survival/armor_tinkerer_model")));
		this.tilePos = tilePos;
		this.model = model;
		this.slot = slot;
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	protected void build(FlowLayout rootComponent) {
		rootComponent.childById(ButtonComponent.class, "back").onPress(v -> Channels.MAIN.clientHandle().send(new ModularArmorPackets.RequestGui(tilePos)));
		var lay = rootComponent.childById(FlowLayout.class, "layout");
		switch (slot) {
			case HEAD -> stack = new ItemStack(MItems.MODULAR_HEAD);
			case CHEST -> stack = new ItemStack(MItems.MODULAR_CHEST);
			case FEET -> stack = new ItemStack(MItems.MODULAR_BOOT);
			case LEGS -> stack = new ItemStack(MItems.MODULAR_LEG);
		}

		stack.getOrCreateNbt().putString("model", model);
		lay.child(Components.item(stack).sizing(Sizing.fixed(100)));
		var cont1 = Containers.verticalFlow(Sizing.fill(100), Sizing.content()).gap(2);
		var scroll = Containers.verticalScroll(Sizing.fill(50), Sizing.fill(100), cont1);
		scroll.verticalAlignment(VerticalAlignment.CENTER);
		lay.child(scroll);
		for (int i = 1; i < ModularArmorPackets.modeles.length; i += 2) {
			var c2 = Containers.horizontalFlow(Sizing.fill(100), Sizing.content()).gap(2);
			c2.horizontalAlignment(HorizontalAlignment.CENTER);
			var model = ModularArmorPackets.modeles[i];
			var model1 = ModularArmorPackets.modeles[i - 1];
			c2.child(Components.button(Text.literal(model), (d) -> setModel(model)).horizontalSizing(Sizing.fill(48)));
			c2.child(Components.button(Text.literal(model1), (d) -> setModel(model1)).horizontalSizing(Sizing.fill(48)));
			cont1.child(c2);
		}
		rootComponent.childById(ButtonComponent.class, "confirm").onPress(v -> Channels.MAIN.clientHandle().send(new ModularArmorPackets.ChangeModel(tilePos, ArmorTinkererBlockEntity.revertedArmorIndex(slot), model)));
	}

	private void setModel(String model) {
		this.model = model;
		stack.getNbt().putString("model", model);
	}
}
