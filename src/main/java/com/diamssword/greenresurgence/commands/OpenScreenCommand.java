package com.diamssword.greenresurgence.commands;

import com.diamssword.greenresurgence.network.GuiPackets;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.perimeter.IFactionList;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class OpenScreenCommand {

    public static void register(LiteralArgumentBuilder<ServerCommandSource> builder)
    {
        builder.requires(ctx-> ctx.hasPermissionLevel(2))
                .then(CommandManager.literal("customizer").executes(OpenScreenCommand::openCustomizer));


    }
    private static int openCustomizer(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        if(ctx.getSource().isExecutedByPlayer())
            GuiPackets.send(ctx.getSource().getPlayer(), GuiPackets.GUI.Customizer);
        return 1;

    }
}
