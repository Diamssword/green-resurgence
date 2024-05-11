package com.diamssword.greenresurgence.gui;

import io.wispforest.owo.ui.component.EntityComponent;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class ComponentsRegister {
    public static void init()
    {
        UIParsing.registerFactory("player", element -> {
            return new EntityComponent<PlayerEntity>(Sizing.content(), MinecraftClient.getInstance().player){};
        });
        UIParsing.registerFactory("inventory", InventoryComponent::parse);
    }
}
