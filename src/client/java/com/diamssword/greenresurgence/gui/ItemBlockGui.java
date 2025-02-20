package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.MBlocks;
import com.diamssword.greenresurgence.blockEntities.ItemBlockEntity;
import com.diamssword.greenresurgence.blocks.ItemBlock;
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

public class ItemBlockGui extends MultiInvHandledScreen<ItemBlock.ScreenHandler,FlowLayout> {
    private ItemBlockEntity tile;
    public ItemBlockGui(ItemBlock.ScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, FlowLayout.class, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource("itemblockgui")));
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
        bindSlider("posX",(100+(tile.getPosition().getX()))/200f,0.005);
        bindSlider("posY",(100+(tile.getPosition().getY()))/200f,0.005);
        bindSlider("posZ",(100+(tile.getPosition().getZ()))/200f,0.005);
        bindSlider("rotX",(180+(tile.getRotation().getX()))/360f,0.003);
        bindSlider("rotY",(180+(tile.getRotation().getY()))/360f,0.003);
        bindSlider("rotZ",(180+(tile.getRotation().getZ()))/360f,0.003);
        bindSlider("size",(-1+tile.getSize())/100f,0.01);
        BlockState st=tile.getWorld().getBlockState(tile.getPos());
        if(st.getBlock() == MBlocks.ITEM_BLOCK) {
            this.component(SmallCheckboxComponent.class, "collision").checked(st.get(ItemBlock.COLLISION)).onChanged().subscribe(v -> {
                Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(tile.getPos(), "collision", v ? 1 : 0));
            });
        }
        else {
            this.component(SmallCheckboxComponent.class, "collision").remove();
            this.component(LabelComponent.class, "rem1").remove();
            this.component(LabelComponent.class, "rem2").remove();
            this.component(LabelComponent.class, "rem3").remove();
            this.component(LabelComponent.class, "rem4").remove();
            this.component(LabelComponent.class, "rem5").remove();
        }
        this.component(SmallCheckboxComponent.class,"light").checked(tile.isLightOffset()).onChanged().subscribe(v->{
            Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(tile.getPos(),"light",v?1:0));
        });

    }
    private void bindSlider(String name, double value,double step)
    {
        this.component(DiscreteSliderComponent.class, name).scrollStep(step).value(value).onChanged().subscribe(v -> {
            Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(tile.getPos(),name,v));
        });
    }
}
