package com.diamssword.greenresurgence.commands;

import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.GuildPackets;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.perimeter.FactionList;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionGuild;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;
import java.util.Optional;

public class FactionCommand {

	/*private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> {
		World w = context.getSource().getWorld();
		FactionList ls = w.getComponent(Components.BASE_LIST);
		return CommandSource.suggestMatching(ls.getNames(), builder);

	};
	private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER1 = (context, builder) -> {
		var name = StringArgumentType.getString(context, "name");
		if (name != null) {
			World w = context.getSource().getWorld();
			FactionList ls = w.getComponent(Components.BASE_LIST);
			var op = ls.get(name);
			if (op.isPresent())
				return CommandSource.suggestMatching(new ArrayList<>(op.get().getSubTerrains().keySet()), builder);
		}
		return CommandSource.suggestMatching(new ArrayList<>(), builder);

	};
*/
	public static void register(LiteralArgumentBuilder<ServerCommandSource> builder) {

		builder.requires(ctx -> ctx.hasPermissionLevel(2))
				.then(CommandManager.literal("get").then(CommandManager.argument("at", BlockPosArgumentType.blockPos()).executes(FactionCommand::getExec)))
				.then(CommandManager.literal("list").then(CommandManager.argument("page", IntegerArgumentType.integer(0)).executes(FactionCommand::getList)).executes(FactionCommand::getList))
				.then(CommandManager.literal("removeArea").then(CommandManager.argument("at", BlockPosArgumentType.blockPos()).executes(FactionCommand::removeExec)))
				.then(CommandManager.literal("impersonate").then(CommandManager.argument("uuid", UuidArgumentType.uuid()).executes(FactionCommand::impersonateExec)))
				.then(CommandManager.literal("delete").then(CommandManager.argument("uuid", UuidArgumentType.uuid()).then(CommandManager.argument("confirm", StringArgumentType.string()).executes(FactionCommand::deleteExec))))
				.then(CommandManager.literal("refresh").executes(ctx -> {
					Components.BASE_LIST.sync(ctx.getSource().getWorld());
					return 1;
				}));

	}

	private static int impersonateExec(CommandContext<ServerCommandSource> ctx) {
		if (ctx.getSource().isExecutedByPlayer()) {
			var id = UuidArgumentType.getUuid(ctx, "uuid");
			if (id != null) {
				var op = ctx.getSource().getWorld().getComponent(Components.BASE_LIST).get(id);
				if (op.isPresent()) {
					Channels.MAIN.serverHandle(ctx.getSource().getPlayer()).send(new GuildPackets.OpenFactionGui(op.get().getId(), op.get().getName()));
					return 1;
				} else {
					ctx.getSource().sendFeedback(() -> Text.literal("Aucune faction trouvé avec cet id"), false);
					return -1;
				}


			}
			return -1;
		}
		return -1;
	}

	private static int getList(CommandContext<ServerCommandSource> ctx) {
		int page = 0;

		try {
			page = IntegerArgumentType.getInteger(ctx, "page");
		} catch (IllegalArgumentException ignored) {
		}
		var base = Text.literal("====Liste des guilds [page " + page + "]====\n");
		var ls = ctx.getSource().getWorld().getComponent(Components.BASE_LIST).getAll();
		ls.sort(Comparator.comparing(FactionGuild::getName));
		for (int i = page * 10; i < ls.size(); i++) {
			var b = ls.get(i);
			base = base.append(RecipeHelperCommand.copyable(Text.literal("[" + b.getName() + "]\n"), b.getId().toString()).styled(st -> st.withColor(Formatting.GREEN).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Gestionnaire: " + b.getOwner().getName() + "\nUuid: " + b.getId() + " (Cliquez pour copier)")))));
		}
		int finalPage = page;
		var arrowA = Text.literal("[<]").styled(st -> st.withColor(Formatting.LIGHT_PURPLE).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(("Précédent")))).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction list " + (finalPage - 1))));
		var arrowB = Text.literal("[>]").styled(st -> st.withColor(Formatting.LIGHT_PURPLE).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(("Suivant")))).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction list " + (finalPage + 1))));
		base = base.append("====");
		if (page > 0)
			base = base.append(arrowA).append(Text.literal(" "));
		base = base.append(arrowB).append("====");
		net.minecraft.text.MutableText finalBase = base;
		ctx.getSource().sendFeedback(() -> finalBase, false);
		return 1;
	}

	private static int getExec(CommandContext<ServerCommandSource> ctx) {
		BlockPos p = BlockPosArgumentType.getBlockPos(ctx, "at");
		FactionList base = ctx.getSource().getWorld().getComponent(Components.BASE_LIST);
		Optional<FactionGuild> b = base.getAt(p);
		b.ifPresentOrElse((i) -> {
			var name = RecipeHelperCommand.copyable(Text.literal("[" + i.getName() + "]"), i.getId().toString()).styled(st -> st.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Cliquez pour copier l'id: " + i.getId().toString()))));
			Text t = Text.literal("[Inconnu]");
			var pl = i.getOwner().asPlayer(ctx.getSource().getWorld());
			if (pl.isPresent())
				t = pl.get().getDisplayName();
			else {
				t = RecipeHelperCommand.copyable(Text.literal("[" + i.getOwner().getName() + "]"), i.getOwner().getId().toString()).styled(st -> st.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Cliquez pour copier l'id: " + i.getId().toString() + "\n Type: " + (i.getOwner().isPlayer() ? "Player" : "Guild")))));

			}


			Text finalT = t;
			ctx.getSource().sendFeedback(() -> Text.literal("Guild trouvée en [" + p.getX() + "," + p.getY() + "," + p.getZ() + "]: ").append(name).append(Text.literal(" \nGerée par ").append(finalT)), false);
		}, () -> {
			ctx.getSource().sendFeedback(() -> Text.literal("Aucune guild trouvée en [" + p.getX() + "," + p.getY() + "," + p.getZ() + "]"), false);
		});
		return b.isPresent() ? 1 : -1;

	}

	private static int deleteExec(CommandContext<ServerCommandSource> ctx) {
		var id = UuidArgumentType.getUuid(ctx, "uuid");
		String confirm = StringArgumentType.getString(ctx, "confirm");
		if (confirm.equals("Confirm")) {
			FactionList base = ctx.getSource().getWorld().getComponent(Components.BASE_LIST);
			boolean t = base.delete(id);
			if (!t) {
				ctx.getSource().sendFeedback(() -> Text.literal("Aucune faction trouvé avec cet id"), false);
				return -1;
			} else {
				ctx.getSource().sendFeedback(() -> Text.literal("Faction " + id.toString() + " supprimée!"), false);

				Components.BASE_LIST.sync(ctx.getSource().getWorld());
				return 1;
			}
		} else {
			ctx.getSource().sendFeedback(() -> Text.literal("Cette action supprimera definitivement toute les zones d'une factions, pour confirmer tapez le text 'Confirm' (avec la maj) dans la commande"), false);
			return 0;
		}

	}

	private static int removeExec(CommandContext<ServerCommandSource> ctx) {
		BlockPos p = BlockPosArgumentType.getBlockPos(ctx, "at");
		FactionList bases = ctx.getSource().getWorld().getComponent(Components.BASE_LIST);
		Optional<FactionGuild> base = bases.getAt(p);
		if (base.isPresent()) {
			boolean flg = base.get().removeTerrainAt(p, ctx.getSource().getWorld());
			if (flg) {
				ctx.getSource().sendFeedback(() -> Text.literal("Zone supprimé pour '" + base.get().getName() + "'"), false);
				Components.BASE_LIST.sync(ctx.getSource().getWorld());
			} else
				ctx.getSource().sendFeedback(() -> Text.literal("Faction introuvable en :[" + p.getX() + "," + p.getY() + "," + p.getZ() + "]"), false);
			return flg ? 1 : -1;
		}
		ctx.getSource().sendFeedback(() -> Text.literal("Faction introuvable en :[" + p.getX() + "," + p.getY() + "," + p.getZ() + "]"), false);
		return -1;

	}
}
