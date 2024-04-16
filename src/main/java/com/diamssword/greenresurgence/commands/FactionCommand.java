package com.diamssword.greenresurgence.commands;

import com.diamssword.greenresurgence.items.IStructureProvider;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.perimeter.FactionInstance;
import com.diamssword.greenresurgence.systems.faction.perimeter.IFactionList;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.*;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Optional;

public class FactionCommand {

    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> {
        World w=context.getSource().getWorld();
        IFactionList ls=w.getComponent(Components.BASE_LIST);
        return CommandSource.suggestMatching(ls.getNames(), builder);

    };

    public static void register(LiteralArgumentBuilder<ServerCommandSource> builder)
    {
        builder.requires(ctx-> ctx.hasPermissionLevel(2))
                .then(CommandManager.literal("create").then(CommandManager.argument("name",StringArgumentType.string()).then(CommandManager.argument("from", BlockPosArgumentType.blockPos()).then(CommandManager.argument("to",BlockPosArgumentType.blockPos()).executes(FactionCommand::createExec)))))
                .then(CommandManager.literal("get").then(CommandManager.argument("at", BlockPosArgumentType.blockPos()).executes(FactionCommand::getExec)))
                .then(CommandManager.literal("addArea").then(CommandManager.argument("name",StringArgumentType.string()).suggests(SUGGESTION_PROVIDER).then(CommandManager.argument("from", BlockPosArgumentType.blockPos()).then(CommandManager.argument("to",BlockPosArgumentType.blockPos()).executes(FactionCommand::addExec)))))
                .then(CommandManager.literal("removeArea").then(CommandManager.argument("at", BlockPosArgumentType.blockPos()).executes(FactionCommand::removeExec)))
                .then(CommandManager.literal("addMember").then(CommandManager.argument("faction",StringArgumentType.string()).suggests(SUGGESTION_PROVIDER).then(CommandManager.argument("player", EntityArgumentType.players()).executes(FactionCommand::addMemberExec))))
                .then(CommandManager.literal("removeMember").then(CommandManager.argument("faction",StringArgumentType.string()).suggests(SUGGESTION_PROVIDER).then(CommandManager.argument("player", EntityArgumentType.players()).executes(FactionCommand::removeMemberExec))))
                .then(CommandManager.literal("delete").then(CommandManager.argument("name",StringArgumentType.string()).suggests(SUGGESTION_PROVIDER).then(CommandManager.argument("confirm", StringArgumentType.string()).executes(FactionCommand::deleteExec))))
                .then(CommandManager.literal("refresh").executes(ctx->{Components.BASE_LIST.sync(ctx.getSource().getWorld()); return 1;}));

    }
    private static int getExec(CommandContext<ServerCommandSource> ctx)
    {
        BlockPos p=BlockPosArgumentType.getBlockPos(ctx,"at");
        IFactionList base=ctx.getSource().getWorld().getComponent(Components.BASE_LIST);
        Optional<FactionInstance> b=base.getAt(p);
        b.ifPresentOrElse((i)->{
            ctx.getSource().sendFeedback(()->Text.literal("Base trouvée en ["+p.getX()+","+p.getY()+","+p.getZ()+"]: "+i.getName()),false);
        },()->{
            ctx.getSource().sendFeedback(()->Text.literal("Aucune base trouvée en ["+p.getX()+","+p.getY()+","+p.getZ()+"]"),false);
        });
        return b.isPresent()?1:-1;

    }
    private static int deleteExec(CommandContext<ServerCommandSource> ctx)
    {
        String name= StringArgumentType.getString(ctx,"name");
        String confirm=StringArgumentType.getString(ctx,"confirm");
        if(confirm.equals("Confirm"))
        {
            IFactionList base=ctx.getSource().getWorld().getComponent(Components.BASE_LIST);
            boolean t=base.delete(name);
            if(!t) {
                ctx.getSource().sendFeedback(() -> Text.literal("Aucune faction trouvé avec ce nom"), false);
                return -1;
            }
            else
            {
                ctx.getSource().sendFeedback(() -> Text.literal("Faction "+name+" supprimée!"), false);

                Components.BASE_LIST.sync(ctx.getSource().getWorld());
                return 1;
            }
        }
        else {
            ctx.getSource().sendFeedback(()->Text.literal("Cette action supprimera definitivement toute les zones d'une factions, pour confirmer tapez le text 'Confirm' (avec la maj) dans la commande"),false);
            return 0;
        }

    }
    private static int createExec(CommandContext<ServerCommandSource> ctx)
    {
        String name= StringArgumentType.getString(ctx,"name");
        BlockPos p=BlockPosArgumentType.getBlockPos(ctx,"from");
        BlockPos p1=BlockPosArgumentType.getBlockPos(ctx,"to");
        IFactionList base=ctx.getSource().getWorld().getComponent(Components.BASE_LIST);

        boolean re= base.add(new FactionInstance(name,new BlockBox(p1.getX(),p1.getY(),p1.getZ(),p.getX(),p.getY(),p.getZ())));
            ctx.getSource().sendFeedback(()->Text.literal(re?"Faction '"+name+"' crée":"Impossible de créer la faction '"+name+"'. Elle existe peut être déja?"),false);
        return re?1:0;

    }
    private static int addMemberExec(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        String name= StringArgumentType.getString(ctx,"faction");
        Collection<ServerPlayerEntity> p=EntityArgumentType.getPlayers(ctx,"player");

        IFactionList base=ctx.getSource().getWorld().getComponent(Components.BASE_LIST);
            Optional<FactionInstance> b=base.get(name);
            if(b.isPresent())
            {
                p.forEach(v->{
                    b.get().addMember(v);
                    ctx.getSource().sendFeedback(()->Text.literal("Ajout de ").append(v.getName()).append(Text.literal(" à la faction '"+b.get().getName()+"'")),false);
                });
                return 1;
            }
        ctx.getSource().sendFeedback(()->Text.literal("Impossible de trouver cette faction"),false);
            return -1;


    }
    private static int removeMemberExec(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        String name= StringArgumentType.getString(ctx,"faction");
        Collection<ServerPlayerEntity> p=EntityArgumentType.getPlayers(ctx,"player");

        IFactionList base=ctx.getSource().getWorld().getComponent(Components.BASE_LIST);
        Optional<FactionInstance> b=base.get(name);
        if(b.isPresent())
        {
            p.forEach(v->{
                b.get().removeMember(v);
                ctx.getSource().sendFeedback(()->Text.literal("Suppression de ").append(v.getName()).append(Text.literal(" à la faction '"+b.get().getName()+"'")),false);
            });
            return 1;
        }
        ctx.getSource().sendFeedback(()->Text.literal("Impossible de trouver cette faction"),false);
        return -1;


    }
    private static int addExec(CommandContext<ServerCommandSource> ctx)
    {
        String name= StringArgumentType.getString(ctx,"name");
        BlockPos p=BlockPosArgumentType.getBlockPos(ctx,"from");
        BlockPos p1=BlockPosArgumentType.getBlockPos(ctx,"to");
        IFactionList bases=ctx.getSource().getWorld().getComponent(Components.BASE_LIST);
        Optional<FactionInstance> base=bases.get(name);
        if(base.isPresent())
        {
            base.get().addArea(new BlockBox(p1.getX(),p1.getY(),p1.getZ(),p.getX(),p.getY(),p.getZ()));
            Components.BASE_LIST.sync(ctx.getSource().getWorld());
            ctx.getSource().sendFeedback(()->Text.literal("Zone de ajoutée pour '"+name+"':["+p.getX()+","+p.getY()+","+p.getZ()+"] à ["+p1.getX()+","+p1.getY()+","+p1.getZ()+"]"),false);
            return 1;
        }
        ctx.getSource().sendFeedback(()->Text.literal("Faction introuvable pour '"+name+"'"),false);
        return -1;

    }
    private static int removeExec(CommandContext<ServerCommandSource> ctx)
    {
        BlockPos p=BlockPosArgumentType.getBlockPos(ctx,"at");
        IFactionList bases=ctx.getSource().getWorld().getComponent(Components.BASE_LIST);
        Optional<FactionInstance> base=bases.getAt(p);
        if(base.isPresent())
        {
                boolean flg=base.get().removeAreaAt(p);
                if(flg)
                {
                    ctx.getSource().sendFeedback(()->Text.literal("Zone supprimé pour '"+base.get().getName()+"'"),false);
                    Components.BASE_LIST.sync(ctx.getSource().getWorld());
                }
                else
                    ctx.getSource().sendFeedback(()->Text.literal("Faction introuvable pour en :["+p.getX()+","+p.getY()+","+p.getZ()+"]"),false);
                return flg?1:-1;
        }
        ctx.getSource().sendFeedback(()->Text.literal("Faction introuvable pour en :["+p.getX()+","+p.getY()+","+p.getZ()+"]"),false);
        return -1;

    }
    public static class StructureTypeArgumentType extends EnumArgumentType<IStructureProvider.StructureType> {
        private StructureTypeArgumentType() {
            super( StringIdentifiable.createCodec(IStructureProvider.StructureType::values), IStructureProvider.StructureType::values);
        }

        public static StructureTypeArgumentType structureType() {
            return new StructureTypeArgumentType();
        }

        public static IStructureProvider.StructureType getStructureType(CommandContext<ServerCommandSource> context, String id) {
            return context.getArgument(id, IStructureProvider.StructureType.class);
        }
    }
}
