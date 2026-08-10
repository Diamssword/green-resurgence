package com.diamssword.greenresurgence.network;

import com.diamssword.characters.api.ComponentManager;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Random;

public class StatsPackets {
	public record RollStat(String role) {
	}

	public static Random random = new Random();

	public static void init() {
		Channels.MAIN.registerServerbound(RollStat.class, (msg, ctx) -> {
			var stats = ComponentManager.getPlayerDatas(ctx.player()).getStats();
			var stat = stats.getLevel(msg.role);
			var pname = ctx.player().getDisplayName().copy().formatted(Formatting.BOLD, Formatting.LIGHT_PURPLE);
			var res = 1 + random.nextInt(100);
			var tooltip = Text.literal(res + "").formatted(Formatting.BLUE).append(Text.translatable("message.green_resurgence.dice_roll.details.dice").formatted(Formatting.GRAY));
			if(stat > 0) {
				tooltip.append(Text.literal("\n+" + stat).formatted(Formatting.GREEN)).append(Text.translatable("message.green_resurgence.dice_roll.details.stats")).formatted(Formatting.GRAY);
				tooltip.append(Text.literal("\n=" + (res + stat)).formatted(Formatting.GOLD));
			}
			var dicet = Text.literal(" :").append("\n \uD83C\uDFB2 " + (res + stat) + "/100").setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true)
					.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltip)));
			ctx.player().sendMessage(Text.translatable("message.green_resurgence.dice_roll.roll", pname).setStyle(Style.EMPTY.withBold(false).withColor(Formatting.GREEN)).append(Text.literal(msg.role.toUpperCase() + " " + msg.role).formatted(Formatting.LIGHT_PURPLE))
					.append(dicet));
		});

	}
}
