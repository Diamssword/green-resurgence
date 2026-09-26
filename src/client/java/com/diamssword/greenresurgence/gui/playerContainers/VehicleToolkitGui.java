package com.diamssword.greenresurgence.gui.playerContainers;

import com.diamssword.greenresurgence.items.VehicleToolKit;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class VehicleToolkitGui extends PlayerBasedGui<VehicleToolKit.ScreenHandler> {
	public VehicleToolkitGui(VehicleToolKit.ScreenHandler handler, PlayerInventory inv, Text title) {
		super(handler, "survival/vehicle_toolkit");
		handler.onReady((h) -> {
			if(h.getInventory("dye") == null)
				this.uiAdapter.rootComponent.childById(FlowLayout.class, "dyeC").remove();
			if(h.getInventory("chest") == null)
				this.uiAdapter.rootComponent.childById(FlowLayout.class, "chestC").remove();
		});
	}


	@Override
	protected void build(FlowLayout rootComponent) {
		super.build(rootComponent);
	}

	@Override
	protected void drawBackground(DrawContext var1, float var2, int var3, int var4) {

	}
}
