package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.Weapons;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public class ItemModelOverrideProvider {
    private static ClampedModelPredicateProvider shield_predicate = (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F;
    private static ClampedModelPredicateProvider activated_predicate = (stack, world, entity, seed) -> stack != null && stack.getOrCreateNbt().getBoolean("activated") ? 1.0F : 0.0F;

    public static void init()
    {
        ModelPredicateProviderRegistry.register(Weapons.TRASH_SHIELD, new Identifier("blocking"), shield_predicate);
        ModelPredicateProviderRegistry.register(Weapons.TRASH_SHIELD_GREEN, new Identifier("blocking"), shield_predicate);
        ModelPredicateProviderRegistry.register(Weapons.SHIELD_WOOD_PLANK, new Identifier("blocking"), shield_predicate);
       // ModelPredicateProviderRegistry.register(Weapons.FLAME_SWORD_ONE_HANDED, new Identifier("activated"), activated_predicate);
    }
}
