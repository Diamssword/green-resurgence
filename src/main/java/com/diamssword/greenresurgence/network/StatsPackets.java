package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.character.stats.StatsDef;
import com.diamssword.greenresurgence.systems.clothing.ClothingLoader;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.UUID;

public class StatsPackets {
    public record RollStat(String role){};
    public static Random random=new Random();
    public static void init()
    {
        Channels.MAIN.registerServerbound(RollStat.class,(msg, ctx)->{
            var stats=ctx.player().getComponent(Components.PLAYER_DATA).stats;
            var stat=stats.getLevel(msg.role);
                var pname = ctx.player().getDisplayName().copy().formatted(Formatting.BOLD, Formatting.LIGHT_PURPLE);
                var res = 1 + random.nextInt(100);
                var tooltip=Text.literal(res+"").formatted(Formatting.BLUE).append(Text.literal("(Dé)").formatted(Formatting.GRAY));
                if(stat >0) {
                    tooltip.append(Text.literal("\n+" + stat).formatted(Formatting.GREEN)).append("(Stat)").formatted(Formatting.GRAY);
                    tooltip.append(Text.literal("\n="+(res+ stat)).formatted(Formatting.GOLD));
                }
                var dicet=Text.literal(" :").append("\n \uD83C\uDFB2 " + (res+ stat)+"/100").setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,tooltip)));
                ctx.player().sendMessage(pname.append(Text.literal(" Lance un dé 100 de: ").setStyle(Style.EMPTY.withBold(false).withColor(Formatting.GREEN)).append(Text.literal(msg.role.toUpperCase() + " " + msg.role).formatted(Formatting.LIGHT_PURPLE))
                        .append(dicet)));
        });

    }
}
