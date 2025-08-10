package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.gui.ScreenHandlers;
import com.diamssword.greenresurgence.gui.components.ComponentsRegister;
import com.diamssword.greenresurgence.gui.hud.CustomHud;
import com.diamssword.greenresurgence.items.ItemModelOverrideProvider;
import com.diamssword.greenresurgence.render.Entities;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.item.Item;

public class GreenResurgenceClient implements ClientModInitializer {
	public static Item[] MARKER_ITEMS = {
			MBlocks.SHADOW_BLOCk.asItem(),
			MBlocks.ITEM_BLOCK.asItem(),
			MBlocks.IMAGE_BLOCK.asItem(),
			MBlocks.LOOT_ITEM_BLOCK.asItem(),
	};

	@Override
	public void onInitializeClient() {
		Entities.init();
		Keybinds.init();
		RenderersRegister.init();
		ClientNetwork.initialize();
		ClientEvents.initialize();
		ItemModelOverrideProvider.init();

		ScreenHandlers.init();
		ComponentsRegister.init();
		CustomHud.init();
	}

}
