package com.diamssword.greenresurgence.commands;

import com.diamssword.greenresurgence.network.GuiPackets;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class OpenScreenCommand {

	public static void register(LiteralArgumentBuilder<ServerCommandSource> builder) {
		builder.requires(ctx -> ctx.hasPermissionLevel(2))
				.then(CommandManager.literal("customizer").executes(OpenScreenCommand::openCustomizer))
				.then(CommandManager.literal("wardrobe").executes(OpenScreenCommand::openWardrobe))
				.then(CommandManager.literal("stats").executes((ctx) -> openGuiPacket(ctx, GuiPackets.GUI.Stats)));


	}

	private static int openGuiPacket(CommandContext<ServerCommandSource> ctx, GuiPackets.GUI gui) {
		if (ctx.getSource().isExecutedByPlayer()) {
			GuiPackets.send(ctx.getSource().getPlayer(), gui);
			return 1;
		}
		return 0;
	}

	private static int openCustomizer(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		openGuiPacket(ctx, GuiPackets.GUI.Customizer);
		return 1;

	}

	private static int openWardrobe(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		openGuiPacket(ctx, GuiPackets.GUI.Wardrobe);
		return 1;

	}
}
