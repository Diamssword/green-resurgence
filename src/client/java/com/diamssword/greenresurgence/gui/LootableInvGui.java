package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import com.diamssword.greenresurgence.network.ClientGuiPacket;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class LootableInvGui extends MultiInvHandledScreen<LootedBlockEntity.Container,FlowLayout> {
    private LootedBlockEntity tile;
    public LootableInvGui(LootedBlockEntity.Container handler, PlayerInventory inv, Text title) {
        super(handler, FlowLayout.class, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource("lootablegui")));
        if(handler.isReady()) {
            tile = ClientGuiPacket.getTile(LootedBlockEntity.class, handler.getPos());
            onReady();
        }
        else
            handler.onReady(v->{
                tile=ClientGuiPacket.getTile(LootedBlockEntity.class,v.getPos());
                onReady();
            });
    }


    @Override
    protected void build(FlowLayout rootComponent) {

    }

    @Override
    protected void drawBackground(DrawContext var1, float var2, int var3, int var4) {

    }

    private void onReady()
    {

    }
}
