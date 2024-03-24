package com.diamssword.greenresurgence.genericBlocks;

import com.diamssword.greenresurgence.GreenResurgence;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class GenericBlocks {
    public static List<GenericBlockSet> sets=new ArrayList<>();
    public static final OwoItemGroup GENERIC_GROUP=OwoItemGroup.builder(new Identifier(GreenResurgence.ID,"generic_group"),()->sets.isEmpty()?Icon.of(Items.STICK):Icon.of(sets.get(0).displayStack()))
            .initializer((group)->{
                for (GenericBlockSet set : sets) {
                    group.addTab(Icon.of(set.displayStack()),set.subdomain,null,false);
                }

            }).build();
    static GenericBlockSet teien= new GenericBlockSet("teien");
    static{
        teien.add("smoker", GenericBlockSet.BlockTypes.FURNACE);
        teien.add("pumpkin", GenericBlockSet.BlockTypes.PILLAR);
        teien.add("tinted_glass", GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("magenta_stained_glass", GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("cyan_stained_glass", GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("brown_stained_glass",true, GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE, GenericBlockSet.BlockTypes.OMNI_CARPET_SOLID).disableGen(GenericBlockSet.BlockTypes.OMNI_CARPET_SOLID);
        teien.add("glass", GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("blue_stained_glass", GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("purple_stained_glass", GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("white_stained_glass", GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("light_blue_stained_glass", GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("iron_bars", GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.IRON_BARS);
        teien.add("nether_bricks", GenericBlockSet.BlockTypes.FENCE).disableGen(GenericBlockSet.BlockTypes.FENCE);
        teien.add("oak_door",true, GenericBlockSet.BlockTypes.DOOR);
        teien.add("birch_door", GenericBlockSet.BlockTypes.DOOR);
        teien.add("iron_door", GenericBlockSet.BlockTypes.DOOR);
        teien.add("shulker_gray", GenericBlockSet.BlockTypes.OMNI_SLAB);
        teien.add("shulker_green", GenericBlockSet.BlockTypes.OMNI_SLAB);
        teien.add("shulker_orange", GenericBlockSet.BlockTypes.PILLAR);
        teien.add("lily_pad",true, GenericBlockSet.BlockTypes.OMNI_CARPET);
        teien.add("lectern",false, GenericBlockSet.BlockTypes.LECTERN).disableGen(GenericBlockSet.BlockTypes.LECTERN);
        teien.add("rusty_beam",true, GenericBlockSet.BlockTypes.OMNI_CARPET,GenericBlockSet.BlockTypes.SIMPLE).disableGen(GenericBlockSet.BlockTypes.OMNI_CARPET);

        sets.add(teien);
    }
    static GenericBlockSet diams= new GenericBlockSet("diams");
    static {
        diams.add("gray_concrete", GenericBlockSet.BlockTypes.OMNI_SLAB).disableGen(GenericBlockSet.BlockTypes.OMNI_SLAB);
        diams.add("gray_glazed_terracotta", GenericBlockSet.BlockTypes.ROTATABLE_SLAB).disableGen(GenericBlockSet.BlockTypes.ROTATABLE_SLAB);
        sets.add(diams);
    }
    public static void register()
    {
        int tab=0;
        for (GenericBlockSet set : sets) {
            set.setTabIndex(tab);
            tab++;
        }
        for (GenericBlockSet set : sets) {
            set.register();
        }


    }
}
