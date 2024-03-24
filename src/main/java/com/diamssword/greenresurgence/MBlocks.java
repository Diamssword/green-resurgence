package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import com.diamssword.greenresurgence.blocks.LootedBlock;
import com.diamssword.greenresurgence.structure.StructurePlacerInstance;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class MBlocks implements BlockRegistryContainer {


    public static final LootedBlock LOOTED_BLOCK = new LootedBlock(AbstractBlock.Settings.create().nonOpaque().dropsNothing().strength(99999,99999).suffocates(Blocks::never));
    public static final BlockEntityType<LootedBlockEntity> LOOTED_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,new Identifier(GreenResurgence.ID, "looted_block"),
            FabricBlockEntityTypeBuilder.create(LootedBlockEntity::new, LOOTED_BLOCK).build()
    );
    @Override
    public void afterFieldProcessing() {

    }
    @Override
    public BlockItem createBlockItem(Block block, String identifier) {
        return new BlockItem(block, new OwoItemSettings().group(MItems.GROUP));
    }
}
