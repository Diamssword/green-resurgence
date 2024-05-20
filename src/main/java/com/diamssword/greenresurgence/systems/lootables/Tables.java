package com.diamssword.greenresurgence.systems.lootables;

import com.diamssword.greenresurgence.materials.Materials;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.util.Identifier;

public class Tables {

    public static final Identifier FURNACE = TableHelper.create("furnace",TableHelper.simple(1)
            .add(Materials.adesive.get("screw"),1,7,20 )
            .add(Materials.metal.get("scrap"),2,7,50 )
            .add(Materials.metal.get("copperwire"),1,2,10 )
            .addAir(30).build());
    public static final Identifier FURNACE_INV = TableHelper.create("furnace_inv",TableHelper.simple(1,9,false).add(Materials.adesive.get("screw"),1,5,20,false )
            .add(Materials.components.get("case"),1,1,10,false )
            .add(Materials.components.get("diode"),1,2,10,false )
            .add(Materials.metal.get("copperwire"),1,2,20,false )
            .addAir(60).build());
    public static final Identifier FURNITURE = TableHelper.create("furniture",TableHelper.simple(1)
            .add(Materials.adesive.get("screw"),4,12,20 )
            .add(Materials.wood.get("furniture"),3,6,40 )
            .add(Materials.paper.get("newspaper"),1,3,10 )
            .add(Materials.metal.get("scrap"),1,2,10 )
            .addAir(20).build());
}
