package com.diamssword.greenresurgence.render.environment.vignettes;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.MItems;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VignetteItemsRegistry {
	private static Map<Item, ItemVignetteRenderer> registry = new ConcurrentHashMap<>();
	public static ItemVignetteRenderer equipped;

	public static void init() {

		registry.put(MItems.GAS_MASK, new GasMaskVignette(GreenResurgence.asRessource("textures/gui/mask_overlay.png"), GreenResurgence.asRessource("textures/particle/fog.png")));


		ClientTickEvents.END_WORLD_TICK.register((world) -> {
			var client = MinecraftClient.getInstance();
			var head = client.player.getInventory().getArmorStack(3);
			equipped = registry.get(head.getItem());
			if(equipped != null)
				equipped.tick(client);
		});
	}

}
