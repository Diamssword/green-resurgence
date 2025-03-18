package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.blockEntities.GenericStorageBlockEntity;
import com.diamssword.greenresurgence.blocks.BaseStorageBlock;
import com.diamssword.greenresurgence.containers.GenericContainer;
import com.diamssword.greenresurgence.gui.components.InventoryComponent;
import com.diamssword.greenresurgence.network.ClientGuiPacket;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class GenericContainerGui extends PlayerBasedGui<GenericContainer> {
    private Text title;
    public GenericContainerGui(GenericContainer handler, PlayerInventory inv, Text title) {
        super(handler, "generic_container");
        this.title=title;

    }
    @Override
    protected void build(FlowLayout rootComponent) {
        super.build(rootComponent);
        var cont=rootComponent.childById(InventoryComponent.class,"container");
        if(cont !=null)
        {
            cont.customName=title;
        }
    }

    @Override
    protected void drawBackground(DrawContext var1, float var2, int var3, int var4) {

    }
}
