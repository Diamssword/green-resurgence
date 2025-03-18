package com.diamssword.greenresurgence.genericBlocks;

import com.diamssword.greenresurgence.GreenResurgence;
import com.google.common.collect.Lists;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import com.diamssword.greenresurgence.genericBlocks.GenericBlockSet.*;
import java.util.ArrayList;
import java.util.List;

public class GenericBlocks {
    private static final float CHAIR=-0.1f;
    private static final float CHAIR_SLAB=-0.35f;
    private static final float CHAIR_MEDIUM=-0.15f;
    public static List<GenericBlockSet> sets=new ArrayList<>();
    public static final OwoItemGroup GENERIC_GROUP=OwoItemGroup.builder(new Identifier(GreenResurgence.ID,"generic_group"),()->sets.isEmpty()?Icon.of(Items.STICK):Icon.of(sets.get(0).displayStack()))
            .initializer((group)->{
                for (GenericBlockSet set : sets) {
                    group.addTab(Icon.of(set.displayStack()),set.subdomain,null,false);
                }

            }).build();
    static GenericBlockSet teien= new GenericBlockSet("teien");
    static{
        teien.create("smoker").addSub(BlockType.PILLAR, ModelType.MACHINE);
        teien.create("pumpkin").addSub(BlockType.PILLAR, ModelType.PILLAR);
        teien.create("brown_stained_glass").addSub(BlockType.SIMPLE).addSub(BlockType.PANE).addSub(BlockType.OMNI_BLOCK, ModelType.CARPET, SubBlock.CARPET).disableGen(false, SubBlock.CARPET).setTransparency(Transparency.CUTOUT);
        //glass cutout
        teien.create("iron_bars","yellow_stained_glass","white_stained_glass","glass","cyan_stained_glass").addSub(BlockType.SIMPLE).addSub(BlockType.PANE).addSub(BlockType.OMNI_BLOCK, ModelType.CARPET, SubBlock.CARPET).setTransparency(Transparency.CUTOUT).sound(BlockSoundGroup.GLASS);

        //glass transparent
        teien.create("blue_stained_glass","purple_stained_glass","tinted_glass","magenta_stained_glass","light_blue_stained_glass","light_blue_stained_glass_alt","lime_stained_glass","orange_stained_glass","black_stained_glass").addSub(BlockType.SIMPLE).addSub(BlockType.PANE).addSub(BlockType.OMNI_BLOCK, ModelType.CARPET, SubBlock.CARPET).setTransparency(Transparency.TRANSPARENT).sound(BlockSoundGroup.GLASS);
        //wool
        teien.create(simpleList("","_wool", "brown","gray","red","cyan","green","yellow","lime","blue","light_gray")).addSub(BlockType.SIMPLE).addSub(BlockType.OMNI_BLOCK, ModelType.CARPET, SubBlock.CARPET).setTransparency(Transparency.OPAQUE).addGroup("wool").sound(BlockSoundGroup.WOOL);
        //door
        teien.create("birch_door","iron_door","crimson_door").addSub(BlockType.DOOR).setTransparency(Transparency.OPAQUE);
        teien.create("spruce_door","oak_door").addSub(BlockType.DOOR).setTransparency(Transparency.CUTOUT);
        //bed
        teien.create("blue_bed","cyan_bed","green_bed","light_gray_bed","yellow_bed").addSub(BlockType.BED).setTransparency(Transparency.UNDEFINED).addGroup("bed");
        //simple
        teien.create("yellow_concrete","emerald_block","quartz","quartz1","quartz2","stone_bricks","cracked_stone_bricks","smooth_stone","polished_andesite","birch_planks","iron_block","bricks").addSub(BlockType.SIMPLE);
        teien.create("copper_block","exposed_copper","weathered_copper","oxidized_copper").addSub(BlockType.SIMPLE, ModelType.PILLAR);
        //machine
        teien.create("chiseled_bookshelf","bookshelf").addSub(BlockType.PILLAR, ModelType.TWO_TEXTURED_MACHINE).setTransparency(Transparency.OPAQUE);
        teien.create("dispenser","dropper").addSub(BlockType.PILLAR, ModelType.BOTOMLESS_MACHINE).setTransparency(Transparency.OPAQUE);
        teien.create("redstone_block","barrel","chest").addSub(BlockType.PILLAR).setTransparency(Transparency.OPAQUE);
        teien.create("brewing_stand").addSub(BlockType.PILLAR, ModelType.SLAB, GenericBlockSet.HitBox.CENTER).disableGen(true).setTransparency(Transparency.CUTOUT);

        teien.add("nether_bricks", Transparency.UNDEFINED, BlockType.FENCE).disableGen(false);
        teien.create("shulker_gray","shulker_green").addSub(BlockType.OMNI_BLOCK, ModelType.SLAB_3TEX, SubBlock.SLAB);
        teien.create("shulker_orange").addSub(BlockType.PILLAR);
        teien.create("lily_pad","newspaper","letter_antique").addSub(BlockType.OMNI_BLOCK, ModelType.CARPET, SubBlock.CARPET).setTransparency(Transparency.CUTOUT).notSolid();
        teien.create("paper_torn","paper_torn_2").addSub(BlockType.OMNI_BLOCK, ModelType.CARPET, SubBlock.CARPET).setTransparency(Transparency.TRANSPARENT).notSolid();

        teien.add("lectern",Transparency.UNDEFINED, BlockType.LECTERN);//.disableGen(false,BlockType.LECTERN);
        teien.create("rusty_beam").addSub(BlockType.SIMPLE).addSub(BlockType.OMNI_BLOCK, ModelType.CARPET, SubBlock.CARPET).setTransparency(Transparency.CUTOUT).disableGen(false, SubBlock.CARPET).notSolid(SubBlock.CARPET);
        teien.create("purpur_block","purpur_block_1").addSub(BlockType.SIMPLE, ModelType.PILLAR);
        teien.create("purpur_pillar","crimson_stem").addSub(BlockType.OMNI_BLOCK, ModelType.INVERSED_PILLAR);
        teien.create("dead_horn_coral_fan").addSub(BlockType.OMNI_BLOCK, ModelType.CARPET, SubBlock.CARPET).disableGen(true).setTransparency(Transparency.CUTOUT).notSolid();
        teien.create("composter").addSub(BlockType.SIMPLE,ModelType.COMPOSTER);
        teien.create("shulker_white").addSub(BlockType.OMNI_BLOCK).disableGen(true).setTransparency(Transparency.CUTOUT);
        teien.create("ladder").addSub(BlockType.PILLAR, ModelType.LADDER, GenericBlockSet.HitBox.CARPET).setTransparency(Transparency.CUTOUT).disableGen(true).addTags(BlockTags.CLIMBABLE).sound(BlockSoundGroup.LADDER);
        teien.add("scaffolding", Transparency.CUTOUT, BlockType.SIMPLE).disableGen(true);
        teien.create("oak_trapdoor","iron_trapdoor","birch_trapdoor","spruce_trapdoor").addSub(BlockType.TRAPDOOR).setTransparency(Transparency.CUTOUT);
        sets.add(teien);
    }
    private static String[] simpleList(String pref,String suff,String... parts)
    {
        var res=new ArrayList<String>();
        for (String part : parts) {
            res.add(pref+part+suff);
        }
        return res.toArray(new String[0]);
    }
    private static String[] simpleList(String pref,String[] mid,String[] suffix)
    {
        var res=new ArrayList<String>();
        for(String p : mid)
        {
            if(suffix!=null) {
                for(String q : suffix) {

                        res.add(pref+p+q);
                }
            }
            else
                res.add(pref+p);
        }
        return res.toArray(new String[0]);
    }
    public static String[] append(String[] baseList, String... toAdd)
    {
        var b= Lists.newArrayList(baseList);
        b.addAll(Lists.newArrayList(toAdd));
        return b.toArray(new String[0]);

    }
    public static String[] allColors(String name)
    {
        String[] s=new String[16];
        for (DyeColor value : DyeColor.values()) {
            s[value.getId()]=name+value.getName();
        }
        return s;
    }
    static GenericBlockSet diams= new GenericBlockSet("diams");
    static {
        //simple block centered
        diams.create("side_road_post").addSub(BlockType.PILLAR,ModelType.SIMPLE,HitBox.CENTER).disableGen(true).setTransparency(Transparency.NOTFULL);
        diams.create("concrete_side_road_blocker").addSub(BlockType.SIMPLE,ModelType.SIMPLE,HitBox.MEDIUM).disableGen(true).setTransparency(Transparency.NOTFULL);
        diams.create("road_tire_spike_cross","road_tire_spike").addSub(BlockType.PILLAR,ModelType.PILLAR,HitBox.CARPET_FIXED).setTransparency(Transparency.CUTOUT).disableGen(true).setTransparency(Transparency.NOTFULL).setDamage(1f).notSolid();
        diams.create("gray_concrete").addSub(BlockType.OMNI_BLOCK,ModelType.SLAB,SubBlock.SLAB).disableGen(false);
        diams.create("gray_glazed_terracotta").addSub(BlockType.ROTATABLE_SLAB,ModelType.SLAB,HitBox.FIXED_SLAB,SubBlock.SLAB).disableGen(false);
        //diams.add("gray_glazed_terracotta", BlockType.ROTATABLE_SLAB).disableGen(false);
        diams.add("road_barrier",Transparency.NOTFULL, BlockType.PILLAR).disableGen(false);
        diams.create("carton_gros","concrete_block").addSub(BlockType.PILLAR).disableGen(true);

        //lamps
        diams.create("lamp_marine_big_plastic","lamp_marine_big_metal","lamp_marine_big_gold","lamp_marine_big_copper","lamp_marine_big_black","lamp_marine_plastic","lamp_marine_metal","lamp_marine_gold","lamp_marine_copper","lamp_marine_black","lamp_ceiling_light_copper")
                        .addSub(BlockType.OMNI_BLOCK,ModelType.CARPET,HitBox.CARPET).togglable().light(12).setTransparency(Transparency.CUTOUT).addGroup("lamps").disableGen(true);
        diams.create("lamp_desk_plastic","lamp_desk_metal","lamp_desk_gold","lamp_desk_copper","lamp_desk_black")
                .addSub(BlockType.PILLAR,ModelType.CARPET,HitBox.SMALL_BOTTOM).togglable().light(12).setTransparency(Transparency.CUTOUT).addGroup("lamps_desk").disableGen(true);
        diams.create("lamp_fairylights","lamp_globe_wall","lamp_neon_palmtree","lamp_neon_open","lamp_neon_text_big_white","lamp_neon_text_big_pink")
                .addSub(BlockType.OMNI_BLOCK,ModelType.CARPET,HitBox.CARPET).togglable().light(12).setTransparency(Transparency.TRANSPARENT).addGroup("neon_mur").disableGen(true);
        diams.create("lamp_neon_vertical_intact","lamp_neon_vertical_damaged","celling_lamp_light_bulb")
                .addSub(BlockType.PILLAR,ModelType.CARPET,HitBox.SMALL_TOP).togglable().light(12).setTransparency(Transparency.CUTOUT).addGroup("lamps").disableGen(true);
        diams.create("lamp_neon_vertical_empty","lamp_neon_vertical_broken")
                .addSub(BlockType.PILLAR,ModelType.CARPET,HitBox.SMALL_TOP).setTransparency(Transparency.CUTOUT).addGroup("lamps").disableGen(true);
        diams.create(allColors("lampshade_wool_")).addSub(BlockType.PILLAR,ModelType.CARPET,HitBox.MEDIUM).togglable().light(12).setTransparency(Transparency.CUTOUT).addGroup("lampshade").disableGen(true);
        diams.create("lamp_base_stick_spruce","lamp_base_stick_oak","lamp_base_spruce","lamp_base_oak").addSub(BlockType.PILLAR,ModelType.CARPET,HitBox.CENTER).setTransparency(Transparency.CUTOUT).addGroup("lampshade").disableGen(true);
        //cartons
        diams.create(simpleList("carton_petit","",new String[]{"","_travers","_ouvert","_entreouvert"})).addGroup("cartons_petit").addSub(BlockType.PILLAR,ModelType.PILLAR,HitBox.MEDIUM).setTransparency(Transparency.NOTFULL).disableGen(true);
        //register
        diams.create("register_black","circulation_plot").addSub(BlockType.PILLAR,ModelType.SLAB,HitBox.SMALL_BOTTOM).setTransparency(Transparency.NOTFULL).disableGen(true);
        diams.create("gaz_bottle","gaz_bottle_big","gaz_bottle_pile","fire_extinguisher").addSub(BlockType.PILLAR,ModelType.SLAB,HitBox.SMALL_BOTTOM).setTransparency(Transparency.CUTOUT).disableGen(true);
        //photo_frame
        diams.create(simpleList("photo_frame_",new String[]{"tree_","portrait_","zen_"},new String[]{"table","wall"})).addSub(BlockType.PILLAR,ModelType.SLAB,HitBox.SMALL_BOTTOM).addGroup("photo_frame").setTransparency(Transparency.NOTFULL).disableGen(true);
        //radiator
        diams.create("radiator_white_central", "radiator_white_left","radiator_white_right","radiator_white_solo").addSub(BlockType.PILLAR,ModelType.PILLAR,HitBox.SLAB).setTransparency(Transparency.NOTFULL).notSolid().addGroup("radiator").disableGen(true);
        //tire
        diams.create(simpleList("tire_car_","","brake_disc","flat_triple","full","jante","up","up_double","up_triple")).addSub(BlockType.PILLAR).setTransparency(Transparency.NOTFULL).addGroup("tire_car").disableGen(true);
        //toggleable
        diams.create("toilet_white","toilet_black").addSub(BlockType.PILLAR,ModelType.SLAB,HitBox.SLAB).togglable().setTransparency(Transparency.NOTFULL).disableGen(true);
        diams.create("trash_can_big").addSub(BlockType.PILLAR).togglable().setTransparency(Transparency.CUTOUT).disableGen(true);

        diams.create("big_plank","small_wood_table","plastic_fence","fridge_bottom","fridge_up","fridge_up_square","concrete_fence","sink_noplate_ceramic", "sink_pedestal_ceramic","air_vent","kitchen_dish_washer","kitchen_cloth_cleaning_machine")
                .addSub(BlockType.PILLAR).setTransparency(Transparency.NOTFULL).disableGen(true);
        //table nap
        diams.create(simpleList("table_square_nap_",new String[]{"blue_","green_","red_"},new String[]{"center","border","corner","end","middle","solo"})).addSub(BlockType.PILLAR).setTransparency(Transparency.CUTOUT).addGroup("table_square_nap").disableGen(true);
        //chair wool
        diams.create(allColors("chair_wool_")).addSub(BlockType.PILLAR).disableGen(true).seat(CHAIR_MEDIUM).setTransparency(Transparency.NOTFULL).addGroup("chair_wool");
        //chair sofa
        diams.create(simpleList("",allColors("chair_sofa_"),new String[]{"_left","_right","_middle","_solo"})).addSub(BlockType.PILLAR,ModelType.SLAB,HitBox.SLAB).disableGen(true).seat(CHAIR_SLAB).setTransparency(Transparency.CUTOUT).addGroup("chair_sofa");
        //microwave
        diams.create(allColors("microwave_")).addSub(BlockType.PILLAR,ModelType.SLAB,HitBox.MEDIUM).setTransparency(Transparency.NOTFULL).disableGen(true).addGroup("microwave");
        diams.create(simpleList("barrel_",new String[]{"toxic","rust","red","blue"},new String[]{"_up","_down"})).addSub(BlockType.PILLAR).setTransparency(Transparency.NOTFULL).disableGen(true).addGroup("barrels");
        //chair slab
        diams.create("tire_car_flat","stool_ottoman_resort","stool_waterhyacinth").addSub(BlockType.PILLAR,ModelType.SLAB,HitBox.SLAB).disableGen(true).seat(CHAIR_SLAB).setTransparency(Transparency.NOTFULL);
        //chair cutout
        diams.create("school_chair_red").addSub(BlockType.PILLAR).disableGen(true).seat(CHAIR_MEDIUM).setTransparency(Transparency.CUTOUT);
        //chair
        diams.create("tire_car_flat_double","chair_folding_oak","chair_folding_spruce","chair_gaming_black","chair_gaming_blue","chair_gaming_green","chair_metro_plastic_connector","chair_metro_plastic_no_connector")
                        .addSub(BlockType.PILLAR).seat(CHAIR).setTransparency(Transparency.NOTFULL).disableGen(true);
        //chair medium
        diams.create("chair_dining_waterhyacinth","chair_stool_vintage","chair_stool_military","chair_wood_oak","chair_wood_oak_b","chair_wood_spruce","chair_wood_spruce_b")
                .addSub(BlockType.PILLAR,ModelType.PILLAR,HitBox.MEDIUM).seat(CHAIR_MEDIUM).setTransparency(Transparency.NOTFULL).disableGen(true);
        //shower
        diams.create(simpleList("shower_","", "wall_head","round_wall_head","round_head_with_hand","round_head","round_ceiling_head","only_with_hand","mixer","head_with_hand","head","ceiling_head"))
                        .addSub(BlockType.PILLAR,ModelType.CARPET,HitBox.CARPET).notSolid().setTransparency(Transparency.CUTOUT).disableGen(true).addGroup("shower");
        diams.create("pendantlight_rattan_light","pendantlight_rattan_dark","trash_can_round_green","trash_can_round_iron","dino_meat_mascot","trash_can_fast_food_red","trash_can_fast_food_green","trash_can_fast_food_gray","trash_can_fast_food_black","trash_can_square_exterior_wood","trash_can_square_exterior_green","restaurant_display_light","restaurant_display_dark").addSub(BlockType.PILLAR).setTransparency(Transparency.CUTOUT).disableGen(true);
        diams.create(simpleList("luggage_","",new String[]{"antique_small_brown","antique_small_white","basket","basket_stand","picnic"})).addSub(BlockType.PILLAR,ModelType.SLAB,HitBox.SLAB).setTransparency(Transparency.NOTFULL).disableGen(true);
        diams.create(simpleList("bathtub_",new String[]{"right_","left_"},new String[]{"empty","water"})).addSub(BlockType.PILLAR,ModelType.SLAB).setTransparency(Transparency.CUTOUT).seat(CHAIR_SLAB).disableGen(true);
        //baricade
        diams.create(simpleList("plank_barricade_",new String[]{"simple_","heavy_","diaguonal_","cross_up_","cross_down_"},new String[]{"oak","spruce"})).addSub(BlockType.OMNI_BLOCK,ModelType.CARPET,HitBox.CARPET).setTransparency(Transparency.NOTFULL).disableGen(true);
        //TV
        diams.create("tv_old_gray","tv_old_gray_broken","tv_old_black","tv_old_black_broken","tv_realy_old_spruce","tv_realy_old_spruce_broken","tv_realy_old_oak","tv_realy_old_oak_broken").addSub(BlockType.PILLAR).setTransparency(Transparency.NOTFULL).disableGen(true).addGroup("tv");
        diams.create("computer_1","computer_2").addSub(BlockType.PILLAR).setTransparency(Transparency.CUTOUT).disableGen(true).addGroup("computer");
        diams.create("computer_portable").addSub(BlockType.PILLAR,ModelType.SLAB,HitBox.FIXED_SLAB).togglable().setTransparency(Transparency.NOTFULL).disableGen(true).addGroup("computer");
        diams.create(allColors("computer_big_")).addSub(BlockType.PILLAR).setTransparency(Transparency.NOTFULL).disableGen(true).addGroup("computer");

        diams.create("ladder_tall_foldable").addSub(BlockType.PILLAR).setTransparency(Transparency.NOTFULL).disableGen(true);

        //carpet
        diams.create("shelf_wall").addSub(BlockType.OMNI_BLOCK,ModelType.CARPET,HitBox.CARPET).disableGen(true);

        //distributor
        diams.create(simpleList("distributor",new String[]{"_orange_","_green_"},new String[]{"up_left","up_right","middle_left","middle_right","down_right","down_left"})).addSub(BlockType.PILLAR).disableGen(true).addGroup("distributor").setTransparency(Transparency.NOTFULL);

        //bucket
        diams.create("bucket_metal_water","bucket_metal_empty").addSub(BlockType.PILLAR,ModelType.SIMPLE,HitBox.MEDIUM).setTransparency(Transparency.NOTFULL).disableGen(true).addGroup("bucket");

        //electrical
        diams.create("electrical_power_switch","electrical_power_house","electrical_power_distributor").addSub(BlockType.PILLAR,ModelType.SLAB,HitBox.CARPET).setTransparency(Transparency.NOTFULL).disableGen(true).addGroup("electrical");
        //umbrela
        diams.create(allColors("umbrella_market_")).addSub(BlockType.PILLAR,ModelType.SIMPLE,HitBox.CENTER).setTransparency(Transparency.CUTOUT).disableGen(true).addGroup("umbrella");
        diams.create("umbrella_stand").addSub(BlockType.PILLAR,ModelType.SIMPLE,HitBox.CARPET_FIXED).setTransparency(Transparency.NOTFULL).disableGen(true).addGroup("umbrella");
        //shopping cart
        diams.create("shopping_cart").addSub(BlockType.CONNECTED_PILLAR,ModelType.PILLAR).setTransparency(Transparency.CUTOUT).disableGen(true);
        //livres
        diams.create(simpleList("book_horizontal_","", "a","b","c","d","e")).addSub(BlockType.PILLAR,ModelType.PILLAR,HitBox.CARPET_FIXED).setTransparency(Transparency.CUTOUT).addGroup("book_horizontal").disableGen(true);

        //ladders
        diams.create("ladder_metal_up","ladder_metal").addSub(BlockType.PILLAR, ModelType.LADDER, GenericBlockSet.HitBox.CARPET).setTransparency(Transparency.CUTOUT).addTags(BlockTags.CLIMBABLE).sound(BlockSoundGroup.LADDER).disableGen(true);

        //fences
        diams.create("barrier_plastic").addSub(BlockType.CONNECTED_PILLAR).setTransparency(Transparency.NOTFULL).disableGen(true);
        diams.create("barrier_plastic_angle").addSub(BlockType.PILLAR).setTransparency(Transparency.NOTFULL).disableGen(true);
        //car
        //genericFurniture(diams, BlockType.PILLAR, Transparency.CUTOUT,"car_low_front_right","car_low_front_left","car_low_back_right","car_low_back_left");
        //genericFurniture(diams, BlockType.TOGGLEABLE, Transparency.CUTOUT,"car_low_trunk","car_low_door","car_low_capot");
        //genericFurniture(diams, BlockType.CHAIR_SLAB, Transparency.CUTOUT,"car_low_chair");
        sets.add(diams);
    }

    static GenericBlockSet last_days= new GenericBlockSet("last_days");
    static {
        //simple
        last_days.create("chiseled_sandstone","redstone_ore","purple_glazed_terracotta","quartz_pillar").addSub(BlockType.SIMPLE);
        last_days.create("glass").addSub(BlockType.SIMPLE).addSub(BlockType.PANE).addSub(BlockType.OMNI_BLOCK, ModelType.CARPET, SubBlock.CARPET).setTransparency(Transparency.CUTOUT);
        last_days.create("iron_block").addSub(BlockType.SIMPLE).addSub(BlockType.PANE);
        last_days.create("furnace").addSub(BlockType.PILLAR, ModelType.BOTOMLESS_MACHINE);
        last_days.create("lectern").addSub(BlockType.LECTERN);
        last_days.create("lantern").addSub(BlockType.LANTERN).disableGen(false).setTransparency(Transparency.CUTOUT);
        last_days.create("quartz_block").addSub(BlockType.SIMPLE).addSub(BlockType.OMNI_BLOCK,ModelType.SLAB,SubBlock.SLAB).addSub(BlockType.STAIRS).variant(16);
        last_days.create("chiseled_quartz_block").addSub(BlockType.SIMPLE).addSub(BlockType.OMNI_BLOCK,ModelType.SLAB,SubBlock.SLAB).addSub(BlockType.STAIRS).variant(33);
        last_days.create("smooth_quartz").addSub(BlockType.SIMPLE).addSub(BlockType.OMNI_BLOCK,ModelType.SLAB,SubBlock.SLAB).addSub(BlockType.STAIRS);
        last_days.create("ladder").addSub(BlockType.PILLAR, ModelType.LADDER, GenericBlockSet.HitBox.CARPET).setTransparency(Transparency.CUTOUT).addTags(BlockTags.CLIMBABLE).sound(BlockSoundGroup.LADDER);
        last_days.create("oak_planks").addSub(BlockType.SIMPLE).addSub(BlockType.OMNI_BLOCK,ModelType.SLAB,SubBlock.SLAB).addSub(BlockType.STAIRS).addSub(BlockType.FENCE,ModelType.WALL).addSub(BlockType.OMNI_BLOCK,ModelType.CARPET,SubBlock.CARPET).variant(10);
        last_days.create("oak_log").addSub(BlockType.SIMPLE,ModelType.PILLAR).addSub(BlockType.OMNI_BLOCK,ModelType.SLAB,SubBlock.SLAB).addSub(BlockType.STAIRS,ModelType.SIMPLE).addSub(BlockType.FENCE,ModelType.WALL).addSub(BlockType.OMNI_BLOCK,ModelType.CARPET,SubBlock.CARPET);
        //BED
        last_days.create(append(simpleList("","_bed",allColors("")),"brown_old_bed")).addSub(BlockType.BED).addGroup("bed").setTransparency(Transparency.CUTOUT);
        //doors
        last_days.create("oak_door","birch_door","jungle_door","acacia_door","dark_oak_door","crimson_door","iron_door").addSub(BlockType.DOOR).setTransparency(Transparency.CUTOUT);
        //trapdoor
        last_days.create("oak_trapdoor","spruce_trapdoor","dark_oak_trapdoor").addSub(BlockType.TRAPDOOR).setTransparency(Transparency.CUTOUT);
        //wools
        last_days.create(simpleList("","_wool",allColors(""))).addSub(BlockType.SIMPLE).addSub(BlockType.STAIRS).addSub(BlockType.OMNI_BLOCK,ModelType.CARPET,SubBlock.CARPET).setTransparency(Transparency.OPAQUE).addGroup("wool");
        //Simple pillar
        last_days.create("chest","chest1","coal_block","lapis_block","red_glazed_terracotta","bone_block").addSub(BlockType.SIMPLE,ModelType.PILLAR).setTransparency(Transparency.OPAQUE);
        //pillar simple
        last_days.create("black_glazed_terracotta").addSub(BlockType.PILLAR,ModelType.SIMPLE).setTransparency(Transparency.OPAQUE);
        //pillar machine
        last_days.create("barrel","gold_block","bookshelf","nether_gold_ore","cyan_glazed_terracotta","lime_glazed_terracotta","pink_glazed_terracotta").addSub(BlockType.PILLAR,ModelType.TWO_TEXTURED_MACHINE).setTransparency(Transparency.OPAQUE);
        //pillar machine1
        last_days.create("smoker","smithing_table").addSub(BlockType.PILLAR,ModelType.MACHINE).setTransparency(Transparency.OPAQUE);
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
