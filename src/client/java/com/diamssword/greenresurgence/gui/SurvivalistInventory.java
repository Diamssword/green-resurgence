package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class SurvivalistInventory extends MultiInvHandledScreen<FlowLayout, MultiInvScreenHandler> {

    public SurvivalistInventory(MultiInvScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler,inv, title,FlowLayout.class, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource("inventory")));
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.childById(ButtonComponent.class, "the-button").onPress(button -> {
            System.out.println("click");
        });
    }

}
