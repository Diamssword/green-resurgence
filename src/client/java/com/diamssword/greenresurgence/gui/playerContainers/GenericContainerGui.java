package com.diamssword.greenresurgence.gui.playerContainers;

import com.diamssword.greenresurgence.containers.GenericContainer;
import com.diamssword.greenresurgence.gui.components.InventoryComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class GenericContainerGui extends PlayerBasedGui<GenericContainer> {
	private final Text title;

	public GenericContainerGui(GenericContainer handler, PlayerInventory inv, Text title) {
		super(handler, "survival/generic_container");
		this.title = title;

	}

	@Override
	protected void build(FlowLayout rootComponent) {
		super.build(rootComponent);
		var cont = rootComponent.childById(InventoryComponent.class, "container");
		if(cont != null) {
			cont.customName = title;
		}
	}

	@Override
	protected void drawBackground(DrawContext var1, float var2, int var3, int var4) {

	}
}
