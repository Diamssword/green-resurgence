package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blockEntities.ItemBlockEntity;
import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import com.diamssword.greenresurgence.blocks.ItemBlock;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.ClientGuiPacket;
import com.diamssword.greenresurgence.network.GuiPackets;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.DiscreteSliderComponent;
import io.wispforest.owo.ui.component.SmallCheckboxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class LootableInvGui extends MultiInvHandledScreen<FlowLayout,LootedBlockEntity.Container> {
    private LootedBlockEntity tile;
    public LootableInvGui(LootedBlockEntity.Container handler, PlayerInventory inv, Text title) {
        super(handler,inv, title,FlowLayout.class, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource("lootablegui")));
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
    public boolean shouldPause() {
        return false;
    }
    @Override
    protected void build(FlowLayout rootComponent) {

    }
    private void onReady()
    {

    }
}
