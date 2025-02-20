package com.diamssword.greenresurgence.commands;

import com.diamssword.greenresurgence.systems.crafting.SimpleRecipe;
import com.diamssword.greenresurgence.systems.crafting.UniversalResource;
import com.google.gson.*;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RecipeHelperCommand {

    public static void register(LiteralArgumentBuilder<ServerCommandSource> builder)
    {
        builder.requires(ctx-> ctx.hasPermissionLevel(2))
                .then(CommandManager.literal("getTags").executes(RecipeHelperCommand::getTagsExec))
              .then(CommandManager.literal("createRecipe").then(CommandManager.argument("chest", BlockPosArgumentType.blockPos())
                              .then(CommandManager.argument("name", StringArgumentType.string()).executes(RecipeHelperCommand::createRecipe))))
                .then(CommandManager.literal("addToRecipe").then(CommandManager.argument("chest", BlockPosArgumentType.blockPos()).then(CommandManager.argument("name", StringArgumentType.string()).executes(RecipeHelperCommand::addRecipe))))
                        .then(CommandManager.literal("listblocks").then(CommandManager.argument("from", BlockPosArgumentType.blockPos()).then(CommandManager.argument("to", BlockPosArgumentType.blockPos()).executes(RecipeHelperCommand::listblocks))));

    }
    public static MutableText copyable(MutableText text,String copied)
    {
        return text.styled(style-> style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, copied)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.copy.click"))).withInsertion(copied));
    }
    public static MutableText open(MutableText text,File file)
    {
        return text.styled(style-> style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getParentFile().getAbsolutePath())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Ouvrir le dossier"))));
    }
    public static Inventory getBlockInventory(ServerCommandSource source, BlockPos pos) throws CommandSyntaxException {
        BlockEntity blockEntity = source.getWorld().getBlockEntity(pos);
        if (!(blockEntity instanceof Inventory)) {
            throw new Dynamic3CommandExceptionType((x, y, z) -> Text.translatable("commands.item.target.not_a_container", new Object[]{x, y, z})).create(pos.getX(), pos.getY(), pos.getZ());
        } else {
            return (Inventory)blockEntity;
        }
    }
    private static int listblocks(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        BlockBox range=BlockBox.create(BlockPosArgumentType.getLoadedBlockPos(ctx,"from"),BlockPosArgumentType.getLoadedBlockPos(ctx,"to"));
        List<Block> blocks=new ArrayList<>();
        for (BlockPos blockPos : BlockPos.iterate(range.getMinX(), range.getMinY(), range.getMinZ(), range.getMaxX(), range.getMaxY(), range.getMaxZ())) {
            var d=new CachedBlockPosition(ctx.getSource().getWorld(), blockPos, true);
            if(!d.getBlockState().isAir() && !blocks.contains(d.getBlockState().getBlock()))
            {
                blocks.add(d.getBlockState().getBlock());
            }
        }
        var t1=Texts.join(blocks, Optional.of(Text.literal(" ")),v->Text.literal(Registries.BLOCK.getId(v).toString()));
        ctx.getSource().sendFeedback(()->copyable(t1,t1.getString()),false);
        return 1;
    }
    private static int addRecipe(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var pos=BlockPosArgumentType.getLoadedBlockPos(ctx,"chest");
        var storage=getBlockInventory(ctx.getSource(),pos);
        List<ItemStack> ingrs=new ArrayList<>();
        for (int i = 0; i < storage.size(); i++) {
            if(!storage.getStack(i).isEmpty())
                ingrs.add(storage.getStack(i));
        }
        var path=ctx.getSource().getWorld().getServer().getSavePath(WorldSavePath.DATAPACKS);
        var id=StringArgumentType.getString(ctx,"name");
        var fold=new File(path.toFile(),"generatedr/green_resurgence/grecipes/"+id+".json");
        try {
            JsonObject recip = JsonParser.parseString(FileUtils.readFileToString(fold, StandardCharsets.UTF_8)).getAsJsonObject();

        var base=new ArrayList<UniversalResource>();
        if(recip.has("results"))
        {
            recip.get("results").getAsJsonArray().forEach(v-> {
                try {
                    base.add(UniversalResource.deserializer(v.getAsJsonObject()));
                } catch (Exception e) {
                    ctx.getSource().sendError(Text.literal("can't read json"));

                }
            });
        }
        else if(recip.has("result"))
        {
            try {
                base.add(UniversalResource.deserializer(recip.get("result").getAsJsonObject()));
            } catch (Exception e) {
                ctx.getSource().sendError(Text.literal("can't read json"));
                return -1;
            }

        }
        ingrs.forEach(v->base.add(UniversalResource.fromItem(v)));
        var arr=new JsonArray();
        base.forEach(v->arr.add(v.serializer()));
        recip.remove("result");
        recip.add("results",arr);
        recip.addProperty("type","multi");
        ctx.getSource().sendFeedback(()->copyable(Text.literal("[JSON crée, cliquez pour copier]"),recip.toString()),false);
        try {
            fold.getParentFile().mkdirs();
            FileWriter w=new FileWriter(fold);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            w.write(gson.toJson(recip));
            w.close();
            ctx.getSource().sendFeedback(()->open(Text.literal("[Recette Modifiée]"),fold),false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        } catch (IOException e) {
            ctx.getSource().sendError(Text.literal("can't read json"));
            return -1;
        }
        return 1;
    }
    private static int createRecipe(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var pos=BlockPosArgumentType.getLoadedBlockPos(ctx,"chest");
        var storage=getBlockInventory(ctx.getSource(),pos);
        List<ItemStack> ingrs=new ArrayList<>();
        for (int i = 0; i < storage.size()-1; i++) {
            if(!storage.getStack(i).isEmpty())
                ingrs.add(storage.getStack(i));
        }
        var res=storage.getStack(storage.size()-1);
        var i=new SimpleRecipe(UniversalResource.fromItem(res),ingrs.stream().map(UniversalResource::fromItem).toList());
        var sel=i.serialize();
        sel.addProperty("type","simple");
        var path=ctx.getSource().getWorld().getServer().getSavePath(WorldSavePath.DATAPACKS);
        var id=StringArgumentType.getString(ctx,"name");
        var fold=new File(path.toFile(),"generatedr/green_resurgence/grecipes/"+id+".json");
        ctx.getSource().sendFeedback(()->copyable(Text.literal("[JSON modifié, cliquez pour copier]"),sel.toString()),false);
        try {
            fold.getParentFile().mkdirs();
            FileWriter w=new FileWriter(fold);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            w.write(gson.toJson(sel));
            w.close();
            ctx.getSource().sendFeedback(()->open(Text.literal("[Recette Sauvegardée]"),fold),false);
        } catch (IOException e) {
          e.printStackTrace();
        }
        return 1;
    }
    private static int getTagsExec(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        if(ctx.getSource().isExecutedByPlayer())
        {
            var item=ctx.getSource().getPlayer().getMainHandStack();
            ctx.getSource().sendFeedback(()->Text.literal("ItemTags:").formatted(Formatting.BOLD),false);
            var ls=Texts.join(item.streamTags().toList(), Optional.empty(),v->Text.literal(v.id().toString()));
            ctx.getSource().sendFeedback(()->copyable(ls,ls.getString()),false);
            if(item.getItem() instanceof BlockItem bi)
            {
                ctx.getSource().sendFeedback(()->Text.literal("\nBlockTags:").formatted(Formatting.BOLD),false);
                var ls1=Texts.join(bi.getBlock().getDefaultState().streamTags().toList(), Optional.empty(),v->Text.literal(v.id().toString()));
                ctx.getSource().sendFeedback(()->copyable(ls1,ls1.getString()),false);
            }
            return 1;
        }
        else
            ctx.getSource().sendError(Text.literal("Must be executed by player"));
        return -1;
    }
}
