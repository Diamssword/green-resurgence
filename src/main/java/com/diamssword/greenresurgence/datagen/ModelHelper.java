package com.diamssword.greenresurgence.datagen;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.genericBlocks.GenericBlockSet;
import net.minecraft.block.Block;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.SlabType;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Optional;

public class ModelHelper {

    public final String subdomain;

    public ModelHelper(String subdomain) {
        this.subdomain = subdomain;
    }
    public TexturedModel.Factory getModeleFactoryFor(GenericBlockSet.ModelType type,String name)
    {
        switch (type)
        {

            case SIMPLE -> {
                return TexturedModel.makeFactory(b1 -> TextureMap.all(this.getBlockModelId(name)),new Model(Optional.of(new Identifier("minecraft", "block/cube_all")), Optional.empty(), TextureKey.ALL));
            }
            case PILLAR -> {
                return TexturedModel.makeFactory(b1 -> this.textureMapPillar(name),new Model(Optional.of(new Identifier("minecraft", "block/cube_column_horizontal")), Optional.empty(), TextureKey.END, TextureKey.SIDE));
            }
            case COMPOSTER -> {
                return TexturedModel.makeFactory(b1 -> this.textureMapComposter(name),new Model(Optional.of(new Identifier( "block/composter")), Optional.empty(), TextureKey.PARTICLE, TextureKey.SIDE,TextureKey.TOP,TextureKey.BOTTOM,TextureKey.INSIDE));
            }

            case INVERSED_PILLAR -> {
                return TexturedModel.makeFactory(b1 -> this.textureMapPillar(name),new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/cube_column_vertical")), Optional.empty(), TextureKey.END, TextureKey.SIDE));
            }
            case MACHINE -> {
                return TexturedModel.makeFactory(b1 -> this.textureMapMachine(name,false,false),new Model(Optional.of(new Identifier("minecraft", "block/orientable_with_bottom")), Optional.empty(), TextureKey.TOP, TextureKey.SIDE, TextureKey.FRONT, TextureKey.BOTTOM));
            }
            case BOTOMLESS_MACHINE -> {
                return TexturedModel.makeFactory(b1 -> this.textureMapMachine(name,true,false),new Model(Optional.of(new Identifier("minecraft", "block/orientable")), Optional.empty(), TextureKey.TOP, TextureKey.SIDE, TextureKey.FRONT));
            }
            case TWO_TEXTURED_MACHINE -> {
                return TexturedModel.makeFactory(b1 -> this.textureMapMachine(name,true,true),new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/simple_orientable")), Optional.empty(), TextureKey.SIDE, TextureKey.FRONT));
            }
        }
        return TexturedModel.makeFactory(b1 -> TextureMap.all(this.getBlockModelId(name)),new Model(Optional.of(new Identifier("minecraft", "block/cube_all")), Optional.empty(), TextureKey.ALL));
    }

    public TextureMap textureMapPillar(String name)
    {
        TextureMap map = new TextureMap();
        map.put(TextureKey.SIDE,getBlockModelId(name).withSuffixedPath("_side"));
        map.put(TextureKey.END,getBlockModelId(name).withSuffixedPath("_top"));
        return map;
    }
    public TextureMap textureMapComposter(String name)
    {
        TextureMap map = new TextureMap();
        map.put(TextureKey.SIDE,getBlockModelId(name).withSuffixedPath("_side"));
        map.put(TextureKey.TOP,getBlockModelId(name).withSuffixedPath("_top"));
        map.put(TextureKey.PARTICLE,getBlockModelId(name).withSuffixedPath("_side"));
        map.put(TextureKey.BOTTOM,getBlockModelId(name).withSuffixedPath("_bottom"));
        map.put(TextureKey.INSIDE,getBlockModelId(name).withSuffixedPath("_bottom"));
        return map;
    }
    public TextureMap textureMapMachine(String name,boolean noBottom,boolean noTop)
    {
        TextureMap map = new TextureMap();
        map.put(TextureKey.SIDE,getBlockModelId(name).withSuffixedPath("_side"));
        if(!noTop)
            map.put(TextureKey.TOP,getBlockModelId(name).withSuffixedPath("_top"));
        map.put(TextureKey.FRONT,getBlockModelId(name).withSuffixedPath("_front"));
        if(!noBottom)
            map.put(TextureKey.BOTTOM,getBlockModelId(name).withSuffixedPath("_bottom"));
        return map;
    }
    public TextureMap textureOmniSlab(String name)
    {
        TextureMap map = new TextureMap();
        map.put(TextureKey.SIDE,getBlockModelId(name).withSuffixedPath("_side"));
        map.put(TextureKey.FRONT,getBlockModelId(name).withSuffixedPath("_front"));
        map.put(TextureKey.BACK,getBlockModelId(name).withSuffixedPath("_back"));
        return map;
    }
    public void registerTrapdoor(BlockStateModelGenerator generator,String name,Block trapdoorBlock) {
        TextureMap textureMap = TextureMap.texture(getBlockModelId(name));
        Identifier identifier = Models.TEMPLATE_TRAPDOOR_TOP.upload(getBlockModelId(name).withSuffixedPath("_top"), textureMap, generator.modelCollector);
        Identifier identifier2 = Models.TEMPLATE_TRAPDOOR_BOTTOM.upload(getBlockModelId(name), textureMap, generator.modelCollector);
        Identifier identifier3 = Models.TEMPLATE_TRAPDOOR_OPEN.upload(getBlockModelId(name).withSuffixedPath("_open"), textureMap, generator.modelCollector);
        generator.blockStateCollector.accept(BlockStateModelGenerator.createTrapdoorBlockState(trapdoorBlock, identifier, identifier2, identifier3));
   //     generator.registerParentedItemModel(trapdoorBlock, identifier2);
    }
    public final void registerLantern(BlockStateModelGenerator generator, String name, Block lantern) {

        TextureMap textureMap = new TextureMap().put(TextureKey.LANTERN, getBlockModelId(name));
        TextureMap textureMap_off = new TextureMap().put(TextureKey.LANTERN, getBlockModelId(name).withSuffixedPath("_off"));
        Identifier identifier1= new Model(Optional.of(new Identifier("minecraft", "block/template_lantern")), Optional.empty(), TextureKey.LANTERN).upload(getBlockModelId(name), textureMap, generator.modelCollector);
        Identifier identifier2=new Model(Optional.of(new Identifier("minecraft", "block/template_hanging_lantern")), Optional.empty(), TextureKey.LANTERN).upload(getBlockModelId(name).withSuffixedPath("_top"), textureMap, generator.modelCollector);
        Identifier identifier3=new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/side_lantern")), Optional.empty(), TextureKey.LANTERN).upload(getBlockModelId(name).withSuffixedPath("_side"), textureMap, generator.modelCollector);

        Identifier identifier4=new Model(Optional.of(new Identifier("minecraft", "block/template_lantern")), Optional.empty(), TextureKey.LANTERN).upload(getBlockModelId(name).withSuffixedPath("_off"), textureMap_off, generator.modelCollector);
        Identifier identifier5=new Model(Optional.of(new Identifier("minecraft", "block/template_hanging_lantern")), Optional.empty(), TextureKey.LANTERN).upload(getBlockModelId(name).withSuffixedPath("_top_off"), textureMap_off, generator.modelCollector);
        Identifier identifier6=new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/side_lantern")), Optional.empty(), TextureKey.LANTERN).upload(getBlockModelId(name).withSuffixedPath("_side_off"), textureMap_off, generator.modelCollector);

        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(lantern).coordinate(fillLanternVariantMap(BlockStateVariantMap.create(Properties.FACING, Properties.LIT), identifier1, identifier2, identifier3, identifier4,identifier5,identifier6)));

    }
    public final void registerBed(BlockStateModelGenerator generator, String name, Block lantern) {

        TextureKey key=TextureKey.of("bed");
        TextureMap textureMap = new TextureMap().put(key, getBlockModelId(name));

        Identifier identifier1=new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/bed")), Optional.empty(), key).upload(getBlockModelId(name), textureMap, generator.modelCollector);
        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(lantern).coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
                .register(Direction.SOUTH,BlockStateVariant.create().put(VariantSettings.MODEL,identifier1))
                .register(Direction.WEST,BlockStateVariant.create().put(VariantSettings.MODEL,identifier1).put(VariantSettings.Y,VariantSettings.Rotation.R90))
                .register(Direction.EAST,BlockStateVariant.create().put(VariantSettings.MODEL,identifier1).put(VariantSettings.Y,VariantSettings.Rotation.R270))
                .register(Direction.NORTH,BlockStateVariant.create().put(VariantSettings.MODEL,identifier1).put(VariantSettings.Y,VariantSettings.Rotation.R180))
        ));

    }
    public final void registerGlassPane(BlockStateModelGenerator generator, String name, Block glassPane, boolean isIron) {

        TextureMap textureMap = new TextureMap().put(TextureKey.PANE, getBlockModelId(name)).put(TextureKey.EDGE,  isIron? getBlockModelId(name):getBlockModelId(name).withSuffixedPath("_pane_top"));
        Identifier identifier = Models.TEMPLATE_GLASS_PANE_POST.upload(getBlockModelId(name).withSuffixedPath("_post"), textureMap, generator.modelCollector);
        Identifier identifier2 = Models.TEMPLATE_GLASS_PANE_SIDE.upload(getBlockModelId(name).withSuffixedPath("_side"), textureMap, generator.modelCollector);
        Identifier identifier3 = Models.TEMPLATE_GLASS_PANE_SIDE_ALT.upload(getBlockModelId(name).withSuffixedPath("_side_alt"), textureMap, generator.modelCollector);
        Identifier identifier4 = Models.TEMPLATE_GLASS_PANE_NOSIDE.upload(getBlockModelId(name).withSuffixedPath("_noside"), textureMap, generator.modelCollector);
        Identifier identifier5 = Models.TEMPLATE_GLASS_PANE_NOSIDE_ALT.upload(getBlockModelId(name).withSuffixedPath("_noside_alt"), textureMap, generator.modelCollector);
        // Models.GENERATED.upload(getBlockModelId(name).withSuffixedPath("_pane"), TextureMap.layer0(glassPane), generator.modelCollector);
        generator.blockStateCollector.accept(MultipartBlockStateSupplier.create(glassPane).with(BlockStateVariant.create().put(VariantSettings.MODEL, identifier)).with((When)When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier2)).with((When)When.create().set(Properties.EAST, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R90)).with((When)When.create().set(Properties.SOUTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier3)).with((When)When.create().set(Properties.WEST, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R90)).with((When)When.create().set(Properties.NORTH, false), BlockStateVariant.create().put(VariantSettings.MODEL, identifier4)).with((When)When.create().set(Properties.EAST, false), BlockStateVariant.create().put(VariantSettings.MODEL, identifier5)).with((When)When.create().set(Properties.SOUTH, false), BlockStateVariant.create().put(VariantSettings.MODEL, identifier5).put(VariantSettings.Y, VariantSettings.Rotation.R90)).with((When)When.create().set(Properties.WEST, false), BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.Y, VariantSettings.Rotation.R270)));
    }
    public final void registerFence(BlockStateModelGenerator generator,String name, Block fence) {

        TextureMap textureMap = new TextureMap().put(TextureKey.TEXTURE, getBlockModelId(name));
        Identifier identifier = Models.FENCE_POST.upload(getBlockModelId(name).withSuffixedPath("_post"), textureMap, generator.modelCollector);
        Identifier identifier2 = Models.FENCE_SIDE.upload(getBlockModelId(name).withSuffixedPath("_side"), textureMap, generator.modelCollector);
        generator.blockStateCollector.accept(MultipartBlockStateSupplier.create(fence)
                .with(BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
                .with((When)When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.UVLOCK,true))
                .with((When)When.create().set(Properties.EAST, true),BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.UVLOCK,true).put(VariantSettings.Y,VariantSettings.Rotation.R90))
                .with((When)When.create().set(Properties.SOUTH, true),BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.UVLOCK,true).put(VariantSettings.Y,VariantSettings.Rotation.R180))
                .with((When)When.create().set(Properties.WEST, true),BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.UVLOCK,true).put(VariantSettings.Y,VariantSettings.Rotation.R270)));

    }
    public void registerDoor(BlockStateModelGenerator generator,String name,Block doorBlock) {
        TextureMap textureMap = TextureMap.texture(getBlockModelId(name));
        Identifier identifier=new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/door_left")), Optional.empty(), TextureKey.PARTICLE).upload(getBlockModelId(name).withSuffixedPath("_left"),textureMap,generator.modelCollector);
        Identifier identifier2=new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/door_left_open")), Optional.empty(), TextureKey.PARTICLE).upload(getBlockModelId(name).withSuffixedPath("_left_open"),textureMap,generator.modelCollector);
        Identifier identifier3=new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/door_right")), Optional.empty(), TextureKey.PARTICLE).upload(getBlockModelId(name).withSuffixedPath("_right"),textureMap,generator.modelCollector);
        Identifier identifier4=new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/door_right_open")), Optional.empty(), TextureKey.PARTICLE).upload(getBlockModelId(name).withSuffixedPath("_right_open"),textureMap,generator.modelCollector);
        generator.blockStateCollector.accept(createDoorBlockState(doorBlock, identifier, identifier2, identifier3, identifier4));
    }
    public static BlockStateSupplier createDoorBlockState(Block doorBlock, Identifier LeftHingeClosedModelId, Identifier LeftHingeOpenModelId, Identifier RightHingeClosedModelId, Identifier RightHingeOpenModelId) {
        return VariantsBlockStateSupplier.create(doorBlock).coordinate(fillDoorVariantMap(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.DOOR_HINGE, Properties.OPEN), LeftHingeClosedModelId, LeftHingeOpenModelId, RightHingeClosedModelId, RightHingeOpenModelId));
    }
    public static BlockStateVariantMap.TripleProperty<Direction, DoorHinge, Boolean> fillDoorVariantMap(BlockStateVariantMap.TripleProperty<Direction, DoorHinge, Boolean> variantMap, Identifier leftHingeClosedModelId, Identifier leftHingeOpenModelId, Identifier rightHingeClosedModelId, Identifier rightHingeOpenModelId) {
        return variantMap
                .register(Direction.WEST, DoorHinge.LEFT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeClosedModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180))
                .register(Direction.WEST, DoorHinge.LEFT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeOpenModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180))
                .register(Direction.NORTH, DoorHinge.LEFT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeClosedModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270))
                .register(Direction.NORTH, DoorHinge.LEFT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeOpenModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270))
                .register(Direction.EAST, DoorHinge.RIGHT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeClosedModelId))
                .register(Direction.EAST, DoorHinge.RIGHT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeOpenModelId))
                .register(Direction.SOUTH, DoorHinge.RIGHT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeClosedModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                .register(Direction.SOUTH, DoorHinge.RIGHT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeOpenModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                .register(Direction.WEST, DoorHinge.RIGHT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeClosedModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180))
                .register(Direction.WEST, DoorHinge.RIGHT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeOpenModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180))
                .register(Direction.NORTH, DoorHinge.RIGHT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeClosedModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270))
                .register(Direction.NORTH, DoorHinge.RIGHT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeOpenModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270))
                .register(Direction.EAST, DoorHinge.LEFT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeClosedModelId))
                .register(Direction.EAST, DoorHinge.LEFT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeOpenModelId))
                .register(Direction.SOUTH, DoorHinge.LEFT, (Boolean)false, BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeClosedModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                .register(Direction.SOUTH, DoorHinge.LEFT, (Boolean)true, BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeOpenModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90));
    }
    public static BlockStateVariantMap.DoubleProperty<Direction, Boolean> fillConnectedPillarVariantMap(BlockStateVariantMap.DoubleProperty<Direction, Boolean> variantMap, Identifier down, Identifier up) {
        return variantMap
                .register(Direction.WEST,true, BlockStateVariant.create().put(VariantSettings.MODEL, up).put(VariantSettings.Y, VariantSettings.Rotation.R270))
                .register(Direction.NORTH, true, BlockStateVariant.create().put(VariantSettings.MODEL, up).put(VariantSettings.Y, VariantSettings.Rotation.R0))
                .register(Direction.SOUTH,true, BlockStateVariant.create().put(VariantSettings.MODEL, up).put(VariantSettings.Y, VariantSettings.Rotation.R180))
                .register(Direction.EAST, true, BlockStateVariant.create().put(VariantSettings.MODEL, up).put(VariantSettings.Y, VariantSettings.Rotation.R90))

                .register(Direction.WEST,false, BlockStateVariant.create().put(VariantSettings.MODEL, down).put(VariantSettings.Y, VariantSettings.Rotation.R270))
                .register(Direction.NORTH, false, BlockStateVariant.create().put(VariantSettings.MODEL, down).put(VariantSettings.Y, VariantSettings.Rotation.R0))
                .register(Direction.SOUTH,false, BlockStateVariant.create().put(VariantSettings.MODEL, down).put(VariantSettings.Y, VariantSettings.Rotation.R180))
                .register(Direction.EAST, false, BlockStateVariant.create().put(VariantSettings.MODEL, down).put(VariantSettings.Y, VariantSettings.Rotation.R90));
    }
    public static BlockStateVariantMap.DoubleProperty<Direction, Boolean> fillLanternVariantMap(BlockStateVariantMap.DoubleProperty<Direction, Boolean> variantMap, Identifier down, Identifier up, Identifier side, Identifier down_off,Identifier up_off,Identifier side_off) {
        return variantMap
                .register(Direction.UP, true, BlockStateVariant.create().put(VariantSettings.MODEL, up))
                .register(Direction.DOWN, true, BlockStateVariant.create().put(VariantSettings.MODEL, down))
                .register(Direction.WEST,true, BlockStateVariant.create().put(VariantSettings.MODEL, side).put(VariantSettings.Y, VariantSettings.Rotation.R270))
                .register(Direction.NORTH, true, BlockStateVariant.create().put(VariantSettings.MODEL, side).put(VariantSettings.Y, VariantSettings.Rotation.R0))
                .register(Direction.SOUTH,true, BlockStateVariant.create().put(VariantSettings.MODEL, side).put(VariantSettings.Y, VariantSettings.Rotation.R180))
                .register(Direction.EAST, true, BlockStateVariant.create().put(VariantSettings.MODEL, side).put(VariantSettings.Y, VariantSettings.Rotation.R90))

                .register(Direction.UP, false, BlockStateVariant.create().put(VariantSettings.MODEL, up_off))
                .register(Direction.DOWN, false, BlockStateVariant.create().put(VariantSettings.MODEL, down_off))
                .register(Direction.WEST,false, BlockStateVariant.create().put(VariantSettings.MODEL, side_off).put(VariantSettings.Y, VariantSettings.Rotation.R270))
                .register(Direction.NORTH, false, BlockStateVariant.create().put(VariantSettings.MODEL, side_off).put(VariantSettings.Y, VariantSettings.Rotation.R0))
                .register(Direction.SOUTH,false, BlockStateVariant.create().put(VariantSettings.MODEL, side_off).put(VariantSettings.Y, VariantSettings.Rotation.R180))
                .register(Direction.EAST, false, BlockStateVariant.create().put(VariantSettings.MODEL, side_off).put(VariantSettings.Y, VariantSettings.Rotation.R90));
    }
    public Identifier getBlockModelId(String baseID) {
        return new Identifier(GreenResurgence.ID,"block/"+this.subdomain+"/"+baseID);

    }
}
