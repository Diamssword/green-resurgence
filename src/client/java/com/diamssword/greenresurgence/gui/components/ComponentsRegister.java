package com.diamssword.greenresurgence.gui.components;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.EntityComponent;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class ComponentsRegister {
    public static void init()
    {
        MinecraftClient mc=MinecraftClient.getInstance();

        UIParsing.registerFactory("player",PlayerComponent::parse);
        UIParsing.registerFactory("inventory", InventoryComponent::parse);
        UIParsing.registerFactory("buttoninventory", ButtonInventoryComponent::parse);
        UIParsing.registerFactory("character", CharacterComponent::parse);
        UIParsing.registerFactory("separator", SeparatorComponent::parse);
        UIParsing.registerFactory("clothlist", ClothInventoryComponent::parse);
        UIParsing.registerFactory("rbutton",(a)->new RButtonComponent(Text.empty(),(RButtonComponent button)->{}));
        UIParsing.registerFactory("arrowbutton",(a)->new ArrowButtonComponent((ArrowButtonComponent button)->{}));

    }
}
