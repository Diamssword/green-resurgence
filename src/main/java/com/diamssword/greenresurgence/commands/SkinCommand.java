package com.diamssword.greenresurgence.commands;

import com.diamssword.greenresurgence.http.APIService;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.CosmeticsPackets;
import com.diamssword.greenresurgence.systems.Components;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class SkinCommand {

    public static void register(LiteralArgumentBuilder<ServerCommandSource> builder)
    {
        var root=builder.requires(ctx-> ctx.hasPermissionLevel(2));
        var link=CommandManager.literal("link").then(CommandManager.argument("code",StringArgumentType.string()).executes(SkinCommand::linkExec).then(CommandManager.argument("player",EntityArgumentType.player()).executes(SkinCommand::linkExec)));
        var player=CommandManager.literal("change").then(CommandManager.argument("code",StringArgumentType.string()).executes(SkinCommand::createExec).then(CommandManager.argument("player",EntityArgumentType.player()).executes(SkinCommand::createExec)));
        root.then(link);
        root.then(player);
        root.then(CommandManager.literal("refresh").executes(ctx->{
            if(ctx.getSource().isExecutedByPlayer()) {
                ctx.getSource().getPlayer().getComponent(Components.PLAYER_DATA).appearance.refreshSkinData();
                Channels.MAIN.serverHandle(ctx.getSource().getServer()).send(new CosmeticsPackets.RefreshSkin(ctx.getSource().getPlayer().getGameProfile().getId()));

            }
            return 1;
        }));
    }
    private static int linkExec(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var entity=ctx.getSource().getPlayer();
        try {
           entity = EntityArgumentType.getPlayer(ctx, "player");
        }catch (IllegalArgumentException ignored){ }
        String sub= StringArgumentType.getString(ctx,"code");
        if(entity !=null && sub !=null)
        {
            APIService.linkAccount(entity,sub).handle((b,t)->{
                if(t!=null)
                    t.printStackTrace();
                ctx.getSource().sendFeedback(()->Text.literal(b?"Compte lié!":"Erreur, veuillez réessayer"),true);
                return true;
            });
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }
    private static int createExec(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var entity=ctx.getSource().getPlayer();
        try {
            entity = EntityArgumentType.getPlayer(ctx, "player");
        }catch (IllegalArgumentException ignored){ }
        String sub= StringArgumentType.getString(ctx,"code");
        if(entity !=null) {
            ServerPlayerEntity finalEntity = entity;
            APIService.validateSkin(entity, sub).handle((b, t) -> {
                if (t != null)
                    t.printStackTrace();
                ctx.getSource().sendFeedback(() -> Text.literal(b ? "Skin sauvgardé!" : "Erreur, veuillez réessayer"), true);
                if (b) {
                    ctx.getSource().getPlayer().getComponent(Components.PLAYER_DATA).appearance.refreshSkinData();
                    Channels.MAIN.serverHandle(ctx.getSource().getServer()).send(new CosmeticsPackets.RefreshSkin(finalEntity.getGameProfile().getId()));
                }
                return true;
            });

            return 1;
        }
        return 0;

    }

}
