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
    public static final OwoItemGroup GROUP=OwoItemGroup.builder(new Identifier(GreenResurgence.ID,"item_group"),()->Icon.of(new ItemStack(Registries.ITEM.get(new Identifier(GreenResurgence.ID,"container_placer")))))
            .initializer((group)->{

        //group.addTab(Icon.of(net.minecraft.item.Items.STICK),"tab",null,true);
    }).build();

    public static final Item WRENCH = new Item(new OwoItemSettings().group(GROUP));
 //   public static final Item WRENCH_1 = new Item(new OwoItemSettings().group(GROUP));
    public static final Item SLEDGEHAMMER = new Item(new OwoItemSettings().group(GROUP));
    public static final Item WIRE_SPOOL = new CableItem(new OwoItemSettings().group(GROUP));
    public static final StructurePlacerInstance[] STRUCTURES_PLACERS=new StructurePlacerInstance[]{
       //     new StructurePlacerInstance("rusted_car",null,false,false,true),
            new StructurePlacerInstance("electric_post",null,true,false,false),
            new StructurePlacerInstance("road_droit",new Identifier("build","roads/droit"),false,true,false),
            new StructurePlacerInstance("road_inter_d",new Identifier("build","roads/intersection_d"),false,true,false),
            new StructurePlacerInstance("road_inter_g",new Identifier("build","roads/intersection_g"),false,true,false),
            new StructurePlacerInstance("road_carrfour",new Identifier("build","roads/carrfour"),false,true,false),
            new StructurePlacerInstance("road_angle_d",new Identifier("build","roads/angle_d"),false,true,false),
            new StructurePlacerInstance("road_angle_g",new Identifier("build","roads/angle_g"),false,true,false),
            new StructurePlacerInstance("container",new Identifier("build","deco/containers/small/base"),false,true,false),
            new StructurePlacerInstance("small_house",new Identifier("build","small_house"),false,true,false),
    };
    @Override
    public void afterFieldProcessing() {
        for (StructurePlacerInstance structuresPlacer : STRUCTURES_PLACERS) {
            Registry.register(Registries.ITEM,new Identifier(GreenResurgence.ID,structuresPlacer.name+"_placer"),structuresPlacer.placer);
            if(structuresPlacer.block!=null) {
                Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, structuresPlacer.name), new BlockItem(structuresPlacer.block, new OwoItemSettings().group(GROUP)));
                Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, structuresPlacer.name), structuresPlacer.block);
            }
        }
    }
}
