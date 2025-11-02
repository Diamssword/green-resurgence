package com.diamssword.greenresurgence.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class WorldToolsCommand {

	public static void register(LiteralArgumentBuilder<ServerCommandSource> builder) {
		//	var apply=CommandManager.literal("apply").then(CommandManager.argument("from", BlockPosArgumentType.blockPos()).then(CommandManager.argument("to", BlockPosArgumentType.blockPos()).executes(WorldToolsCommand::selectPatternChest)));
		var select = CommandManager.literal("select").then(CommandManager.argument("chest", BlockPosArgumentType.blockPos()).executes(WorldToolsCommand::selectPatternChest));
		builder.requires(ctx -> ctx.hasPermissionLevel(2))
				.then(CommandManager.literal("palette").then(select));
		//.then(CommandManager.literal("save").then(CommandManager.argument("from", BlockPosArgumentType.blockPos()).then(CommandManager.argument("to", BlockPosArgumentType.blockPos()).executes(WorldToolsCommand::saveExec))));
	}

	private static void sendWEReplaceCommand(CommandContext<ServerCommandSource> ctx, ItemStack in, ItemStack out) {
		if(in.getItem() instanceof BlockItem ib) {
			var b1 = ib.getBlock();
			if(out.getItem() instanceof BlockItem ib1) {
				String cmd = "//replace " + Registries.BLOCK.getId(b1) + " " + Registries.BLOCK.getId(ib1.getBlock());
				ctx.getSource().sendFeedback(() -> Text.literal(cmd).styled(s -> s.withColor(Formatting.AQUA).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Cliquez pour executer")))), false);
			}
		}
	}

	private static int selectPatternChest(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		var pos = BlockPosArgumentType.getLoadedBlockPos(ctx, "chest");
		var inventory = RecipeHelperCommand.getBlockInventory(ctx.getSource(), pos);
		if(inventory != null) {
			Map<Block, Block> map = new HashMap<>();
			boolean simple = inventory.size() == 27;
			if(!simple && inventory.size() != 54)
				throw new Dynamic3CommandExceptionType((x, y, z) -> Text.literal("The target container at " + x + " " + y + " " + z + " should be a normal or double chest")).create(pos.getX(), pos.getY(), pos.getZ());
			ctx.getSource().sendFeedback(() -> Text.literal("===Palette du coffre[" + pos.getX() + " " + pos.getY() + " " + pos.getZ() + "]===").formatted(Formatting.GREEN), false);
			ctx.getSource().sendFeedback(() -> Text.literal("(Cliquez sur une commande pour l'executer directement)"), false);

			for(var pass = 0; pass < (simple ? 1 : 3); pass++) {
				for(int i = 0; i < 9; i++) {
					sendWEReplaceCommand(ctx, inventory.getStack((pass * 18) + i), inventory.getStack((pass * 18) + 9 + i));
				}
			}
			ctx.getSource().sendFeedback(() -> Text.literal("=================").formatted(Formatting.GREEN), false);
		}
		return 1;
	}

	private static int switchPaletteExec(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
		Map<String, Integer> map = new HashMap<>();
		int start = IntegerArgumentType.getInteger(ctx, "startAt");
		int res = iterateBlocks(ctx, s -> {
			String n = s.getTemplateName();
			int ind = n.lastIndexOf('_');
			if(ind > -1) {

				if(n.length() > ind + 1 && StringUtils.isNumeric(n.substring(ind + 1))) {
					Integer ct = map.getOrDefault(n.substring(0, ind), start);
					s.setTemplateName(n.substring(0, ind) + "_" + ct);
					s.markDirty();
					ctx.getSource().getWorld().updateListeners(s.getPos(), s.getCachedState(), s.getCachedState(), Block.NOTIFY_ALL);
					map.put(n.substring(0, ind), ct + 1);
					ctx.getSource().sendFeedback(() -> Text.literal("Sauvegarde de " + s.getTemplateName()), false);

				}


			}
		});
		ctx.getSource().sendFeedback(() -> Text.literal("Sauvgarde de " + res + " structures!"), false);
		return res;

	}

	private static int saveExec(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {


		int res = iterateBlocks(ctx, StructureBlockBlockEntity::saveStructure);
		ctx.getSource().sendFeedback(() -> Text.literal("Sauvgarde de " + res + " structures!"), false);
		return res;

	}

	private static int iterateBlocks(CommandContext<ServerCommandSource> ctx, Consumer<StructureBlockBlockEntity> forEach) throws CommandSyntaxException {
		BlockBox range = BlockBox.create(BlockPosArgumentType.getLoadedBlockPos(ctx, "from"), BlockPosArgumentType.getLoadedBlockPos(ctx, "to"));
		int k = 0;
		Predicate<CachedBlockPosition> filter = (v) -> v.getBlockState().getBlock() == Blocks.STRUCTURE_BLOCK;
		for(BlockPos blockPos : BlockPos.iterate(range.getMinX(), range.getMinY(), range.getMinZ(), range.getMaxX(), range.getMaxY(), range.getMaxZ())) {

			if(filter.test(new CachedBlockPosition(ctx.getSource().getWorld(), blockPos, true))) {
				BlockEntity ent = ctx.getSource().getWorld().getBlockEntity(blockPos);
				if(ent instanceof StructureBlockBlockEntity ste) {
					forEach.accept(ste);
					k++;
				}
			}
		}
		return k;
	}
}
