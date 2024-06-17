package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.Components;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.EntityComponent;
import io.wispforest.owo.ui.component.SliderComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class CharacterCustomizationScreen extends BaseUIModelScreen<FlowLayout> {

    public CharacterCustomizationScreen() {
        super(FlowLayout.class, DataSource.asset(GreenResurgence.asRessource("character/size")));
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        //PlayerEntity pl= MinecraftClient.getInstance().player;
        Entity e=rootComponent.childById(EntityComponent.class,"player").entity();
        var comp = e.getComponent(Components.PLAYER_DATA);

        rootComponent.childById(SliderComponent.class,"width").onChanged().subscribe((v)->{
            comp.appearance.width= (float) v/10f;
        });
        rootComponent.childById(SliderComponent.class,"height").onChanged().subscribe((v)->{
            comp.appearance.height= (float) v/10f;
        });
    }
    public boolean shouldPause() {
        return false;
    }
}