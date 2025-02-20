package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.blockEntities.*;
import io.wispforest.owo.registration.reflect.BlockEntityRegistryContainer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.StructureBlockBlockEntity;

import static com.diamssword.greenresurgence.MBlocks.*;

public class MBlockEntities implements BlockEntityRegistryContainer {

    public static final BlockEntityType<LootedBlockEntity> LOOTED_BLOCk= FabricBlockEntityTypeBuilder.create(LootedBlockEntity::new, LOOTED_BLOCK).build();
    public static final BlockEntityType<ConnectorBlockEntity> CONNECTOR_BLOCK= FabricBlockEntityTypeBuilder.create(ConnectorBlockEntity::new, MBlocks.CONNECTOR).build();
    public static final BlockEntityType<StructureBlockBlockEntity> SMART_STRUCTURE_BLOCK= FabricBlockEntityTypeBuilder.create(StructureBlockBlockEntity::new, MBlocks.STRUCTURE_BLOCK_SMART).build();
    public static final BlockEntityType<ItemBlockEntity> ITEM_BLOCK= FabricBlockEntityTypeBuilder.create(ItemBlockEntity::new, MBlocks.ITEM_BLOCK).build();
    public static final BlockEntityType<LootableItemBlockEntity> LOOT_ITEM_BLOCK= FabricBlockEntityTypeBuilder.create(LootableItemBlockEntity::new, MBlocks.LOOT_ITEM_BLOCK).build();
    public static final BlockEntityType<LootableShelfEntity> LOOTABLE_SHELF= FabricBlockEntityTypeBuilder.create(LootableShelfEntity::new, MBlocks.SHELF_BLOCK, SIDEWAY_SHELF_BLOCK,WOOD_CRATE_SHELF_BLOCK).build();
    public static final BlockEntityType<ImageBlockEntity> IMAGE_BLOCK= FabricBlockEntityTypeBuilder.create(ImageBlockEntity::new, MBlocks.IMAGE_BLOCK).build();
    public static final BlockEntityType<GenericStorageBlockEntity> GENERIC_STORAGE= FabricBlockEntityTypeBuilder.create(GenericStorageBlockEntity::new, BASE_CRATE_T1,BASE_CRATE_T2).build();
    public static final BlockEntityType<GeneratorBlockEntity> GENERATOR= FabricBlockEntityTypeBuilder.create(GeneratorBlockEntity::new, GENRATOR_T1).build();
    public static final BlockEntityType<CrafterBlockEntity> CRAFTER= FabricBlockEntityTypeBuilder.create(CrafterBlockEntity::new, MBlocks.CRAFTER).build();
    public static final BlockEntityType<DeployableLadderEntity> DEPLOYABLE_LADDER= FabricBlockEntityTypeBuilder.create(DeployableLadderEntity::new, MBlocks.DEPLOYABLE_LADDER).build();


}
