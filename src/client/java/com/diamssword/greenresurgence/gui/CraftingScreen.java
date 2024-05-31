package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.container.FlowLayout;

public class CraftingScreen extends BaseUIModelScreen<FlowLayout> {

    public CraftingScreen() {
        super(FlowLayout.class, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource("craftinggui")));
    }

    @Override
    protected void build(FlowLayout rootComponent) {

    }
}