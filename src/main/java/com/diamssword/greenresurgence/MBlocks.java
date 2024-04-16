package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.blockEntities.ConnectorBlockEntity;
import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import com.diamssword.greenresurgence.blocks.*;
import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MBlocks implements BlockRegistryContainer {

    @NoItemGroup
    public static final LootedBlock LOOTED_BLOCK = new LootedBlock(AbstractBlock.Settings.create().nonOpaque().dropsNothing().strength(99999,99999).suffocates(Blocks::never));
    @DiamsGroup
    public static final ConnectorBlock CONNECTOR = new ConnectorBlock(AbstractBlock.Settings.create().nonOpaque().strength(99999,99999).suffocates(Blocks::never));
    @DiamsGroup
    public static final PostBlock ELECTRIC_POST=new PostBlock(AbstractBlock.Settings.create().nonOpaque().sounds(BlockSoundGroup.WOOD),true);
    @DiamsGroup
    public static final PostBlock WOOD_POST=new PostBlock(AbstractBlock.Settings.create().nonOpaque().sounds(BlockSoundGroup.WOOD),false);
    public static final StructureBlock STRUCTURE_BLOCK =new StructureBlock(FabricBlockSettings.create().resistance(20000).solidBlock((_1, __, ___)->false).nonOpaque());
    public static final SmartStructureBlock STRUCTURE_BLOCK_SMART =new SmartStructureBlock(FabricBlockSettings.create().resistance(20000).solidBlock((_1, __, ___)->false).nonOpaque());
    public static final BlockEntityType<LootedBlockEntity> LOOTED_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,new Identifier(GreenResurgence.ID, "looted_block"),
            FabricBlockEntityTypeBuilder.create(LootedBlockEntity::new, LOOTED_BLOCK).build()
    );
    public static final BlockEntityType<ConnectorBlockEntity> CONNECTOR_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,new Identifier(GreenResurgence.ID, "connector_block"),
            FabricBlockEntityTypeBuilder.create(ConnectorBlockEntity::new, LOOTED_BLOCK).build()
    );
    public static final BlockEntityType<ConnectorBlockEntity> STRUCUTRE_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,new Identifier(GreenResurgence.ID, "smart_structure_block"),
            FabricBlockEntityTypeBuilder.create(ConnectorBlockEntity::new, STRUCTURE_BLOCK_SMART).build()
    );
    @Override
    public void afterFieldProcessing() {

    }
    @Override
    public void postProcessField(String namespace, Block value, String identifier, Field field) {
        // preserve normal traversal behaviour
        if (field.isAnnotationPresent(NoItemGroup.class)) return;
        Item i;
        if(field.isAnnotationPresent(DiamsGroup.class))
            i= new BlockItem(value, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(1));
        else
            i= new BlockItem(value, new OwoItemSettings().group(MItems.GROUP));
        Registry.register(Registries.ITEM, new Identifier(namespace, identifier),i);

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface NoItemGroup {}
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface DiamsGroup {}
}
