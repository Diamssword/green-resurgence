package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.blockEntities.ConnectorBlockEntity;
import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import com.diamssword.greenresurgence.blocks.ConnectorBlock;
import com.diamssword.greenresurgence.blocks.LootedBlock;
import com.diamssword.greenresurgence.blocks.PostBlock;
import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MBlocks implements BlockRegistryContainer {
    private static List<Block> addToDiamsTab= new ArrayList<>();


    public static final LootedBlock LOOTED_BLOCK = new LootedBlock(AbstractBlock.Settings.create().nonOpaque().dropsNothing().strength(99999,99999).suffocates(Blocks::never));
    public static final ConnectorBlock CONNECTOR = addToDiamsTab(new ConnectorBlock(AbstractBlock.Settings.create().nonOpaque().strength(99999,99999).suffocates(Blocks::never)));
    public static final PostBlock ELECTRIC_POST=addToDiamsTab(new PostBlock(AbstractBlock.Settings.create().nonOpaque().sounds(BlockSoundGroup.WOOD),true));
    public static final PostBlock WOOD_POST=addToDiamsTab(new PostBlock(AbstractBlock.Settings.create().nonOpaque().sounds(BlockSoundGroup.WOOD),false));
    public static final BlockEntityType<LootedBlockEntity> LOOTED_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,new Identifier(GreenResurgence.ID, "looted_block"),
            FabricBlockEntityTypeBuilder.create(LootedBlockEntity::new, LOOTED_BLOCK).build()
    );
    public static final BlockEntityType<ConnectorBlockEntity> CONNECTOR_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,new Identifier(GreenResurgence.ID, "connector_block"),
            FabricBlockEntityTypeBuilder.create(ConnectorBlockEntity::new, LOOTED_BLOCK).build()
    );
    @Override
    public void afterFieldProcessing() {

    }
    @Override
    public BlockItem createBlockItem(Block block, String identifier) {
        if(addToDiamsTab.contains(block))
            return new BlockItem(block, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(1));
        return new BlockItem(block, new OwoItemSettings().group(MItems.GROUP));
    }
    public static <T extends Block> T addToDiamsTab(T b)
    {
        addToDiamsTab.add(b);
        return b;
    }
}
