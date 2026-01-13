package com.diamssword.greenresurgence.dynamicLight;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.equipement.utils.IFlashLightProvider;
import dev.lambdaurora.lambdynlights.api.behavior.DynamicLightBehavior;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynLightEventHandler {
	private static final Map<Entity, DynamicLightBehavior> flashlights = new HashMap<>();

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
			if(stack.getItem() instanceof IFlashLightProvider fl && fl.isLightOn(p, stack))
				addEntityLight(p, stack, fl);
			else {
				stack = p.getOffHandStack();
				if(stack.getItem() instanceof IFlashLightProvider fl && fl.isLightOn(p, stack))
					addEntityLight(p, stack, fl);
				else if(flashlights.containsKey(p)) {
					DynamicLightsManager.INSTANCE.removeLightSource(flashlights.remove(p));
				}
			}
		});
		var player = GreenResurgence.clientHelper.getPlayer();
		if(player != null) {
			clientWorld.getEntitiesByClass(Entity.class, Box.from(player.getPos()).expand(128), (e) -> e instanceof IFlashLightProvider).forEach(p -> addEntityLight(p, null, (IFlashLightProvider) p));

		}
		if(clientWorld.getTime() % 10 == 0) {
			List<Entity> toRemove = new ArrayList<>();
			flashlights.forEach((k, v) -> {
				if(!k.isAlive())
					toRemove.add(k);
			});
			toRemove.forEach(e -> DynamicLightsManager.INSTANCE.removeLightSource(flashlights.remove(e)));
		}
	}

	private static void addEntityLight(Entity owner, @Nullable ItemStack stack, IFlashLightProvider provider) {
		if(provider.isLightOn(owner, stack)) {
			if(!flashlights.containsKey(owner)) {
				var l = new FlashlightLightBehavior(owner, provider.lightOffset());
				DynamicLightsManager.INSTANCE.addLightSource(l);
				flashlights.put(owner, l);
			}
		} else if(flashlights.containsKey(owner))
			DynamicLightsManager.INSTANCE.removeLightSource(flashlights.remove(owner));
	}

}
