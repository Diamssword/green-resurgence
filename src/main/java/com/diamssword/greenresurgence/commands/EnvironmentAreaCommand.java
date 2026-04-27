package com.diamssword.greenresurgence.commands;

import com.diamssword.greenresurgence.network.EnvironmentPacket;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.environment.EffectArea;
import com.diamssword.greenresurgence.systems.environment.EnvironementAreas;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class EnvironmentAreaCommand {

	private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> CommandSource.suggestMatching(EnvironementAreas.FACTORIES.keySet(), builder);

	public static void register(LiteralArgumentBuilder<ServerCommandSource> builder) {

		builder.requires(ctx -> ctx.hasPermissionLevel(2))
				.then(CommandManager.literal("get").then(CommandManager.argument("at", BlockPosArgumentType.blockPos()).executes(EnvironmentAreaCommand::getExec)))
				.then(CommandManager.literal("list").then(CommandManager.argument("page", IntegerArgumentType.integer(0)).executes(EnvironmentAreaCommand::getList)).executes(EnvironmentAreaCommand::getList))
				.then(CommandManager.literal("add").then(CommandManager.argument("from", BlockPosArgumentType.blockPos()).then(CommandManager.argument("to", BlockPosArgumentType.blockPos()).then(CommandManager.argument("type", StringArgumentType.word()).suggests(SUGGESTION_PROVIDER).then(CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound()).executes(EnvironmentAreaCommand::createExec))))))
				.then(CommandManager.literal("delete").then(CommandManager.argument("at", BlockPosArgumentType.blockPos()).executes(EnvironmentAreaCommand::removeExec)))
				.then(CommandManager.literal("edit").then(CommandManager.argument("at", BlockPosArgumentType.blockPos()).then(CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound()).executes(EnvironmentAreaCommand::editExec))))
				.then(CommandManager.literal("refresh").executes(ctx -> {
					EnvironmentPacket.sendListForAll(ctx.getSource().getWorld());
					return 1;
				}));

	}

	private static int editExec(CommandContext<ServerCommandSource> ctx) {
		BlockPos p = BlockPosArgumentType.getBlockPos(ctx, "at");
		NbtCompound nbt = NbtCompoundArgumentType.getNbtCompound(ctx, "nbt");
		EnvironementAreas areas = ctx.getSource().getWorld().getComponent(Components.ENVIRONMENT_AREAS);
		var ls = areas.getAtFirst(p.toCenterPos());
		if(ls.isPresent()) {
			ls.get().fromNBT(nbt);
			EnvironmentPacket.sendListForAll(ctx.getSource().getWorld());
			ctx.getSource().sendFeedback(() -> RecipeHelperCommand.copyable(Text.literal("Zone modifiée à '" + p + "'"), ls.get().toNBT().toString()).styled(st -> st.withColor(Formatting.GREEN).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("NBT datas: " + ls.get().toNBT() + "\n(Cliquez pour copier)")))), false);
			return 1;
		}
		ctx.getSource().sendFeedback(() -> Text.literal("Pas de zone en :[" + p.getX() + "," + p.getY() + "," + p.getZ() + "]"), false);
		return -1;

	}

	private static int createExec(CommandContext<ServerCommandSource> ctx) {
		BlockPos from = BlockPosArgumentType.getBlockPos(ctx, "from");
		BlockPos to = BlockPosArgumentType.getBlockPos(ctx, "to");
		String type = StringArgumentType.getString(ctx, "type");
		NbtCompound nbt = NbtCompoundArgumentType.getNbtCompound(ctx, "nbt");
		EnvironementAreas areas = ctx.getSource().getWorld().getComponent(Components.ENVIRONMENT_AREAS);
		var fac = EnvironementAreas.FACTORIES.get(type);
		if(fac != null) {
			var created = fac.get().fromNBT(nbt);
			created.setArea(new Box(from, to));
			areas.add(created);
			EnvironmentPacket.sendListForAll(ctx.getSource().getWorld());
			ctx.getSource().sendFeedback(() -> Text.literal("Zone crée."), false);
			return 1;
		}
		ctx.getSource().sendFeedback(() -> Text.literal("Type inconnu: " + type), false);
		return -1;
	}


	private static int getList(CommandContext<ServerCommandSource> ctx) {
		int page = 0;

		try {
			page = IntegerArgumentType.getInteger(ctx, "page");
		} catch(IllegalArgumentException ignored) {
		}
		var base = Text.literal("====Liste des zones environmental [page " + page + "]====\n");
		var ls = ctx.getSource().getWorld().getComponent(Components.ENVIRONMENT_AREAS).getAreas();
		for(int i = page * 10; i < ls.size(); i++) {
			var b = ls.get(i);
			base = base.append(getDisplayVersion(b));
		}
		int finalPage = page;
		var arrowA = Text.literal("[<]").styled(st -> st.withColor(Formatting.LIGHT_PURPLE).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(("Précédent")))).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/environmentAreas list " + (finalPage - 1))));
		var arrowB = Text.literal("[>]").styled(st -> st.withColor(Formatting.LIGHT_PURPLE).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(("Suivant")))).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/environmentAreas list " + (finalPage + 1))));
		base = base.append("====");
		if(page > 0)
			base = base.append(arrowA).append(Text.literal(" "));
		base = base.append(arrowB).append("====");
		net.minecraft.text.MutableText finalBase = base;
		ctx.getSource().sendFeedback(() -> finalBase, false);
		return 1;
	}

	private static int getExec(CommandContext<ServerCommandSource> ctx) {
		BlockPos p = BlockPosArgumentType.getBlockPos(ctx, "at");
		EnvironementAreas areas = ctx.getSource().getWorld().getComponent(Components.ENVIRONMENT_AREAS);
		var ls = areas.getAt(p.toCenterPos());
		var base = Text.literal("====Zones à " + p + "====\n");
		for(EffectArea b : ls) {
			base = base.append(getDisplayVersion(b));
		}
		base = base.append("================");

		net.minecraft.text.MutableText finalBase = base;
		ctx.getSource().sendFeedback(() -> finalBase, false);

		return ls.isEmpty() ? 0 : 1;

	}

	private static MutableText getDisplayVersion(EffectArea area) {
		return RecipeHelperCommand.copyable(Text.literal(area.getArea() + "\n"), area.toNBT().toString()).styled(st -> st.withColor(Formatting.GREEN).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("NBT datas: " + area.toNBT() + "\n(Cliquez pour copier)"))));
	}

	private static int removeExec(CommandContext<ServerCommandSource> ctx) {
		BlockPos p = BlockPosArgumentType.getBlockPos(ctx, "at");
		EnvironementAreas areas = ctx.getSource().getWorld().getComponent(Components.ENVIRONMENT_AREAS);
		var ls = areas.getAtFirst(p.toCenterPos());
		if(ls.isPresent()) {
			areas.remove(ls.get());

			ctx.getSource().sendFeedback(() -> RecipeHelperCommand.copyable(Text.literal("Zone supprimé à '" + p + "'"), ls.get().toNBT().toString()).styled(st -> st.withColor(Formatting.GREEN).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("NBT datas: " + ls.get().toNBT() + "\n(Cliquez pour copier)")))), false);
			return 1;
		}
		ctx.getSource().sendFeedback(() -> Text.literal("Pas de zone en :[" + p.getX() + "," + p.getY() + "," + p.getZ() + "]"), false);
		return -1;

	}
}
