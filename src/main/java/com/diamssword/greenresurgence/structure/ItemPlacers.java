package com.diamssword.greenresurgence.structure;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.items.JigsawPlacerItem;
import com.diamssword.greenresurgence.items.StructurePlacerItem;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemPlacers {

    private static final Map<String,Item> toRegister=new HashMap<>();
    public static final List<MultiblockInstance> multiblocksStructure=new ArrayList<>();
    public static MultiblockInstance rusted_car=multiblock("rusted_car",false);
    public static StructurePlacerItem electric_post=structure("electric_post",true);
    public static JigsawPlacerItem road_droit=jigsaw("road_droit",new Identifier("build","roads/droit"));
    public static JigsawPlacerItem road_inter_d=jigsaw("road_inter_d",new Identifier("build","roads/intersection_d"));
    public static JigsawPlacerItem road_inter_g=jigsaw("road_inter_g",new Identifier("build","roads/intersection_g"));
    public static JigsawPlacerItem road_carrfour=jigsaw("road_carrfour",new Identifier("build","roads/carrfour"));

    public static JigsawPlacerItem road_angle_d=jigsaw("road_angle_d",new Identifier("build","roads/angle_d"));
    public static JigsawPlacerItem road_angle_g=jigsaw("road_angle_g",new Identifier("build","roads/angle_g"));
    public static JigsawPlacerItem container=jigsaw("container",new Identifier("build","deco/containers/small"));
    public static JigsawPlacerItem small_house=jigsaw("small_house",new Identifier("build","small_house"));
    public static JigsawPlacerItem tree=jigsaw("tree",new Identifier("build","props/tree/basic"));
     public static JigsawPlacerItem jigsaw(String name,Identifier pool)
    {
        JigsawPlacerItem item= new JigsawPlacerItem(new OwoItemSettings().group(MItems.GROUP).tab(1),pool);
        toRegister.put(name,item);
        return item;
    }
    public static StructurePlacerItem structure(String name,boolean centered)
    {
        return structure(name,centered,new Identifier(GreenResurgence.ID+":placer/"+name));
    }
    public static StructurePlacerItem structure(String name,boolean centered,Identifier structure)
    {
        StructurePlacerItem item= new StructurePlacerItem(new OwoItemSettings().group(MItems.GROUP).tab(1),structure,centered);
        toRegister.put(name,item);
        return item;
    }
    public static MultiblockInstance multiblock(String name, boolean centered)
    {
        return multiblock(name,centered,new Identifier(GreenResurgence.ID+":placer/"+name));
    }
    public static MultiblockInstance multiblock(String name, boolean centered, Identifier structure)
    {
        MultiblockInstance item= new MultiblockInstance(name,structure,centered);
        multiblocksStructure.add(item);
        return item;
    }

    public static void init()
    {
        toRegister.forEach((name,ob)->{
            Registry.register(Registries.ITEM,new Identifier(GreenResurgence.ID,name+"_placer"),ob);
        });
        multiblocksStructure.forEach(i->{
            Registry.register(Registries.ITEM,new Identifier(GreenResurgence.ID,i.name+"_placer"),i.placer);
            Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, i.name), new BlockItem(i.block, new OwoItemSettings().group(MItems.GROUP)));
            Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, i.name), i.block);
        });

    }
}
