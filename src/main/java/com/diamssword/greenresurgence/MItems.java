package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.entities.TwoPassengerVehicle;
import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import com.diamssword.greenresurgence.items.*;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class MItems implements ItemRegistryContainer {
    public static final OwoItemGroup GROUP=OwoItemGroup.builder(new Identifier(GreenResurgence.ID,"item_group"),()->Icon.of(new ItemStack(Registries.ITEM.get(new Identifier(GreenResurgence.ID,"tools/iron_pipe_axe")))))
            .initializer((group)->{
        group.addTab(Icon.of(new ItemStack(Registries.ITEM.get(new Identifier(GreenResurgence.ID,"item_block")))),"base",null,true);
        group.addTab(Icon.of(new ItemStack(Registries.ITEM.get(new Identifier(GreenResurgence.ID,"tools/iron_pipe_axe")))),"weapons",null,false);
        group.addTab(Icon.of(new ItemStack(Registries.ITEM.get(new Identifier(GreenResurgence.ID,"container_placer")))),"placer",null,false);
        group.addTab(Icon.of(GreenResurgence.asRessource("textures/item/materials/wood_furniture.png"),0,0,16,16),"materials",null,false);
    }).build();
    public static final Item WIRE_SPOOL = new CableItem(withDiamsTab());
    public static final Item RAIL = new Item(new OwoItemSettings().group(GROUP));
    public static final UniversalPlacerItem UNIVERSAL_PLACER= new UniversalPlacerItem(new Item.Settings().maxCount(1));
    public static final Item REMOVABLE_LADDER=new DeployableLadder(new OwoItemSettings().group(GROUP));
    public static final Item CLAIM_PLACER=new ClaimBlockPlacerItem(new OwoItemSettings().group(GROUP));


    public static final Item MODULAR_HEAD = new ModularArmorItem(ArmorMaterials.CHAIN, ArmorItem.Type.HELMET,new OwoItemSettings().group(GROUP));
    public static final Item MODULAR_CHEST = new ModularArmorItem(ArmorMaterials.CHAIN, ArmorItem.Type.CHESTPLATE,new OwoItemSettings().group(GROUP));
    public static final Item MODULAR_LEG = new ModularArmorItem(ArmorMaterials.CHAIN, ArmorItem.Type.LEGGINGS,new OwoItemSettings().group(GROUP));
    public static final Item MODULAR_BOOT = new ModularArmorItem(ArmorMaterials.CHAIN, ArmorItem.Type.BOOTS,new OwoItemSettings().group(GROUP));
    public static final Item BACKPACK = new BackPackItem(new OwoItemSettings().group(GROUP),5,2);
    public static final Item BACKPACK1 = new BackPackItem(new OwoItemSettings().group(GROUP),3,3);
    public static final Item BACKPACK2 = new BackPackItem(new OwoItemSettings().group(GROUP),7,5);
    public static final Item SATCHEL = new SatchelItem(new OwoItemSettings().group(GROUP),1,2);
    public static final Item SATCHELA = new SatchelItem(new OwoItemSettings().group(GROUP),1,3);
    public static final Item SATCHELB = new SatchelItem(new OwoItemSettings().group(GROUP),2,3);
    public static final EntityPlacerItem CADDIE = new EntityPlacerItem((u,p)->new TwoPassengerVehicle(MEntities.CADDIE,u.getWorld(),p.x,p.y,p.z),new OwoItemSettings().maxCount(1).group(GROUP));
    @Override
    public void afterFieldProcessing() {
    }
    public static <T extends Block> OwoItemSettings withDiamsTab()
    {

        return new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(1);
    }
}
