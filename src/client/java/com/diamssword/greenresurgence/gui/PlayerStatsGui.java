package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.gui.components.*;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.StatsPackets;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.character.stats.PlayerStats;
import com.diamssword.greenresurgence.systems.character.stats.StatsDef;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class PlayerStatsGui extends BaseUIModelScreen<FlowLayout> {

    public PlayerStatsGui() {
        super(FlowLayout.class, DataSource.asset(GreenResurgence.asRessource("player_stats")));
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        var datas=MinecraftClient.getInstance().player.getComponent(Components.PLAYER_DATA);
        for (var st:StatsDef.getRoles()) {
            createCategorie(st,datas.stats,rootComponent);
        }
        rootComponent.inflate(rootComponent.fullSize());
    }
    private void createCategorie(String mainstat, PlayerStats pstats, FlowLayout root)
    {
      /*  pstats.getLevel(mainstat).ifPresent((m->{
            var frame=root.childById(FlowLayout.class,"mainFrame");
            var cont=Containers.verticalFlow(Sizing.fill((100-StatsDef.STAT.values().length)/StatsDef.STAT.values().length),Sizing.content());
            cont.surface(Panels.PANEL_WHITE);
            cont.padding(Insets.of(2));
            cont.margins(Insets.horizontal(2));
            frame.child(cont);
            var txt=io.wispforest.owo.ui.component.Components.label(Text.literal(mainstat.toString().toUpperCase()));
            txt.margins(Insets.of(2));
            txt.sizing(Sizing.fill(100),Sizing.content());
            txt.horizontalTextAlignment(HorizontalAlignment.CENTER);
            cont.child(txt);
            var sep=new SeparatorComponent(Sizing.fill(100),Sizing.fixed(5));
            sep.color(0xFCFCFCFF);
            cont.child(sep);
            StatsDef.SUBS.get(mainstat).forEach(title->{
                pstats.get(mainstat,title).ifPresent(playerStat -> createSubCategorie(mainstat, title, cont, playerStat));
            });

        }));*/
    }/*
    private void createSubCategorie(StatsDef.STAT stat, String catName, FlowLayout root, PlayerStatCat.PlayerStat statV)
    {
        var cont=Containers.horizontalFlow(Sizing.content(),Sizing.content());
        cont.margins(Insets.vertical(3));
        cont.horizontalSizing(Sizing.fill(100));
        cont.verticalAlignment(VerticalAlignment.CENTER);
        root.child(cont);
        var txt=io.wispforest.owo.ui.component.Components.label(Text.literal(" - "+catName+" L"+ statV.getLevel()));
        txt.horizontalSizing(Sizing.fill(80));
        cont.child(txt);
        txt.tooltip(List.of(Text.literal("Desc").formatted(Formatting.GRAY),
                Text.literal("Niveau "+ statV.getLevel()).formatted(Formatting.BOLD,Formatting.BLUE),
                Text.literal("XP: |||||").formatted(Formatting.BOLD,Formatting.GREEN).append(Text.literal("|||||").formatted(Formatting.GRAY)).append(" "+ statV.getProgress()).formatted(Formatting.GREEN),
                Text.literal("Bonus: +"+ statV.getBonus()).formatted(Formatting.BOLD,Formatting.GREEN)

        ));
        var btr=ButtonComponent.Renderer.texture(GreenResurgence.asRessource("textures/gui/dice.png"),0,0,20,40);
        var bt=io.wispforest.owo.ui.component.Components.button(Text.empty(),(r)->{
            Channels.MAIN.clientHandle().send(new StatsPackets.RollStat(stat,catName));
            close();
        });
        bt.sizing(Sizing.fixed(20));
        bt.renderer(btr);
        bt.tooltip(List.of(Text.literal("Lancer un dé").formatted(Formatting.ITALIC,Formatting.GRAY)));
        cont.child(bt);
    }*/
    public boolean shouldPause() {
        return false;
    }
}