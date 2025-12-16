package com.diamssword.greenresurgence.dynamicLight;

import com.diamssword.greenresurgence.systems.equipement.utils.IFlashLightProvider;
import dev.lambdaurora.lambdynlights.api.behavior.DynamicLightBehavior;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class DynLightEventHandler {
	private static Map<PlayerEntity, DynamicLightBehavior> flashlights = new HashMap<>();

	public static void register() {
		ClientTickEvents.END_WORLD_TICK.register(DynLightEventHandler::onPlayerTick);
		//Create Compat
		if(FabricLoader.getInstance().isModLoaded("create")) {
			CreateEventHandler.register();
		}
	}

	private static void onPlayerTick(ClientWorld clientWorld) {
		clientWorld.getPlayers().forEach(p -> {
			var stack = p.getMainHandStack();
			if(stack.getItem() instanceof IFlashLightProvider fl && fl.isOn(stack))
				addPlayerLight(p, stack, fl);
			else {
				stack = p.getOffHandStack();
				if(stack.getItem() instanceof IFlashLightProvider fl && fl.isOn(stack))
					addPlayerLight(p, stack, fl);
				else if(flashlights.containsKey(p)) {
					DynamicLightsManager.INSTANCE.removeLightSource(flashlights.remove(p));
				}
			}
		});
	}

	private static void addPlayerLight(PlayerEntity player, ItemStack stack, IFlashLightProvider provider) {
		if(provider.isOn(stack)) {
			if(!flashlights.containsKey(player)) {
				var l = new FlashlightLightBehavior(player);
				DynamicLightsManager.INSTANCE.addLightSource(l);
				flashlights.put(player, l);
			}
		} else if(flashlights.containsKey(player))
			DynamicLightsManager.INSTANCE.removeLightSource(flashlights.remove(player));
	}

}
