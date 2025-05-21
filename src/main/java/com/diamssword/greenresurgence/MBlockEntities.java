package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.blockEntities.ModBlockEntity;
import io.wispforest.owo.registration.reflect.BlockEntityRegistryContainer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MBlockEntities implements BlockEntityRegistryContainer {

    //public static final BlockEntityType<LootedBlockEntity> LOOTED_BLOCk= FabricBlockEntityTypeBuilder.create(LootedBlockEntity::new, LOOTED_BLOCK).build();
    //public static final BlockEntityType<ConnectorBlockEntity> CONNECTOR_BLOCK= FabricBlockEntityTypeBuilder.create(ConnectorBlockEntity::new, MBlocks.CONNECTOR).build();
    //public static final BlockEntityType<ItemBlockEntity> ITEM_BLOCK= FabricBlockEntityTypeBuilder.create(ItemBlockEntity::new, MBlocks.ITEM_BLOCK).build();
    //public static final BlockEntityType<LootableItemBlockEntity> LOOT_ITEM_BLOCK= FabricBlockEntityTypeBuilder.create(LootableItemBlockEntity::new, MBlocks.LOOT_ITEM_BLOCK).build();
    //public static final BlockEntityType<LootableShelfEntity> LOOTABLE_SHELF= FabricBlockEntityTypeBuilder.create(LootableShelfEntity::new, MBlocks.SHELF_BLOCK, SIDEWAY_SHELF_BLOCK,WOOD_CRATE_SHELF_BLOCK).build();
    //public static final BlockEntityType<ImageBlockEntity> IMAGE_BLOCK= FabricBlockEntityTypeBuilder.create(ImageBlockEntity::new, MBlocks.IMAGE_BLOCK).build();
    //public static final BlockEntityType<GenericStorageBlockEntity> GENERIC_STORAGE= FabricBlockEntityTypeBuilder.create(GenericStorageBlockEntity::new, BASE_CRATE_T1,BASE_CRATE_T2).build();
    //public static final BlockEntityType<GeneratorBlockEntity> GENERATOR= FabricBlockEntityTypeBuilder.create(GeneratorBlockEntity::new, GENRATOR_T1).build();
    //public static final BlockEntityType<CrafterBlockEntity> CRAFTER= FabricBlockEntityTypeBuilder.create(CrafterBlockEntity::new, MBlocks.CRAFTER).build();
    //public static final BlockEntityType<DeployableLadderEntity> DEPLOYABLE_LADDER= FabricBlockEntityTypeBuilder.create(DeployableLadderEntity::new, MBlocks.DEPLOYABLE_LADDER).build();
    public static final Map<Class<? extends BlockEntity>, List<ModBlockEntity<?>>> toRegisterBlocks=new HashMap<>();
    public static void addToRegister(ModBlockEntity<?> be)
    {
        var clazz=be.getBlockEntityClass();
        if(!toRegisterBlocks.containsKey(clazz))
            toRegisterBlocks.put(clazz,new ArrayList<>());
        toRegisterBlocks.get(clazz).add(be);
    }
    public static void registerAll()
    {
        toRegisterBlocks.forEach((k,v)->{

            var blocks=v.toArray(new ModBlockEntity<?>[0]);
            var id=Registries.BLOCK.getId(blocks[0]);
            var type=blocks[0].registerEntityType(blocks);
            for (var b:blocks) {
                b.registerFromExternalType(type);
            }

            var id1=blocks[0].getCustomBlockEntityName();
            if(id1 ==null)
                id1=new Identifier(id.getNamespace(), "be_"+id.getPath());

            Registry.register(Registries.BLOCK_ENTITY_TYPE,id1,type);
        });
    }
}