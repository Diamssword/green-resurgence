package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.Shields;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public class ItemModelOverrideProvider {
	private static final ClampedModelPredicateProvider shield_predicate = (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F;
	private static final ClampedModelPredicateProvider activated_predicate = (stack, world, entity, seed) -> stack != null && stack.getOrCreateNbt().getBoolean("activated") ? 1.0F : 0.0F;

	public static void init() {
		Shields.specialRenderRegister.forEach(v -> ModelPredicateProviderRegistry.register(v, new Identifier("blocking"), shield_predicate));
		Shields.specialRenderRegister.clear();
	}
}
