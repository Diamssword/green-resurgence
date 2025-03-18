package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blockEntities.GenericStorageBlockEntity;
import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import com.diamssword.greenresurgence.blocks.BaseStorageBlock;
import com.diamssword.greenresurgence.gui.components.InventoryComponent;
import com.diamssword.greenresurgence.network.ClientGuiPacket;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class FactionChestGui extends PlayerBasedGui<BaseStorageBlock.ScreenHandler> {
    private GenericStorageBlockEntity tile;
    public FactionChestGui(BaseStorageBlock.ScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, "faction_chest");
        handler.onReady(v->{
            tile=ClientGuiPacket.getTile(GenericStorageBlockEntity.class,v.getPos());
        });
    }


    @Override
    protected void build(FlowLayout rootComponent) {
        super.build(rootComponent);
    var inv=rootComponent.childById(InventoryComponent.class,"storage");
    //inv.setSize(this.handler.getInventory("storage").getWidth(),this.handler.getInventory("storage").getHeight());
    }

    @Override
    protected void drawBackground(DrawContext var1, float var2, int var3, int var4) {

    }
}
