package com.diamssword.greenresurgence.genericBlocks;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.datagen.LangGenerator;
import com.diamssword.greenresurgence.datagen.ModelHelper;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.FabricTagBuilder;
import net.minecraft.block.*;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.data.client.*;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Function;

public class GenericBlockSet {
    public final String subdomain;
    private final ModelHelper helper;
    private final List<GenericBlockInstance> blocks=new ArrayList<>();
    private final Map<Block,Transparency> glassRenderNeeded =new HashMap();
    private final List<GeneratedBlockInstance> generatedBlocks =new ArrayList<>();
    private final List<GeneratedItemInstance> generatedItems =new ArrayList<>();
    private final List<GenExceptionInstance> genExceptions =new ArrayList<>();
    private final List<ModelExceptionInstance> modelsExceptions =new ArrayList<>();
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
        this.blocks.add(new GenericBlockInstance(name,Transparency.UNDEFINED,blocks));
        return new GenericBlockRegisterInstance(this,name,blocks);
    }
    public GenericBlockRegisterInstance add(String name,Transparency render,BlockTypes... blocks)
    {
        this.blocks.add(new GenericBlockInstance(name,render,blocks));
        return new GenericBlockRegisterInstance(this,name,blocks);
    }

    /**
     *
     * @return false if modeleGeneration is disabled
     */
    private boolean canGenerate(String name,BlockTypes type)
    {
        return genExceptions.stream().filter(b->b.name.equals(name) && b.type==type).findFirst().isEmpty();
    }

    /**
     * @return true if you can generate the blockstate or the item model of a block
     */
    private boolean canGenerateState(String name,BlockTypes type)
    {
        return genExceptions.stream().filter(b->b.name.equals(name) && b.type==type && !b.genBlockState).findFirst().isEmpty();
    }
    public void register()
    {
        this.blocks.forEach(entry->{
            for (BlockTypes block : entry.blocks) {
                switch (block)
                {
                    case SIMPLE -> {
                        Block b=new Block(processSettings(entry.transparency,AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL)));
                        Item i;
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name,b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name,i,block));
                        glassRenderNeeded.put(b,entry.transparency);
                    }
                    case PILLAR -> {
                        Block b=new GenericPillar(processSettings(entry.transparency,AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL)),entry.transparency);
                        Item i;
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name,b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name,i,block));
                        glassRenderNeeded.put(b,entry.transparency);
                    }
                    case CHAIR_SLAB -> {
                        Block b=new ChairSlab(processSettings(entry.transparency,AbstractBlock.Settings.create().sounds(BlockSoundGroup.WOOD)));
                        Item i;
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name,b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name,i,block));
                        glassRenderNeeded.put(b,entry.transparency);
                    }
                    case CHAIR -> {
                        Block b=new Chair(processSettings(entry.transparency,AbstractBlock.Settings.create().sounds(BlockSoundGroup.WOOD)),entry.transparency);
                        Item i;
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name,b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name,i,block));
                        glassRenderNeeded.put(b,entry.transparency);
                    }
                    case PILLAR_SLAB -> {
                        Block b=new PillarSlab(processSettings(entry.transparency,AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL)));
                        Item i;
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name,b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name,i,block));
                        glassRenderNeeded.put(b,entry.transparency);
                    }
                    case CONNECTED_H,CONNECTED_V -> {
                        Block b=new ConnectedBlock(processSettings(entry.transparency,AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL)),block==BlockTypes.CONNECTED_H);
                        Item i;
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name,b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name,i,block));
                        glassRenderNeeded.put(b,entry.transparency);
                    }
                    case GLASS_BLOCK -> {
                        Block b=new GlassBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(Blocks::never).solidBlock(Blocks::never).suffocates(Blocks::never).blockVision(Blocks::never));
                        Item i;
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        glassRenderNeeded.put(b,entry.transparency==Transparency.UNDEFINED?Transparency.TRANSPARENT:entry.transparency);
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name,b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name,i,block));
                    }
                    case GLASS_PANE,IRON_BARS -> {
                        Item i;
                        Block b=new PaneBlock(AbstractBlock.Settings.create().sounds(block==BlockTypes.IRON_BARS?BlockSoundGroup.METAL: BlockSoundGroup.GLASS).nonOpaque());
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name+"_pane"), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name+"_pane"), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        glassRenderNeeded.put(b,entry.transparency==Transparency.UNDEFINED?Transparency.TRANSPARENT:entry.transparency);
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name+"_pane",b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name+"_pane",i,block));
                    }
                    case ROTATABLE_SLAB -> {
                        Item i;
                        Block b=new RotatableSlabBlock(processSettings(entry.transparency,AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE)));
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name+"_slab"), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name+"_slab"), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        glassRenderNeeded.put(b,entry.transparency);
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name+"_slab",b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name+"_slab",i,block));
                    }
                    case LECTERN -> {
                        Item i;
                        Block b=new LecternShapedBlock(processSettings(entry.transparency,AbstractBlock.Settings.create().mapColor(Blocks.OAK_PLANKS.getDefaultMapColor()).nonOpaque().pistonBehavior(PistonBehavior.DESTROY)));
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        glassRenderNeeded.put(b,entry.transparency);
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name,b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name,i,block));
                    }
                    case DOOR -> {
                        Item i;
                        Block b=new DoorLongBlock(AbstractBlock.Settings.create().mapColor(Blocks.OAK_PLANKS.getDefaultMapColor()).nonOpaque().pistonBehavior(PistonBehavior.DESTROY).solidBlock(Blocks::never).suffocates(Blocks::never).blockVision(Blocks::never), BlockSetType.OAK);
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), i=new TallBlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        glassRenderNeeded.put(b,entry.transparency);
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name,b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name,i,block));
                    }
                    case FENCE -> {
                        Item i;
                        Block b=new FenceBlock(processSettings(entry.transparency,AbstractBlock.Settings.create().sounds(BlockSoundGroup.NETHER_BRICKS)));
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name+"_fence"), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name+"_fence"), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        glassRenderNeeded.put(b,entry.transparency);
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name+"_fence",b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name+"_fence",i,block));
                    }
                    case OMNI_SLAB,OMNI_CARPET,OMNI_CARPET_SOLID -> {
                        Item i;
                        Block b=new OmniSlabBlock(processSettings(entry.transparency,AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL)),block==BlockTypes.OMNI_CARPET||block==BlockTypes.OMNI_CARPET_SOLID,block==BlockTypes.OMNI_CARPET);
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name+"_slab"), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name+"_slab"), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        glassRenderNeeded.put(b,entry.transparency);
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name+"_slab",b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name+"_slab",i,block));
                    }
                    case OMNI_BLOCK -> {
                        Item i;
                        Block b=new OmniBlock(processSettings(entry.transparency,AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL)));
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        glassRenderNeeded.put(b,entry.transparency);
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name,b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name,i,block));
                    }
                    case LANTERN -> {
                        Item i;
                        Block b=new LanternGeneric(processSettings(entry.transparency,AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).solid().strength(3.5f).sounds(BlockSoundGroup.LANTERN).luminance(LanternGeneric::produceLight).nonOpaque().pistonBehavior(PistonBehavior.DESTROY)));

                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        glassRenderNeeded.put(b,entry.transparency);
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name,b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name,i,block));
                    }
                    case BED -> {
                        Item i;
                        Block b=new BedGeneric(processSettings(entry.transparency,AbstractBlock.Settings.create().mapColor(MapColor.WHITE_GRAY).sounds(BlockSoundGroup.WOOD).strength(0.2f).nonOpaque().burnable().pistonBehavior(PistonBehavior.DESTROY)));

                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        glassRenderNeeded.put(b,entry.transparency);
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name,b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name,i,block));
                    }
                    case TRAPDOOR -> {
                        Item i;
                        Block b=new TrapdoorBlock(AbstractBlock.Settings.create().mapColor(Blocks.OAK_PLANKS.getDefaultMapColor()).nonOpaque().solidBlock(Blocks::never).suffocates(Blocks::never).blockVision(Blocks::never), BlockSetType.OAK);
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), i=new TallBlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        glassRenderNeeded.put(b,entry.transparency);
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name,b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name,i,block));
                    }
                    case LADDER -> {
                        Item i;
                        Block b=new GenericLadder(AbstractBlock.Settings.create().notSolid().strength(0.4f).sounds(BlockSoundGroup.LADDER).nonOpaque().pistonBehavior(PistonBehavior.DESTROY));
                        Registry.register(Registries.BLOCK, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), b);
                        Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain+"_"+entry.name), i=new BlockItem(b, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex)));
                        glassRenderNeeded.put(b,entry.transparency);
                        generatedBlocks.add(new GeneratedBlockInstance(entry.name,b,block));
                        generatedItems.add(new GeneratedItemInstance(entry.name,i,block));
                    }
                }
            }
        });

    }
    private AbstractBlock.Settings processSettings(Transparency non_opaque, AbstractBlock.Settings settings)
    {
        if(non_opaque!=Transparency.OPAQUE)
            settings=settings.nonOpaque().solidBlock(Blocks::never);
        if(non_opaque==Transparency.NOTFULL)
            settings=settings.notSolid().nonOpaque();
        return settings;
    }
    public Map<Block,Transparency> getGlasses() {
        return glassRenderNeeded;
    }
    public void tagGenerator(Function<TagKey<Block>, FabricTagBuilder> factory){
        for (GeneratedBlockInstance b : generatedBlocks) {
            if (b.type == BlockTypes.FENCE)
                factory.apply(BlockTags.FENCES).add(b.block);
            if (b.type == BlockTypes.DOOR) {
                factory.apply(BlockTags.DOORS).add(b.block);

                factory.apply(BlockTags.WOODEN_DOORS).add(b.block);
            }
            if (b.type == BlockTypes.LADDER)
                factory.apply(BlockTags.CLIMBABLE).add(b.block);

        }
    }
    public void langGenerator(FabricLanguageProvider.TranslationBuilder builder)
    {
        for (GeneratedBlockInstance b : generatedBlocks) {
            builder.add(b.block, LangGenerator.capitalizeString(b.name.replaceAll("_"," ")));
        }
    }
    public Optional<ModelType> getModelFor(String name,BlockTypes type)
    {
        return this.modelsExceptions.stream().filter(f->f.name.equals(name) && f.block==type).map(f->f.model).findFirst();
    }
    public void modelGenerator(ItemModelGenerator generator){
        for (GeneratedItemInstance b : generatedItems) {

            if(canGenerateState(b.name,b.type)) {
                if (b.type == BlockTypes.GLASS_PANE || b.type == BlockTypes.IRON_BARS)
                    new Model(Optional.of(new Identifier("item/generated")), Optional.empty(), TextureKey.LAYER0).upload(ModelIds.getItemModelId(b.item), TextureMap.layer0(new Identifier(GreenResurgence.ID, "block/" + this.subdomain + "/" + b.name.replace("_pane", ""))), generator.writer);
                else if (b.type == BlockTypes.FENCE)
                    new Model(Optional.of(new Identifier("block/fence_inventory")), Optional.empty(), TextureKey.TEXTURE).upload(ModelIds.getItemModelId(b.item), TextureMap.texture(new Identifier(GreenResurgence.ID, "block/" + this.subdomain + "/" + b.name)), generator.writer);
                else if (b.type == BlockTypes.DOOR)
                    generator.register(b.item, new Model(Optional.of(helper.getBlockModelId(b.name).withSuffixedPath("_left")), Optional.empty()));
                else if (b.type == BlockTypes.CONNECTED_V)
                    generator.register(b.item, new Model(Optional.of(helper.getBlockModelId(b.name).withSuffixedPath("_bottom")), Optional.empty()));
                else
                    generator.register(b.item, new Model(Optional.of(helper.getBlockModelId(b.name)), Optional.empty()));
            }
        }
    }
    public void modelGenerator(BlockStateModelGenerator generator){
        generatedBlocks.forEach(b->{

            if(canGenerateState(b.name,b.type)) {
                boolean noModel = !canGenerate(b.name,b.type);
                switch (b.type) {

                    case SIMPLE, GLASS_BLOCK -> {
                        generator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(b.block, helper.getBlockModelId(b.name)));
                        if(noModel) return;
                       TexturedModel.Factory factory = helper.getModeleFactoryFor(getModelFor(b.name,b.type).orElse(ModelType.SIMPLE),b.name);
                        factory.get(b.block).getModel().upload(helper.getBlockModelId(b.name), factory.get(b.block).getTextures(), generator.modelCollector);

                    }
                    case PILLAR,CHAIR_SLAB,CHAIR,PILLAR_SLAB -> {
                        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(b.block, BlockStateVariant.create().put(VariantSettings.MODEL, helper.getBlockModelId(b.name))).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
                        if(noModel) return;
                        TexturedModel.Factory factory = helper.getModeleFactoryFor(getModelFor(b.name,b.type).orElse(ModelType.PILLAR),b.name);
                        factory.get(b.block).getModel().upload(helper.getBlockModelId(b.name), factory.get(b.block).getTextures(), generator.modelCollector);

                    }
                    case CONNECTED_V -> {
                        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(b.block)
                                .coordinate(ModelHelper.fillConnectedPillarVariantMap(BlockStateVariantMap.create(HorizontalFacingBlock.FACING, ConnectedBlock.BOTTOM),helper.getBlockModelId(b.name).withSuffixedPath("_bottom"),helper.getBlockModelId(b.name).withSuffixedPath("_up"))));
                        if(noModel) return;
                        TexturedModel.Factory factory = helper.getModeleFactoryFor(getModelFor(b.name,b.type).orElse(ModelType.PILLAR),b.name);
                        factory.get(b.block).getModel().upload(helper.getBlockModelId(b.name), factory.get(b.block).getTextures(), generator.modelCollector);

                    }
                    case LADDER -> {
                        TextureMap map = new TextureMap();
                        map.put(TextureKey.PARTICLE,helper.getBlockModelId(b.name));
                        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(b.block, BlockStateVariant.create().put(VariantSettings.MODEL, helper.getBlockModelId(b.name))).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
                        if(noModel) return;
                        TexturedModel.Factory factory = TexturedModel.makeFactory(b1 -> map,new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/ladder")), Optional.empty(), TextureKey.PARTICLE));
                        factory.get(b.block).getModel().upload(helper.getBlockModelId(b.name), factory.get(b.block).getTextures(), generator.modelCollector);
                    }
                    case GLASS_PANE, IRON_BARS -> {
                        helper.registerGlassPane(generator, b.name.replace("_pane", ""), b.block, b.type == BlockTypes.IRON_BARS);
                    }
                    case DOOR -> {
                        helper.registerDoor(generator, b.name, b.block);
                    }
                    case LANTERN -> {
                        helper.registerLantern(generator,b.name,b.block);
                    }
                    case FENCE -> {
                        helper.registerFence(generator, b.name, b.block);
                    }
                    case OMNI_CARPET,OMNI_CARPET_SOLID -> {
                        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(b.block, BlockStateVariant.create().put(VariantSettings.MODEL, helper.getBlockModelId(b.name))).coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates()));
                        if(noModel) return;
                        TexturedModel.Factory factory = TexturedModel.makeFactory(b1 -> new TextureMap().put(TextureKey.PARTICLE, helper.getBlockModelId(b.name.replace("_slab", ""))),
                                new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/omni_carpet")), Optional.empty(), TextureKey.PARTICLE));
                        factory.get(b.block).getModel().upload(helper.getBlockModelId(b.name), factory.get(b.block).getTextures(), generator.modelCollector);
                    }
                    case OMNI_SLAB -> {
                        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(b.block, BlockStateVariant.create().put(VariantSettings.MODEL, helper.getBlockModelId(b.name))).coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates()));
                        if(noModel) return;
                        TexturedModel.Factory factory = TexturedModel.makeFactory(b1 -> helper.textureOmniSlab(b.name.replace("_slab", "")),
                                new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/omni_slab")), Optional.empty(), TextureKey.FRONT, TextureKey.SIDE, TextureKey.BACK));
                        factory.get(b.block).getModel().upload(helper.getBlockModelId(b.name), factory.get(b.block).getTextures(), generator.modelCollector);
                    }
                    case OMNI_BLOCK -> {
                        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(b.block, BlockStateVariant.create().put(VariantSettings.MODEL, helper.getBlockModelId(b.name))).coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates()));
                        if(noModel) return;
                        TexturedModel.Factory factory = helper.getModeleFactoryFor(getModelFor(b.name,b.type).orElse(ModelType.MACHINE),b.name);
                        factory.get(b.block).getModel().upload(helper.getBlockModelId(b.name), factory.get(b.block).getTextures(), generator.modelCollector);
                    }
                    case BED -> {
                        helper.registerBed(generator,b.name,b.block);
                    }
                    case TRAPDOOR -> {
                        helper.registerTrapdoor(generator, b.name, b.block);
                    }
                }
            }
        });

    }

    public record GenericBlockInstance(String name,Transparency transparency, BlockTypes... blocks){};
    private record GeneratedBlockInstance(String name, Block block, BlockTypes type){};
    private record GeneratedItemInstance(String name, Item item, BlockTypes type){};
    private record GenExceptionInstance(String name, BlockTypes type,boolean genBlockState){};
    private record ModelExceptionInstance(String name, BlockTypes block,ModelType model){};
    protected static class GenericBlockRegisterInstance
    {
        private final GenericBlockSet set;
        private final String name;
        private final BlockTypes[] registeredTypes;

        private GenericBlockRegisterInstance(GenericBlockSet set, String name,BlockTypes[] registeredTypes) {
            this.set = set;
            this.name = name;
            this.registeredTypes=registeredTypes;
        }
        public GenericBlockRegisterInstance model(ModelType model)
        {
            return this.model(model,this.registeredTypes);
        }
        public GenericBlockRegisterInstance model(ModelType model,BlockTypes... types)
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
                this.set.modelsExceptions.add(new ModelExceptionInstance(name,type,model));
            }
            return  this;
        }

        public GenericBlockRegisterInstance disableGen(boolean genBlockStateAndItem)
        {
            return this.disableGen(genBlockStateAndItem,this.registeredTypes);
        }
        public GenericBlockRegisterInstance disableGen(boolean genBlockStateAndItem,BlockTypes... types)
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
                this.set.genExceptions.add(new GenExceptionInstance(name,type,genBlockStateAndItem));
            }
            return this;
        }
    }
    public static enum Transparency
    {
        TRANSPARENT,
        CUTOUT,
        OPAQUE,
        NOTFULL,
        UNDEFINED
    }
    public static enum ModelType
    {
        SIMPLE,
        PILLAR,
        INVERSED_PILLAR,
        COMPOSTER,
        MACHINE,
        BOTOMLESS_MACHINE,
        TWO_TEXTURED_MACHINE,

    }
    public static enum BlockTypes
    {
        SIMPLE,
        PILLAR,
        PILLAR_SLAB,
        CHAIR_SLAB,
        CHAIR,
        GLASS_BLOCK,
        GLASS_PANE,
        IRON_BARS,
        ROTATABLE_SLAB,
        LECTERN,
        DOOR,
        FENCE,
        OMNI_SLAB,
        OMNI_BLOCK,
        OMNI_CARPET,
        OMNI_CARPET_SOLID,
        LANTERN,
        BED,
        TRAPDOOR,
        LADDER,
        CONNECTED_V,
        CONNECTED_H
    }
}
