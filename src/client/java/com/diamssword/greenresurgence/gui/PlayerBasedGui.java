package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import com.diamssword.greenresurgence.containers.player.VanillaPlayerInvMokup;
import com.diamssword.greenresurgence.gui.components.InventoryComponent;
import com.diamssword.greenresurgence.gui.components.PlayerComponent;
import com.diamssword.greenresurgence.gui.components.SubScreenLayout;
import com.diamssword.greenresurgence.systems.Components;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

public class PlayerBasedGui<T extends MultiInvScreenHandler> extends MultiInvHandledScreen<T,FlowLayout> {
    public final String subscreen;
    public PlayerBasedGui(T handler, String subscreen) {
        super(handler, FlowLayout.class, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource("player_inventory")));
        this.subscreen=subscreen;
    }


    @Override
    protected void build(FlowLayout rootComponent) {
        var sub=rootComponent.childById(SubScreenLayout.class,"subcontainer");
        if(sub!=null) {
            sub.setLayout(subscreen);
            rootComponent.onChildMutated(sub);
        }

        var playerComp=rootComponent.childById(PlayerComponent.class,"playerSkin");
        var player=playerComp.entity();
        var cp=new NbtCompound();
        MinecraftClient.getInstance().player.getComponent(Components.PLAYER_DATA).writeToNbt(cp);
        var dt=player.getComponent(Components.PLAYER_DATA);
        dt.readFromNbt(cp);
        var dt1=player.getComponent(Components.PLAYER_INVENTORY);
        dt1.setBackpackStack(MinecraftClient.getInstance().player.getComponent(Components.PLAYER_INVENTORY).getBackpackStack());
        dt.appearance.refreshSkinData();
    //inv.setSize(this.handler.getInventory("storage").getWidth(),this.handler.getInventory("storage").getHeight());
    }

    @Override
    protected void drawBackground(DrawContext var1, float var2, int var3, int var4) {

    }
}
