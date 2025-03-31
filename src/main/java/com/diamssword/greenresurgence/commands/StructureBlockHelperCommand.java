package com.diamssword.greenresurgence.commands;

import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.perimeter.FactionList;
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

public class StructureBlockHelperCommand {

    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> {
        World w=context.getSource().getWorld();
        FactionList ls=w.getComponent(Components.BASE_LIST);
        return CommandSource.suggestMatching(ls.getNames(), builder);

    };

    public static void register(LiteralArgumentBuilder<ServerCommandSource> builder)
    {
        builder.requires(ctx-> ctx.hasPermissionLevel(2))
                .then(CommandManager.literal("rename").then(CommandManager.argument("from", BlockPosArgumentType.blockPos()).then(CommandManager.argument("to",BlockPosArgumentType.blockPos()).then(CommandManager.argument("startAt", IntegerArgumentType.integer(0)).executes(StructureBlockHelperCommand::renameExec)))))
                .then(CommandManager.literal("save").then(CommandManager.argument("from", BlockPosArgumentType.blockPos()).then(CommandManager.argument("to",BlockPosArgumentType.blockPos()).executes(StructureBlockHelperCommand::saveExec))));


    }
    private static int renameExec(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Map<String,Integer> map=new HashMap<>();
        int start=IntegerArgumentType.getInteger(ctx,"startAt");
        int res=iterateBlocks(ctx,s->{
            String n=s.getTemplateName();
            int ind=n.lastIndexOf('_');
            if(ind>-1)
            {

                if(n.length()>ind+1 && StringUtils.isNumeric(n.substring(ind+1)))
                {
                    Integer ct=map.getOrDefault(n.substring(0,ind),start);
                    s.setTemplateName(n.substring(0,ind)+"_"+ct);
                    s.markDirty();
                    ctx.getSource().getWorld().updateListeners(s.getPos(),s.getCachedState(),s.getCachedState(), Block.NOTIFY_ALL);
                    map.put(n.substring(0,ind),ct+1);
                    ctx.getSource().sendFeedback(()->Text.literal("Sauvegarde de "+s.getTemplateName()),false);

                }


            }
        });
        ctx.getSource().sendFeedback(()->Text.literal("Sauvgarde de "+res+" structures!"),false);
        return res;

    }
    private static int saveExec(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {



            int res=iterateBlocks(ctx, StructureBlockBlockEntity::saveStructure);
        ctx.getSource().sendFeedback(()->Text.literal("Sauvgarde de "+res+" structures!"),false);
            return res;

    }
    private static int iterateBlocks(CommandContext<ServerCommandSource> ctx, Consumer<StructureBlockBlockEntity> forEach) throws CommandSyntaxException {
        BlockBox range=BlockBox.create(BlockPosArgumentType.getLoadedBlockPos(ctx,"from"),BlockPosArgumentType.getLoadedBlockPos(ctx,"to"));
        int k=0;
        Predicate<CachedBlockPosition> filter=(v)->v.getBlockState().getBlock()== Blocks.STRUCTURE_BLOCK;
        for (BlockPos blockPos : BlockPos.iterate(range.getMinX(), range.getMinY(), range.getMinZ(), range.getMaxX(), range.getMaxY(), range.getMaxZ())) {

            if (filter.test(new CachedBlockPosition(ctx.getSource().getWorld(), blockPos, true)) )
            {
                BlockEntity ent= ctx.getSource().getWorld().getBlockEntity(blockPos);
                if(ent instanceof StructureBlockBlockEntity ste)
                {
                    forEach.accept(ste);
                    k++;
                }
            }
        }
        return k;
    }
}
