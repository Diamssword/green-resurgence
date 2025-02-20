package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.MBlocks;
import com.diamssword.greenresurgence.blockEntities.ItemBlockEntity;
import com.diamssword.greenresurgence.blocks.ItemBlock;
import com.diamssword.greenresurgence.blocks.ShelfBlock;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.ClientGuiPacket;
import com.diamssword.greenresurgence.network.GuiPackets;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.DiscreteSliderComponent;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.SmallCheckboxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class ItemBlockGuiSimple extends MultiInvHandledScreen<ShelfBlock.ScreenHandler,FlowLayout> {
    private ItemBlockEntity tile;
    public ItemBlockGuiSimple(ShelfBlock.ScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, FlowLayout.class, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource("itemblockguisimple")));
        if(handler.isReady()) {
            tile = ClientGuiPacket.getTile(ItemBlockEntity.class, handler.getPos());
            onReady();
        }
        else
            handler.onReady(v->{
                tile=ClientGuiPacket.getTile(ItemBlockEntity.class,v.getPos());
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

    @Override
    protected void drawBackground(DrawContext var1, float var2, int var3, int var4) {

    }

    private void onReady()
    {

    }
    private void bindSlider(String name, double value,double step)
    {
    }
}
