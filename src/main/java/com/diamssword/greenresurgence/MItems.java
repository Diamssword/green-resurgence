package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import com.diamssword.greenresurgence.items.CableItem;
import com.diamssword.greenresurgence.items.MultiblockLinkerItem;
import com.diamssword.greenresurgence.items.UniversalPlacerItem;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
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
    public static final Item WIRE_SPOOL = new CableItem(withDiamsTab());
    public static final Item MULTIBLOCK_LINKER = new MultiblockLinkerItem(new OwoItemSettings().group(GROUP));
    public static final UniversalPlacerItem UNIVERSAL_PLACER= new UniversalPlacerItem(new Item.Settings().maxCount(1));

    @Override
    public void afterFieldProcessing() {
    }
    public static <T extends Block> OwoItemSettings withDiamsTab()
    {
        return new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(1);
    }
}
