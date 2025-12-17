package com.diamssword.greenresurgence;

import com.diamssword.characters.api.CharactersApi;
import com.diamssword.greenresurgence.gui.WardrobeGui;
import com.diamssword.greenresurgence.gui.components.ComponentsRegister;
import com.diamssword.greenresurgence.gui.hud.CustomHud;
import com.diamssword.greenresurgence.items.GeckoToolEquipmentRenderer;
import com.diamssword.greenresurgence.items.ItemModelOverrideProvider;
import com.diamssword.greenresurgence.systems.equipement.EquipmentSkins;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ModelIdentifier;
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
		Keybinds.init();
		RenderersRegister.init();
		ClientNetwork.initialize();
		ClientEvents.initialize();
		ItemModelOverrideProvider.init();

		ScreenHandlers.init();
		ComponentsRegister.init();
		CustomHud.init();
		ClientLifecycleEvents.CLIENT_STARTED.register(GreenResurgenceClient::postInit);
		ModelLoadingPlugin.register(pluginContext -> {
			EquipmentSkins.skins.values().forEach(v -> {
				v.values().forEach(c -> {
					pluginContext.addModels(new ModelIdentifier(c.getVanillaPath(), "inventory"));
				});
			});
			pluginContext.addModels(new ModelIdentifier(GeckoToolEquipmentRenderer.BP_BG, "inventory"));
		});
		GreenResurgence.clientHelper = new ClientSideHelperImp();
	}

	@Environment(EnvType.CLIENT)
	private static void postInit(MinecraftClient client) {
		CharactersApi.instance.overrideWardrobeGui(WardrobeGui::new);
	}

}
