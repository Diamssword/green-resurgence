package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.blockEntities.ArmorTinkererBlockEntity;
import com.diamssword.greenresurgence.gui.components.FreeRowGridLayout;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.ModularArmorPackets;
import com.diamssword.greenresurgence.systems.armor.ArmorLoader;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;
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
		switch (slot) {
			case HEAD -> stack = new ItemStack(MItems.MODULAR_HEAD);
			case CHEST -> stack = new ItemStack(MItems.MODULAR_CHEST);
			case FEET -> stack = new ItemStack(MItems.MODULAR_BOOT);
			case LEGS -> stack = new ItemStack(MItems.MODULAR_LEG);
		}

		stack.getOrCreateNbt().putString("model", model);
		rootComponent.childById(ItemComponent.class, "item").stack(stack);
		var grid = rootComponent.childById(FreeRowGridLayout.class, "grid");
		ArmorLoader.loader.getModels().forEach((k, v) -> {
			if (ArmorLoader.isSLotValidFor(v, slot))
				grid.child(Components.button(Text.literal(v.model()), (d) -> setModel(k)).horizontalSizing(Sizing.fill(48)));
		});

		rootComponent.childById(ButtonComponent.class, "confirm").onPress(v -> Channels.MAIN.clientHandle().send(new ModularArmorPackets.ChangeModel(tilePos, ArmorTinkererBlockEntity.revertedArmorIndex(slot), model)));
	}

	private void setModel(String model) {
		this.model = model;
		stack.getNbt().putString("model", model);
	}
}
