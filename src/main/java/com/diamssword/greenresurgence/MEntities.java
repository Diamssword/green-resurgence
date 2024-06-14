package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.entities.ChairEntity;
import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import com.diamssword.greenresurgence.items.CableItem;
import com.diamssword.greenresurgence.items.MultiblockLinkerItem;
import com.diamssword.greenresurgence.items.UniversalPlacerItem;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.registration.reflect.EntityRegistryContainer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class MEntities implements EntityRegistryContainer {
    public static final EntityType<Entity> chair = FabricEntityTypeBuilder.create(SpawnGroup.MISC, ChairEntity::new).dimensions(EntityDimensions.fixed(0.1f, 0.1f)).build();

}
