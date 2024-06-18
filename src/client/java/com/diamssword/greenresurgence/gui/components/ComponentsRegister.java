package com.diamssword.greenresurgence.gui.components;

import io.wispforest.owo.ui.component.EntityComponent;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class ComponentsRegister {
    public static void init()
    {
        MinecraftClient mc=MinecraftClient.getInstance();
        UIParsing.registerFactory("player", element -> new EntityComponent<PlayerEntity>(Sizing.content(), new OtherClientPlayerEntity(mc.world,mc.player.getGameProfile()) {
            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return false;
            }
        }){});
        UIParsing.registerFactory("inventory", InventoryComponent::parse);
        UIParsing.registerFactory("buttoninventory", ButtonInventoryComponent::parse);
        UIParsing.registerFactory("character", CharacterComponent::parse);
    }
}
