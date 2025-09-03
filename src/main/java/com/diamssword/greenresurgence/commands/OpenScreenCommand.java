package com.diamssword.greenresurgence.commands;

import com.diamssword.greenresurgence.network.GuiPackets;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class OpenScreenCommand {

	public static void register(LiteralArgumentBuilder<ServerCommandSource> builder) {
		builder.requires(ctx -> ctx.hasPermissionLevel(2))

				.then(CommandManager.literal("stats").executes((ctx) -> openGuiPacket(ctx, GuiPackets.GUI.Stats)));


	}

	private static int openGuiPacket(CommandContext<ServerCommandSource> ctx, GuiPackets.GUI gui) {
		if (ctx.getSource().isExecutedByPlayer()) {
			GuiPackets.send(ctx.getSource().getPlayer(), gui);
			return 1;
		}
		return 0;
	}
}
