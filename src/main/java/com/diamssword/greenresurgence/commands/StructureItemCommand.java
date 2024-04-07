package com.diamssword.greenresurgence.commands;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.items.IStructureProvider;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.advancement.Advancement;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.*;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

import java.util.Collection;
import java.util.stream.Stream;

import static net.minecraft.server.command.CommandManager.*;

public class StructureItemCommand {

    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> {
        IStructureProvider.StructureType type =StructureTypeArgumentType.getStructureType(context,"type");
        if(type== IStructureProvider.StructureType.jigsaw) {
            Collection<RegistryKey<StructurePool>> collection = ((ServerCommandSource) context.getSource()).getServer().getRegistryManager().get(RegistryKeys.TEMPLATE_POOL).getKeys();
            return CommandSource.suggestIdentifiers(collection.stream().map(RegistryKey::getValue), builder);
        }
        else
        {
            Stream<Identifier> collection = ((ServerCommandSource) context.getSource()).getServer().getStructureTemplateManager().streamTemplates();
            return CommandSource.suggestIdentifiers(collection, builder);
        }
    };

    public static void register(LiteralArgumentBuilder<ServerCommandSource> builder)
    {
        builder.requires(ctx-> ctx.hasPermissionLevel(2))
                .then(CommandManager.argument("targets", EntityArgumentType.players())
                .then(argument("type", StructureTypeArgumentType.structureType())
                .then(argument("structure", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER)
                .executes(ctx->{
                    Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "targets");
                    Identifier id =IdentifierArgumentType.getIdentifier(ctx,"structure");
                    IStructureProvider.StructureType type =StructureTypeArgumentType.getStructureType(ctx,"type");
                    ItemStack st=new ItemStack(MItems.UNIVERSAL_PLACER);
                    NbtCompound tag=st.getOrCreateNbt();
                    tag.putString("structure",id.toString());
                    tag.putInt("type",type.id);
                    players.forEach(v->{
                        v.giveItemStack(st);
                    });
                   return 1;
                }))));
        ArgumentTypeRegistry.registerArgumentType(new Identifier(GreenResurgence.ID,"structure_type"), StructureTypeArgumentType.class,ConstantArgumentSerializer.of(StructureTypeArgumentType::structureType));

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
