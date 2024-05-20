package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blockEntities.ImageBlockEntity;
import com.diamssword.greenresurgence.blockEntities.ItemBlockEntity;
import com.diamssword.greenresurgence.blocks.ItemBlock;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.ClientGuiPacket;
import com.diamssword.greenresurgence.network.GuiPackets;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.DiscreteSliderComponent;
import io.wispforest.owo.ui.component.SmallCheckboxComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class ImageBlockGui extends BaseUIModelScreen<FlowLayout> {
    private ImageBlockEntity tile;
    public ImageBlockGui(ImageBlockEntity tile) {
        super(FlowLayout.class,DataSource.asset(GreenResurgence.asRessource("imageblockgui")));
        this.tile=tile;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.childById(DiscreteSliderComponent.class,"sizeX").scrollStep(0.05).value((-1+tile.getSize().x)/20f).onChanged().subscribe(v->{
            Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(tile.getPos(),"sizeX",v));
        });
        rootComponent.childById(DiscreteSliderComponent.class,"sizeY").scrollStep(0.05).value((-1+tile.getSize().y)/20f).active(tile.isStretch()).onChanged().subscribe(v->{
            Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(tile.getPos(),"sizeY",v));
        });
        rootComponent.childById(DiscreteSliderComponent.class,"rotation").scrollStep(0.003).value((180+tile.getRotation())/360f).onChanged().subscribe(v->{
            Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(tile.getPos(),"rotation",v));
        });
        rootComponent.childById(SmallCheckboxComponent.class,"stretch").checked(tile.isStretch()).onChanged().subscribe(v->{
                rootComponent.childById(DiscreteSliderComponent.class,"sizeY").active(v);
            Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(tile.getPos(),"stretch",v?1:0));
        });
        rootComponent.childById(SmallCheckboxComponent.class,"offsetX").checked(tile.isOffsetX()).onChanged().subscribe(v->{
            Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(tile.getPos(),"offsetX",v?1:0));
        });
        rootComponent.childById(SmallCheckboxComponent.class,"offsetY").checked(tile.isOffsetY()).onChanged().subscribe(v->{
            Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(tile.getPos(),"offsetY",v?1:0));
        });
        var field=rootComponent.childById(TextBoxComponent.class,"url");
        field.setMaxLength(30000);
        field.text(tile.getContent());
        rootComponent.childById(ButtonComponent.class,"send").onPress(v->{
            Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(tile.getPos(),"url",field.getText()));
        });

    }
}
