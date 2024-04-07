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
        teien.add("brown_stained_glass",true, GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE, GenericBlockSet.BlockTypes.OMNI_CARPET_SOLID).disableGen(false,GenericBlockSet.BlockTypes.OMNI_CARPET_SOLID);
        teien.add("glass", GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("blue_stained_glass", GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("purple_stained_glass", GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("white_stained_glass", GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("light_blue_stained_glass", GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("yellow_stained_glass", GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("iron_bars", GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.IRON_BARS);
        teien.add("nether_bricks", GenericBlockSet.BlockTypes.FENCE).disableGen(false,GenericBlockSet.BlockTypes.FENCE);
        teien.add("oak_door",true, GenericBlockSet.BlockTypes.DOOR);
        teien.add("birch_door", GenericBlockSet.BlockTypes.DOOR);
        teien.add("iron_door", GenericBlockSet.BlockTypes.DOOR);
        teien.add("spruce_door",true, GenericBlockSet.BlockTypes.DOOR);
        teien.add("crimson_door", GenericBlockSet.BlockTypes.DOOR);
        teien.add("shulker_gray", GenericBlockSet.BlockTypes.OMNI_SLAB);
        teien.add("shulker_green", GenericBlockSet.BlockTypes.OMNI_SLAB);
        teien.add("shulker_orange", GenericBlockSet.BlockTypes.PILLAR);
        teien.add("lily_pad",true, GenericBlockSet.BlockTypes.OMNI_CARPET);
        teien.add("lectern",false, GenericBlockSet.BlockTypes.LECTERN);//.disableGen(false,GenericBlockSet.BlockTypes.LECTERN);
        teien.add("rusty_beam",true, GenericBlockSet.BlockTypes.OMNI_CARPET,GenericBlockSet.BlockTypes.SIMPLE).disableGen(false,GenericBlockSet.BlockTypes.OMNI_CARPET);

        teien.add("purpur_block", GenericBlockSet.BlockTypes.SIMPLE).model(GenericBlockSet.ModelType.PILLAR);
        teien.add("purpur_pillar", GenericBlockSet.BlockTypes.OMNI_BLOCK).model(GenericBlockSet.ModelType.INVERSED_PILLAR);
        teien.add("purpur_block_1", GenericBlockSet.BlockTypes.SIMPLE).model(GenericBlockSet.ModelType.PILLAR);
        teien.add("crimson_stem", GenericBlockSet.BlockTypes.OMNI_BLOCK).model(GenericBlockSet.ModelType.INVERSED_PILLAR);
        teien.add("oak_trapdoor",true, GenericBlockSet.BlockTypes.TRAPDOOR);
        teien.add("dead_horn_coral_fan",true, GenericBlockSet.BlockTypes.OMNI_CARPET).disableGen(true);
        teien.add("composter", GenericBlockSet.BlockTypes.SIMPLE).model(GenericBlockSet.ModelType.COMPOSTER);
        teien.add("shulker_white",true, GenericBlockSet.BlockTypes.OMNI_BLOCK).disableGen(true);
        teien.add("blue_bed", GenericBlockSet.BlockTypes.BED);
        teien.add("bookshelf", GenericBlockSet.BlockTypes.FURNACE).model(GenericBlockSet.ModelType.TWO_TEXTURED_MACHINE);
        teien.add("chiseled_bookshelf", GenericBlockSet.BlockTypes.FURNACE).model(GenericBlockSet.ModelType.TWO_TEXTURED_MACHINE);
        sets.add(teien);
    }
    static GenericBlockSet diams= new GenericBlockSet("diams");
    static {
        diams.add("gray_concrete", GenericBlockSet.BlockTypes.OMNI_SLAB).disableGen(false);
        diams.add("gray_glazed_terracotta", GenericBlockSet.BlockTypes.ROTATABLE_SLAB).disableGen(false);
        diams.add("road_barrier",true, GenericBlockSet.BlockTypes.PILLAR).disableGen(false);
        sets.add(diams);
    }
    static GenericBlockSet last_days= new GenericBlockSet("last_days");
    static {
        last_days.add("chiseled_sandstone", GenericBlockSet.BlockTypes.SIMPLE);
        last_days.add("chest", GenericBlockSet.BlockTypes.SIMPLE).model(GenericBlockSet.ModelType.PILLAR);
        last_days.add("chest1", GenericBlockSet.BlockTypes.SIMPLE).model(GenericBlockSet.ModelType.PILLAR);
        last_days.add("glass", GenericBlockSet.BlockTypes.GLASS_BLOCK,GenericBlockSet.BlockTypes.GLASS_PANE);
        last_days.add("iron_block", GenericBlockSet.BlockTypes.SIMPLE);
        last_days.add("nether_gold_ore", GenericBlockSet.BlockTypes.FURNACE).model(GenericBlockSet.ModelType.TWO_TEXTURED_MACHINE);
        last_days.add("bookshelf", GenericBlockSet.BlockTypes.FURNACE).model(GenericBlockSet.ModelType.TWO_TEXTURED_MACHINE);
        last_days.add("redstone_ore", GenericBlockSet.BlockTypes.SIMPLE);
        last_days.add("purple_glazed_terracotta", GenericBlockSet.BlockTypes.SIMPLE);
        last_days.add("furnace", GenericBlockSet.BlockTypes.FURNACE).model(GenericBlockSet.ModelType.BOTOMLESS_MACHINE);
        last_days.add("smoker", GenericBlockSet.BlockTypes.FURNACE);
        last_days.add("lectern", GenericBlockSet.BlockTypes.LECTERN);
        last_days.add("barrel", GenericBlockSet.BlockTypes.FURNACE).model(GenericBlockSet.ModelType.TWO_TEXTURED_MACHINE);
        last_days.add("oak_door",true, GenericBlockSet.BlockTypes.DOOR);
        last_days.add("birch_door",true, GenericBlockSet.BlockTypes.DOOR);
        last_days.add("jungle_door", true,GenericBlockSet.BlockTypes.DOOR);
        last_days.add("acacia_door", true,GenericBlockSet.BlockTypes.DOOR);
        last_days.add("dark_oak_door", true,GenericBlockSet.BlockTypes.DOOR);
        last_days.add("lantern",true, GenericBlockSet.BlockTypes.LANTERN).disableGen(false);
        last_days.add("green_bed", GenericBlockSet.BlockTypes.BED);
        last_days.add("brown_bed", GenericBlockSet.BlockTypes.BED);
        last_days.add("oak_trapdoor", GenericBlockSet.BlockTypes.TRAPDOOR);
        sets.add(last_days);
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
