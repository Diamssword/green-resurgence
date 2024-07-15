package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.gui.components.ArrowButtonComponent;
import com.diamssword.greenresurgence.gui.components.ClothInventoryComponent;
import com.diamssword.greenresurgence.gui.components.PlayerComponent;
import com.diamssword.greenresurgence.gui.components.RButtonComponent;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.CosmeticsPackets;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.clothing.ClothingLoader;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

import java.util.*;

public class WardrobeGui extends BaseUIModelScreen<FlowLayout> {

    private static final Pair<String, ClothingLoader.Layer[]>[] layerBts=new Pair[]{
            new Pair<>("all", ClothingLoader.Layer.clothLayers()),
            new Pair<>("head", new ClothingLoader.Layer[]{ClothingLoader.Layer.hat,ClothingLoader.Layer.accessories,ClothingLoader.Layer.glasses}),
            new Pair<>("body", new ClothingLoader.Layer[]{ClothingLoader.Layer.teeshirt,ClothingLoader.Layer.jacket,ClothingLoader.Layer.full}),
            new Pair<>("pants", new ClothingLoader.Layer[]{ClothingLoader.Layer.pants,ClothingLoader.Layer.underwear}),
            new Pair<>("shoes", new ClothingLoader.Layer[]{ClothingLoader.Layer.shoes}),
            new Pair<>("current", ClothingLoader.Layer.clothLayers()),
    };
    private Pair<String, ClothingLoader.Layer[]> currentLayer= layerBts[0];
    private Map<ClothingLoader.Layer, ClothingLoader.Cloth> oldCloths;
    private RButtonComponent[] collections;

    private int scroll=0;
    private String currentCol="all";

    public WardrobeGui() {
        super(FlowLayout.class, DataSource.asset(GreenResurgence.asRessource("wardrobe")));
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        var ward=rootComponent.childById(ClothInventoryComponent.class,"cloths");
        var flow=rootComponent.childById(FlowLayout.class,"layerLayout");
        var flowColl=rootComponent.childById(FlowLayout.class,"collectionLayout");
        var ndLayout=rootComponent.childById(FlowLayout.class,"ndLayout");
        var txt1=rootComponent.childById(LabelComponent.class,"title_right");
        var txt2=rootComponent.childById(LabelComponent.class,"count_text");
        var playerComp=rootComponent.childById(PlayerComponent.class,"player");
        var slider=rootComponent.childById(SlimSliderComponent.class,"slider");
        var player=playerComp.entity();
        var cp=new NbtCompound();
        MinecraftClient.getInstance().player.getComponent(Components.PLAYER_DATA).writeToNbt(cp);
        var dt=player.getComponent(Components.PLAYER_DATA);
        dt.readFromNbt(cp);

        var outfits=dt.appearance.getOutfits();
        slider.value(0.5);
        slider.onChanged().subscribe(v->{
            playerComp.rotation((int)(-180+ (v*360f)));
        });
        for(int i=1;i<=7;i++)
        {
            var v=Text.literal("Outfit "+i);
            final var i1=i-1;
            if(i1<outfits.size())
                v=Text.literal(outfits.get(i1));
            var ar= new ArrayList<Text>();
            ar.add(v);
            ar.add(Text.literal("[maj] + [clique] pour modifier cette tenue").formatted(Formatting.GRAY,Formatting.ITALIC));
            rootComponent.childById(RButtonComponent.class,"memory"+i).onPress(v1->{
                if(Screen.hasShiftDown())
                {
                    createOutfitWindow(v1,i1);
                }
                else {
                    Channels.MAIN.clientHandle().send(new CosmeticsPackets.EquipOutfit(i1));
                    dt.appearance.equipOutfit(i1);
                }
            }).tooltip(ar);
        }
        ndLayout.mouseScroll().subscribe((x,y,m)->{
            scrollColl((int) m,txt1,txt2,ward);
            return true;
        });
        clothHandling(ward,playerComp);
        if( flow!=null)
        {
            final List<RButtonComponent> bts=new ArrayList<>();
            for (Pair<String, ClothingLoader.Layer[]> value : layerBts) {
                    var bt = new RButtonComponent(Text.empty(), (o) -> {
                        currentLayer=value;
                        switchColl(txt1,txt2,ward);
                        bts.forEach(o1->{
                            o1.setActivated(false);
                        });
                        o.setActivated(true);
                    });
                    if(value.getLeft().equals("all"))
                        bt.setActivated(true);
                    bt.icon(value.getLeft()).sizing(Sizing.fixed(20)).tooltip(Text.translatable(GreenResurgence.ID+".wardrobe.layerbt."+ value.getLeft())).margins(Insets.of(2,0,2,0));
                    flow.child(bt);
                    bts.add(bt);

            }

        }
        if(flowColl!=null)
        {
            var ar1=new ArrowButtonComponent((a)-> scrollColl(1,txt1,txt2,ward));
            var ar2=new ArrowButtonComponent((a)->scrollColl(-1,txt1,txt2,ward)).setDown(true);
            var s= ClothingLoader.instance.getCollections();
            flowColl.child(new RButtonComponent(Text.empty(), (o) ->{
                currentCol="all";
                switchColl(txt1,txt2,ward);
            }).icon("all")
                    .sizing(Sizing.fixed(20)).tooltip(Text.translatable(GreenResurgence.ID+".wardrobe.collection.all"))
                    .margins(Insets.of(2,0,2,0))
            );
            if(s.size()<7)
            {
                s.forEach(c->{
                    var bt = new RButtonComponent(Text.empty(), (o) -> {
                        currentCol=c;
                        switchColl(txt1,txt2,ward);
                    });
                    bt.icon(c).sizing(Sizing.fixed(20)).tooltip(Text.translatable(GreenResurgence.ID+".wardrobe.collection."+ c)).margins(Insets.of(2,0,2,0));
                    flowColl.child(bt);
                });
            }
            else {
                flowColl.child(ar1.margins(Insets.of(2, 0, 2, 0)));
                collections=new RButtonComponent[6];
                for (int i = 0; i < collections.length; i++) {
                    final var i1=i;
                    var bt = new RButtonComponent(Text.empty(), (o) -> {
                        currentCol=s.get(i1);
                        switchColl(txt1,txt2,ward);});
                    bt.icon(s.get(i)).sizing(Sizing.fixed(20)).tooltip(Text.translatable(GreenResurgence.ID+".wardrobe.collection."+ s.get(i))).margins(Insets.of(2,0,2,0));
                    collections[i]=bt;
                    flowColl.child(bt);
                }
                flowColl.child(ar2.margins(Insets.of(2, 0, 2, 0)));
            }
        }
        switchColl(txt1,txt2,ward);
    }
    private void createOutfitWindow(RButtonComponent bt,int index)
    {

        MinecraftClient.getInstance().setScreen(new OutfitPopupGui(this,index,bt));


    }
    private void switchColl(LabelComponent text1,LabelComponent text2, ClothInventoryComponent inventory)
    {
        var equip=MinecraftClient.getInstance().player.getComponent(Components.PLAYER_DATA).appearance.getCloths().values().stream().filter(Objects::nonNull).toList();
        var list=ClothingLoader.instance.getAvailablesClothsCollectionForPlayer(MinecraftClient.getInstance().player,currentCol,currentLayer.getRight());
        inventory.setEquipped(equip);
        if(text1!=null)
            text1.text(Text.translatable(GreenResurgence.ID+".wardrobe.collection."+currentCol));
        if(currentLayer.getLeft().equals("current")) {
            inventory.setSelection(equip);
            text2.text(Text.literal(""));
        }
        else {
            inventory.setSelection(list);
            text2.text(Text.literal(list.size()+"/"+ClothingLoader.instance.getClothsCollection(currentCol,currentLayer.getRight()).size()));
        }

    }
    private void scrollColl(int dir,LabelComponent text1,LabelComponent text2, ClothInventoryComponent inventory)
    {
        var col=ClothingLoader.instance.getCollections();
        if(col.size()>=7) {
            if (dir > 0) {
                scroll = Math.max(scroll - 1, 0);
            } else if (dir < 0) {
                scroll = Math.min(scroll + 1, col.size() - 6);
            }
            for (int i = scroll; i < collections.length+scroll; i++) {
                final int i1=i-scroll;
                collections[i-scroll].onPress((o)->{
                    currentCol=col.get(i1-scroll);
                    switchColl(text1,text2,inventory);}).icon(col.get(i)).tooltip(Text.translatable(GreenResurgence.ID + ".wardrobe.collection." + col.get(i)));
            }
        }
    }
    private void clothHandling(ClothInventoryComponent comp, PlayerComponent playerComp)
    {
        var player=playerComp.entity();
        var dt=player.getComponent(Components.PLAYER_DATA);
        oldCloths=dt.appearance.getCloths();
        comp.onClothPicked().subscribe(v->{
            System.out.println("pick");
            if(oldCloths.containsValue(v)) {
                dt.appearance.setCloth(v.layer(), null);
                Channels.MAIN.clientHandle().send(new CosmeticsPackets.EquipCloth("null",v.layer().toString()));
            }
            else {
                dt.appearance.setCloth(v.layer(), v);
                Channels.MAIN.clientHandle().send(new CosmeticsPackets.EquipCloth(v.id(),v.layer().toString()));
            }
            oldCloths=dt.appearance.getCloths();
            comp.setEquipped(oldCloths.values().stream().toList());
        });
        comp.onClothHovered().subscribe(v->{
            if(v !=null)
                dt.appearance.setCloth(v.layer(),v);
            else
            {
                oldCloths.forEach((a,v1)->{
                    dt.appearance.setCloth(a,v1);
                });
            }

        });

    }
    public boolean shouldPause() {
        return false;
    }
}