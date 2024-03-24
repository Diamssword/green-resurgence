package com.diamssword.greenresurgence.genericBlocks;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.datagen.LangGenerator;
import com.diamssword.greenresurgence.datagen.ModelHelper;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.FabricTagBuilder;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.data.client.*;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class GenericBlockSet {
    public final String subdomain;
    private final ModelHelper helper;
    private List<GenericBlockInstance> blocks=new ArrayList<>();
    private List<Block> glassRenderNeeded =new ArrayList<>();
    private List<GeneratedBlockInstance> generatedBlocks =new ArrayList<>();
    private List<GeneratedItemInstance> generatedItems =new ArrayList<>();
    private List<GenExceptionInstance> genExceptions =new ArrayList<>();
    private int tabIndex;
    public GenericBlockSet(String subdomain) {
        this.subdomain = subdomain;
        helper=new ModelHelper(this.subdomain);
    }
    public void setTabIndex(int ind)
    {
        this.tabIndex=ind;
    }
    public ItemStack displayStack()
    {
        return generatedBlocks.isEmpty() ?new ItemStack(Items.STICK):new ItemStack(generatedBlocks.get(0).block);
    }
    public GenericBlockRegisterInstance add(String name,BlockTypes... blocks)
    {
        this.blocks.add(new GenericBlockInstance(name,false,blocks));
        return new GenericBlockRegisterInstance(this,name);
    }
    public GenericBlockRegisterInstance add(String name,boolean forceTransluscent,BlockTypes... blocks)
    {
        this.blocks.add(new GenericBlockInstance(name,forceTransluscent,blocks));
        return new GenericBlockRegisterInstance(this,name);
    }
    private boolean canGenerate(String name,BlockTypes type)
    {
        return genExceptions.stream().filter(b->b.name.equals(name) && b.type==type).findFirst().isEmpty();
    }
    public void register()
    {
        this.blocks.forEach(entry->{
            for (BlockTypes block : entry.blocks) {
                switch (block)
                {
                    case SIMPLE -> {
                        Block b=new Block(processSettings(entry.forceTransparent,AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL)));
                        Item i;
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name,b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name,i,block));
                        if(entry.forceTransparent)
                            glassRenderNeeded.add(b);
                    }
                    case FURNACE,PILLAR -> {
                        Block b=new GlazedTerracottaBlock(processSettings(entry.forceTransparent,AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL)));
                        Item i;
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name,b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name,i,block));
                    }
                    case GLASS_BLOCK -> {
                        Block b=new GlassBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(Blocks::never).solidBlock(Blocks::never).suffocates(Blocks::never).blockVision(Blocks::never));
                        Item i;
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        glassRenderNeeded.add(b);
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name,b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name,i,block));
                    }
                    case GLASS_PANE,IRON_BARS -> {
                        Item i;
                        Block b=new PaneBlock(AbstractBlock.Settings.create().sounds(block==BlockTypes.IRON_BARS?BlockSoundGroup.METAL: BlockSoundGroup.GLASS).nonOpaque());
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name+"_pane"), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name+"_pane"), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        glassRenderNeeded.add(b);
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name+"_pane",b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name+"_pane",i,block));
                    }
                    case ROTATABLE_SLAB -> {
                        Item i;
                        Block b=new RotatableSlabBlock(processSettings(entry.forceTransparent,AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE)));
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name+"_slab"), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name+"_slab"), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        if(entry.forceTransparent)
                            glassRenderNeeded.add(b);
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name+"_slab",b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name+"_slab",i,block));
                    }
                    case LECTERN -> {
                        Item i;
                        Block b=new LecternShapedBlock(processSettings(entry.forceTransparent,AbstractBlock.Settings.create().mapColor(Blocks.OAK_PLANKS.getDefaultMapColor()).nonOpaque().pistonBehavior(PistonBehavior.DESTROY)));
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        if(entry.forceTransparent)
                            glassRenderNeeded.add(b);
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name,b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name,i,block));
                    }
                    case DOOR -> {
                        Item i;
                        Block b=new DoorLongBlock(AbstractBlock.Settings.create().mapColor(Blocks.OAK_PLANKS.getDefaultMapColor()).nonOpaque().pistonBehavior(PistonBehavior.DESTROY).solidBlock(Blocks::never).suffocates(Blocks::never).blockVision(Blocks::never), BlockSetType.OAK);
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), i=new TallBlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        if(entry.forceTransparent)
                            glassRenderNeeded.add(b);
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name,b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name,i,block));
                    }
                    case FENCE -> {
                        Item i;
                        Block b=new FenceBlock(processSettings(entry.forceTransparent,AbstractBlock.Settings.create().sounds(BlockSoundGroup.NETHER_BRICKS)));
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name+"_fence"), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name+"_fence"), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        if(entry.forceTransparent)
                            glassRenderNeeded.add(b);
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name+"_fence",b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name+"_fence",i,block));
                    }
                    case OMNI_SLAB,OMNI_CARPET,OMNI_CARPET_SOLID -> {
                        Item i;
                        Block b=new OmniSlabBlock(processSettings(entry.forceTransparent,AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL)),block==BlockTypes.OMNI_CARPET||block==BlockTypes.OMNI_CARPET_SOLID,block==BlockTypes.OMNI_CARPET);
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name+"_slab"), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name+"_slab"), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        if(entry.forceTransparent)
                            glassRenderNeeded.add(b);
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name+"_slab",b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name+"_slab",i,block));
                    }
                }
            }
        });

    }
    private AbstractBlock.Settings processSettings(boolean non_opaque, AbstractBlock.Settings settings)
    {
        if(non_opaque)
            return settings.nonOpaque().solidBlock(Blocks::never);
        return settings;
    }
    public Block[] getGlasses() {
        return glassRenderNeeded.toArray(new Block[0]);
    }
    public void tagGenerator(Function<TagKey<Block>, FabricTagBuilder> factory){
        for (GeneratedBlockInstance b : generatedBlocks) {
                if (b.type == BlockTypes.FENCE)
                    factory.apply(BlockTags.FENCES).add(b.block);
                if (b.type == BlockTypes.DOOR)
                    factory.apply(BlockTags.DOORS).add(b.block);

        }
    }
    public void langGenerator(FabricLanguageProvider.TranslationBuilder builder)
    {
        for (GeneratedBlockInstance b : generatedBlocks) {
            builder.add(b.block, LangGenerator.capitalizeString(b.name.replaceAll("_"," ")));
        }
    }
    public void modelGenerator(ItemModelGenerator generator){
        for (GeneratedItemInstance b : generatedItems) {

            if(canGenerate(b.name,b.type)) {
                if (b.type == BlockTypes.GLASS_PANE || b.type == BlockTypes.IRON_BARS)
                    new Model(Optional.of(new Identifier("item/generated")), Optional.empty(), TextureKey.LAYER0).upload(ModelIds.getItemModelId(b.item), TextureMap.layer0(new Identifier(GreenResurgence.ID, "block/" + this.subdomain + "/" + b.name.replace("_pane", ""))), generator.writer);
                else if (b.type == BlockTypes.FENCE)
                    new Model(Optional.of(new Identifier("block/fence_inventory")), Optional.empty(), TextureKey.TEXTURE).upload(ModelIds.getItemModelId(b.item), TextureMap.texture(new Identifier(GreenResurgence.ID, "block/" + this.subdomain + "/" + b.name)), generator.writer);
                else if (b.type == BlockTypes.DOOR)
                    generator.register(b.item, new Model(Optional.of(helper.getBlockModelId(b.name).withSuffixedPath("_left")), Optional.empty()));
                else
                    generator.register(b.item, new Model(Optional.of(helper.getBlockModelId(b.name)), Optional.empty()));
            }
        }
    }
    public void modelGenerator(BlockStateModelGenerator generator){
        generatedBlocks.forEach(b->{

            if(canGenerate(b.name,b.type)) {
                switch (b.type) {

                    case SIMPLE, GLASS_BLOCK -> {
                        generator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(b.block, helper.getBlockModelId(b.name)));

                        TexturedModel.Factory factory = TexturedModel.makeFactory(b1 -> TextureMap.all(helper.getBlockModelId(b.name)),
                                new Model(Optional.of(new Identifier("minecraft", "block/cube_all")), Optional.empty(), TextureKey.ALL));
                        factory.get(b.block).getModel().upload(helper.getBlockModelId(b.name), factory.get(b.block).getTextures(), generator.modelCollector);

                    }
                    case PILLAR -> {
                        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(b.block, BlockStateVariant.create().put(VariantSettings.MODEL, helper.getBlockModelId(b.name))).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
                        TexturedModel.Factory factory = TexturedModel.makeFactory(b1 -> helper.textureMapPillar(b.name),
                                new Model(Optional.of(new Identifier("minecraft", "block/cube_column_horizontal")), Optional.empty(), TextureKey.END, TextureKey.SIDE));
                        factory.get(b.block).getModel().upload(helper.getBlockModelId(b.name), factory.get(b.block).getTextures(), generator.modelCollector);

                    }
                    case FURNACE -> {
                        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(b.block, BlockStateVariant.create().put(VariantSettings.MODEL, helper.getBlockModelId(b.name))).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
                        TexturedModel.Factory factory = TexturedModel.makeFactory(b1 -> helper.textureMapMachine(b.name),
                                new Model(Optional.of(new Identifier("minecraft", "block/orientable_with_bottom")), Optional.empty(), TextureKey.TOP, TextureKey.SIDE, TextureKey.FRONT, TextureKey.BOTTOM));
                        factory.get(b.block).getModel().upload(helper.getBlockModelId(b.name), factory.get(b.block).getTextures(), generator.modelCollector);


                    }
                    case GLASS_PANE, IRON_BARS -> {
                        helper.registerGlassPane(generator, b.name.replace("_pane", ""), b.block, b.type == BlockTypes.IRON_BARS);
                    }
                    case ROTATABLE_SLAB -> {

                    }
                    case DOOR -> {
                        helper.registerDoor(generator, b.name, b.block);
                    }
                    case FENCE -> {
                        helper.registerFence(generator, b.name, b.block);
                    }
                    case OMNI_CARPET,OMNI_CARPET_SOLID -> {
                        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(b.block, BlockStateVariant.create().put(VariantSettings.MODEL, helper.getBlockModelId(b.name))).coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates()));
                        TexturedModel.Factory factory = TexturedModel.makeFactory(b1 -> new TextureMap().put(TextureKey.PARTICLE, helper.getBlockModelId(b.name.replace("_slab", ""))),
                                new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/omni_carpet")), Optional.empty(), TextureKey.PARTICLE));
                        factory.get(b.block).getModel().upload(helper.getBlockModelId(b.name), factory.get(b.block).getTextures(), generator.modelCollector);
                    }
                    case OMNI_SLAB -> {
                        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(b.block, BlockStateVariant.create().put(VariantSettings.MODEL, helper.getBlockModelId(b.name))).coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates()));
                        TexturedModel.Factory factory = TexturedModel.makeFactory(b1 -> helper.textureOmniSlab(b.name.replace("_slab", "")),
                                new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/omni_slab")), Optional.empty(), TextureKey.FRONT, TextureKey.SIDE, TextureKey.BACK));
                        factory.get(b.block).getModel().upload(helper.getBlockModelId(b.name), factory.get(b.block).getTextures(), generator.modelCollector);
                    }
                }
            }
        });

    }

    public record GenericBlockInstance(String name,boolean forceTransparent, BlockTypes... blocks){};
    private record GeneratedBlockInstance(String name, Block block, BlockTypes type){};
    private record GeneratedItemInstance(String name, Item item, BlockTypes type){};
    private record GenExceptionInstance(String name, BlockTypes type){};
    protected static class GenericBlockRegisterInstance
    {
        private final GenericBlockSet set;
        private final String name;

        private GenericBlockRegisterInstance(GenericBlockSet set, String name) {
            this.set = set;
            this.name = name;
        }
        public GenericBlockRegisterInstance disableGen(BlockTypes... types)
        {
            for (BlockTypes type : types) {
                String name=this.name;
                switch (type)
                {
                    case OMNI_SLAB,OMNI_CARPET,OMNI_CARPET_SOLID,ROTATABLE_SLAB ->{
                        name=name+"_slab";
                    }
                    case FENCE -> {
                        name=name+"_fence";
                    }
                    case GLASS_PANE,IRON_BARS -> {
                        name=name+"_pane";
                    }
                }
                this.set.genExceptions.add(new GenExceptionInstance(name,type));
            }
            return this;
        }
    }
    public static enum BlockTypes
    {
        SIMPLE,
        FURNACE,
        PILLAR,
        GLASS_BLOCK,
        GLASS_PANE,
        IRON_BARS,
        ROTATABLE_SLAB,
        LECTERN,
        DOOR,
        FENCE,
        OMNI_SLAB,
        OMNI_CARPET,
        OMNI_CARPET_SOLID

    }
}
