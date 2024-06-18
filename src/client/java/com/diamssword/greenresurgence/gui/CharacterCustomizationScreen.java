package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.gui.components.CharacterComponent;
import com.diamssword.greenresurgence.systems.Components;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.EntityComponent;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.SliderComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class CharacterCustomizationScreen extends BaseUIModelScreen<FlowLayout> {

    public static enum Type{
        size,
        skin,
    }
    public CharacterCustomizationScreen(Type type) {
        super(FlowLayout.class, DataSource.asset(GreenResurgence.asRessource("character/"+type)));
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        //PlayerEntity pl= MinecraftClient.getInstance().player;
        var chara=rootComponent.childById(CharacterComponent.class,"player");
        chara.createLayer("skin",0,false,chara.entity.getSkinTexture());
        var Sw=rootComponent.childById(SliderComponent.class,"width");
        var Sh=rootComponent.childById(SliderComponent.class,"height");
        Sw.value(0.33);
        Sh.value(0.60);
        Sw.onChanged().subscribe((v)->{
            chara.charWidth= (float) v/60f;
        });
        Sh.onChanged().subscribe((v)->{
            chara.charHeight= (float) v/180f;
        });
    }
    private void buildSize(FlowLayout root)
    {

    }
    public boolean shouldPause() {
        return false;
    }
}