package com.diamssword.greenresurgence.genericBlocks;

import com.diamssword.greenresurgence.GreenResurgence;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

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
        teien.add("smoker", GenericBlockSet.BlockTypes.PILLAR).model(GenericBlockSet.ModelType.MACHINE);
        teien.add("pumpkin", GenericBlockSet.BlockTypes.PILLAR);
        teien.add("tinted_glass", GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("magenta_stained_glass", GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("cyan_stained_glass",GenericBlockSet.Transparency.CUTOUT, GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("brown_stained_glass", GenericBlockSet.Transparency.CUTOUT, GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE, GenericBlockSet.BlockTypes.OMNI_CARPET_SOLID).disableGen(false,GenericBlockSet.BlockTypes.OMNI_CARPET_SOLID);
        teien.add("glass", GenericBlockSet.Transparency.CUTOUT, GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("blue_stained_glass", GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("purple_stained_glass", GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("white_stained_glass",GenericBlockSet.Transparency.CUTOUT, GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("light_blue_stained_glass", GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("yellow_stained_glass",GenericBlockSet.Transparency.CUTOUT, GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.GLASS_PANE);
        teien.add("iron_bars",GenericBlockSet.Transparency.CUTOUT, GenericBlockSet.BlockTypes.GLASS_BLOCK, GenericBlockSet.BlockTypes.IRON_BARS);
        teien.add("nether_bricks", GenericBlockSet.BlockTypes.FENCE).disableGen(false,GenericBlockSet.BlockTypes.FENCE);
        teien.add("oak_door", GenericBlockSet.Transparency.CUTOUT, GenericBlockSet.BlockTypes.DOOR);
        teien.add("birch_door", GenericBlockSet.BlockTypes.DOOR);
        teien.add("iron_door", GenericBlockSet.BlockTypes.DOOR);
        teien.add("spruce_door",GenericBlockSet.Transparency.CUTOUT, GenericBlockSet.BlockTypes.DOOR);
        teien.add("crimson_door", GenericBlockSet.BlockTypes.DOOR);
        teien.add("shulker_gray", GenericBlockSet.BlockTypes.OMNI_SLAB);
        teien.add("shulker_green", GenericBlockSet.BlockTypes.OMNI_SLAB);
        teien.add("shulker_orange", GenericBlockSet.BlockTypes.PILLAR);
        teien.add("lily_pad",GenericBlockSet.Transparency.CUTOUT, GenericBlockSet.BlockTypes.OMNI_CARPET);
        teien.add("newspaper",GenericBlockSet.Transparency.CUTOUT, GenericBlockSet.BlockTypes.OMNI_CARPET);
        teien.add("letter_antique",GenericBlockSet.Transparency.CUTOUT, GenericBlockSet.BlockTypes.OMNI_CARPET);
        teien.add("paper_torn",GenericBlockSet.Transparency.TRANSPARENT, GenericBlockSet.BlockTypes.OMNI_CARPET);
        teien.add("paper_torn_2",GenericBlockSet.Transparency.TRANSPARENT, GenericBlockSet.BlockTypes.OMNI_CARPET);
        teien.add("lectern", GenericBlockSet.BlockTypes.LECTERN);//.disableGen(false,GenericBlockSet.BlockTypes.LECTERN);
        teien.add("rusty_beam",GenericBlockSet.Transparency.CUTOUT, GenericBlockSet.BlockTypes.OMNI_CARPET,GenericBlockSet.BlockTypes.SIMPLE).disableGen(false,GenericBlockSet.BlockTypes.OMNI_CARPET);
        teien.add("purpur_block", GenericBlockSet.BlockTypes.SIMPLE).model(GenericBlockSet.ModelType.PILLAR);
        teien.add("purpur_pillar", GenericBlockSet.BlockTypes.OMNI_BLOCK).model(GenericBlockSet.ModelType.INVERSED_PILLAR);
        teien.add("purpur_block_1", GenericBlockSet.BlockTypes.SIMPLE).model(GenericBlockSet.ModelType.PILLAR);
        teien.add("crimson_stem", GenericBlockSet.BlockTypes.OMNI_BLOCK).model(GenericBlockSet.ModelType.INVERSED_PILLAR);
        teien.add("oak_trapdoor",GenericBlockSet.Transparency.CUTOUT, GenericBlockSet.BlockTypes.TRAPDOOR);
        teien.add("dead_horn_coral_fan",GenericBlockSet.Transparency.CUTOUT, GenericBlockSet.BlockTypes.OMNI_CARPET).disableGen(true);
        teien.add("composter", GenericBlockSet.BlockTypes.SIMPLE).model(GenericBlockSet.ModelType.COMPOSTER);
        teien.add("shulker_white",GenericBlockSet.Transparency.CUTOUT, GenericBlockSet.BlockTypes.OMNI_BLOCK).disableGen(true);
        teien.add("blue_bed", GenericBlockSet.BlockTypes.BED);
        teien.add("bookshelf", GenericBlockSet.BlockTypes.PILLAR).model(GenericBlockSet.ModelType.TWO_TEXTURED_MACHINE);
        teien.add("chiseled_bookshelf", GenericBlockSet.BlockTypes.PILLAR).model(GenericBlockSet.ModelType.TWO_TEXTURED_MACHINE);
        teien.add("ladder", GenericBlockSet.Transparency.CUTOUT, GenericBlockSet.BlockTypes.LADDER).disableGen(true);
        sets.add(teien);
    }
    private static void genericFurniture(GenericBlockSet set,GenericBlockSet.BlockTypes type, GenericBlockSet.Transparency render, String... names)
    {
        for (String name : names) {
            set.add(name,render, type).disableGen(true);
        }
    }
    private static void genericFurnitureWithVariant(GenericBlockSet set,GenericBlockSet.BlockTypes type, GenericBlockSet.Transparency render,String variant, String... names)
    {
        for (String name : names) {
            set.add(name,render, type).disableGen(true).variantItem(variant);
        }
    }
    private static void genericChair(GenericBlockSet set, GenericBlockSet.Transparency render, String... names)
    {
        for (String name : names) {
            set.add(name,render, GenericBlockSet.BlockTypes.CHAIR).disableGen(true);
        }
    }
    private static void genericFurniture(GenericBlockSet set, GenericBlockSet.BlockTypes type, GenericBlockSet.Transparency render,String base,String[] p1,@Nullable String[] p2,@Nullable  String[] p3)
    {
        List<String> res=new ArrayList<>();
        for(String p : p1)
        {
            if(p2!=null) {
                for(String q : p2) {
                    if(p3!=null) {
                        for(String s : p3) {
                            res.add(base+p+q+s);
                        }
                    }
                    else
                        res.add(base+p+q);
                }
            }
            else
                res.add(base+p);
        }
        for (String name : res) {
            set.add(name,render, type).disableGen(true);
        }
    }
    private static void genericFurnitureWithVariant(GenericBlockSet set, GenericBlockSet.BlockTypes type, GenericBlockSet.Transparency render,String variantName,String base,String[] p1,@Nullable String[] p2,@Nullable  String[] p3)
    {
        List<String> res=new ArrayList<>();
        for(String p : p1)
        {
            if(p2!=null) {
                for(String q : p2) {
                    if(p3!=null) {
                        for(String s : p3) {
                            res.add(base+p+q+s);
                        }
                    }
                    else
                        res.add(base+p+q);
                }
            }
            else
                res.add(base+p);
        }
        for (String name : res) {
            set.add(name,render, type).disableGen(true).variantItem(variantName);
        }
    }
    private static void genericChairSlab(GenericBlockSet set, GenericBlockSet.Transparency render, String... names)
    {
        for (String name : names) {
            set.add(name,render, GenericBlockSet.BlockTypes.CHAIR_SLAB).disableGen(true);
        }
    }
    private static String[] allColors(String name)
    {
        String[] s=new String[16];
        for (DyeColor value : DyeColor.values()) {
            s[value.getId()]=name+value.getName();
        }
        return s;
    }
    static GenericBlockSet diams= new GenericBlockSet("diams");
    static {
        diams.add("gray_concrete", GenericBlockSet.BlockTypes.OMNI_SLAB).disableGen(false);
        diams.add("gray_glazed_terracotta", GenericBlockSet.BlockTypes.ROTATABLE_SLAB).disableGen(false);
        diams.add("road_barrier",GenericBlockSet.Transparency.NOTFULL, GenericBlockSet.BlockTypes.PILLAR).disableGen(false);
        diams.add("carton_gros", GenericBlockSet.BlockTypes.PILLAR).disableGen(true);
        diams.add("concrete_block", GenericBlockSet.BlockTypes.PILLAR).disableGen(true);
        //cartons
        genericFurnitureWithVariant(diams, GenericBlockSet.BlockTypes.PILLAR, GenericBlockSet.Transparency.NOTFULL,"cartons_petit","carton_petit",new String[]{"","_travers","_ouvert","_entreouvert"},null,null);
        //register
        genericFurniture(diams, GenericBlockSet.BlockTypes.PILLAR_SLAB, GenericBlockSet.Transparency.NOTFULL,"register_black");
        //photo_frame
        genericFurnitureWithVariant(diams, GenericBlockSet.BlockTypes.PILLAR_SLAB, GenericBlockSet.Transparency.NOTFULL,"photo_frame","photo_frame_",new String[]{"tree_","portrait_","zen_"},new String[]{"table","wall"},null);
        //radiator
        genericFurnitureWithVariant(diams, GenericBlockSet.BlockTypes.PILLAR, GenericBlockSet.Transparency.NOTFULL,"radiator","radiator_white_central", "radiator_white_left","radiator_white_right","radiator_white_solo");
        //tire
        genericFurnitureWithVariant(diams, GenericBlockSet.BlockTypes.PILLAR, GenericBlockSet.Transparency.NOTFULL,"tire_car","tire_car_",new String[]{"brake_disc","flat_triple","full","jante","up","up_double","up_triple"},null,null);
        //toggleable
        genericFurniture(diams, GenericBlockSet.BlockTypes.TOGGLEABLE, GenericBlockSet.Transparency.NOTFULL,"toilet_white","toilet_black");
        genericFurniture(diams, GenericBlockSet.BlockTypes.TOGGLEABLE, GenericBlockSet.Transparency.CUTOUT,"trash_can_big");
        genericFurniture(diams, GenericBlockSet.BlockTypes.PILLAR, GenericBlockSet.Transparency.NOTFULL,"big_plank","small_wood_table",
        "plastic_fence","fridge_bottom","fridge_up","fridge_up_square","concrete_fence","sink_noplate_ceramic", "sink_pedestal_ceramic");
        //table nap
        genericFurnitureWithVariant(diams, GenericBlockSet.BlockTypes.PILLAR, GenericBlockSet.Transparency.CUTOUT,"table_square_nap","table_square_nap_",new String[]{"blue_","green_","red_"},new String[]{"center","border","corner","end","middle","solo"},null);
        //chair wool
        genericFurnitureWithVariant(diams, GenericBlockSet.BlockTypes.CHAIR, GenericBlockSet.Transparency.NOTFULL,"chair_wool",allColors("chair_wool_"));
        //chair sofa
        genericFurnitureWithVariant(diams, GenericBlockSet.BlockTypes.CHAIR_SLAB, GenericBlockSet.Transparency.CUTOUT,"chair_sofa","",allColors("chair_sofa_"),new String[]{"_left","_right","_middle","_solo"},null);
        //microwave
        genericFurnitureWithVariant(diams, GenericBlockSet.BlockTypes.PILLAR_SLAB,GenericBlockSet.Transparency.NOTFULL,"microwave",allColors("microwave_"));
        genericFurnitureWithVariant(diams, GenericBlockSet.BlockTypes.PILLAR, GenericBlockSet.Transparency.NOTFULL,"barrels","barrel_",new String[]{"toxic","rust","red","blue"},new String[]{"_up","_down"},null);
        //chair slab
        genericChairSlab(diams, GenericBlockSet.Transparency.NOTFULL,"tire_car_flat","stool_ottoman_resort","stool_waterhyacinth");
        //chair cutout
        genericChair(diams, GenericBlockSet.Transparency.CUTOUT,"school_chair_red");
        //chair
        genericChair(diams, GenericBlockSet.Transparency.NOTFULL,"tire_car_flat_double","chair_stool_military","chair_stool_vintage","chair_dining_waterhyacinth","chair_folding_oak","chair_folding_spruce","chair_gaming_black",
        "chair_gaming_blue","chair_gaming_green","chair_wood_oak","chair_wood_oak_b","chair_wood_spruce","chair_wood_spruce_b","chair_metro_plastic_connector","chair_metro_plastic_no_connector");

        genericFurniture(diams, GenericBlockSet.BlockTypes.PILLAR,GenericBlockSet.Transparency.CUTOUT,"pendantlight_rattan_light","pendantlight_rattan_dark","trash_can_round_green","trash_can_round_iron");

        genericFurniture(diams, GenericBlockSet.BlockTypes.PILLAR_SLAB, GenericBlockSet.Transparency.NOTFULL,"luggage_",new String[]{"antique_small_brown","antique_small_white","basket","basket_stand","picnic"},null,null);

        sets.add(diams);
    }

    static GenericBlockSet last_days= new GenericBlockSet("last_days");
    static {
        last_days.add("chiseled_sandstone", GenericBlockSet.BlockTypes.SIMPLE);
        last_days.add("chest", GenericBlockSet.BlockTypes.SIMPLE).model(GenericBlockSet.ModelType.PILLAR);
        last_days.add("chest1", GenericBlockSet.BlockTypes.SIMPLE).model(GenericBlockSet.ModelType.PILLAR);
        last_days.add("glass",GenericBlockSet.Transparency.CUTOUT, GenericBlockSet.BlockTypes.GLASS_BLOCK,GenericBlockSet.BlockTypes.GLASS_PANE, GenericBlockSet.BlockTypes.OMNI_CARPET);
        last_days.add("iron_block", GenericBlockSet.BlockTypes.SIMPLE);
        last_days.add("nether_gold_ore", GenericBlockSet.BlockTypes.PILLAR).model(GenericBlockSet.ModelType.TWO_TEXTURED_MACHINE);
        last_days.add("bookshelf", GenericBlockSet.BlockTypes.PILLAR).model(GenericBlockSet.ModelType.TWO_TEXTURED_MACHINE);
        last_days.add("redstone_ore", GenericBlockSet.BlockTypes.SIMPLE);
        last_days.add("purple_glazed_terracotta", GenericBlockSet.BlockTypes.SIMPLE);
        last_days.add("furnace", GenericBlockSet.BlockTypes.PILLAR).model(GenericBlockSet.ModelType.BOTOMLESS_MACHINE);
        last_days.add("smoker", GenericBlockSet.BlockTypes.PILLAR).model(GenericBlockSet.ModelType.MACHINE);
        last_days.add("lectern", GenericBlockSet.BlockTypes.LECTERN);
        last_days.add("barrel", GenericBlockSet.BlockTypes.PILLAR).model(GenericBlockSet.ModelType.TWO_TEXTURED_MACHINE);
        last_days.add("oak_door",GenericBlockSet.Transparency.CUTOUT, GenericBlockSet.BlockTypes.DOOR);
        last_days.add("birch_door",GenericBlockSet.Transparency.CUTOUT, GenericBlockSet.BlockTypes.DOOR);
        last_days.add("jungle_door", GenericBlockSet.Transparency.CUTOUT,GenericBlockSet.BlockTypes.DOOR);
        last_days.add("acacia_door", GenericBlockSet.Transparency.CUTOUT,GenericBlockSet.BlockTypes.DOOR);
        last_days.add("dark_oak_door", GenericBlockSet.Transparency.CUTOUT,GenericBlockSet.BlockTypes.DOOR);
        last_days.add("lantern", GenericBlockSet.Transparency.CUTOUT,GenericBlockSet.BlockTypes.LANTERN).disableGen(false);
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
