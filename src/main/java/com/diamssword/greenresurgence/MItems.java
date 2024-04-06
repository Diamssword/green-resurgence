package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.items.CableItem;
import com.diamssword.greenresurgence.structure.StructurePlacerInstance;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class MItems implements ItemRegistryContainer {
    public static final OwoItemGroup GROUP=OwoItemGroup.builder(new Identifier(GreenResurgence.ID,"item_group"),()->Icon.of(new ItemStack(Registries.ITEM.get(new Identifier(GreenResurgence.ID,"wrench")))))
            .initializer((group)->{
        group.addTab(Icon.of(new ItemStack(Registries.ITEM.get(new Identifier(GreenResurgence.ID,"wrench")))),"base",null,true);
        group.addTab(Icon.of(new ItemStack(Registries.ITEM.get(new Identifier(GreenResurgence.ID,"container_placer")))),"placer",null,false);
    }).build();
    public static final Item WRENCH = new Item(new OwoItemSettings().group(GROUP));
 //   public static final Item WRENCH_1 = new Item(new OwoItemSettings().group(GROUP));
    public static final Item SLEDGEHAMMER = new Item(new OwoItemSettings().group(GROUP));
    public static final Item WIRE_SPOOL = new CableItem(new OwoItemSettings().group(GROUP));

    @Override
    public void afterFieldProcessing() {

    }
}
