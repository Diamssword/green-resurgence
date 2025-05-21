package com.diamssword.greenresurgence.genericBlocks;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blocks.SideShelfBlock;
import com.diamssword.greenresurgence.datagen.LangGenerator;
import com.diamssword.greenresurgence.datagen.ModelHelper;
import com.diamssword.greenresurgence.items.BlockVariantItem;
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
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.*;
import java.util.function.Function;


public class GenericBlockSet {
	public final String subdomain;
	private final ModelHelper helper;
	private final List<GenericBlockRegisterInstance> blocks = new ArrayList<>();
	private final List<GeneratedBlockInstance> generatedBlocks = new ArrayList<>();
	private final List<GeneratedItemInstance> generatedItems = new ArrayList<>();
	private final Map<String, BlockVariantItem> itemGroups = new HashMap<>();
	private int tabIndex;

	public GenericBlockSet(String subdomain) {
		this.subdomain = subdomain;
		helper = new ModelHelper(this.subdomain);
	}

	public void setTabIndex(int ind) {
		this.tabIndex = ind;
	}

	public ItemStack displayStack() {
		return generatedBlocks.isEmpty() ? new ItemStack(Items.STICK) : new ItemStack(generatedBlocks.get(0).block);
	}

	public GenericBlockRegisterInstance create(String... names) {
		var d = new GenericBlockRegisterInstance(names);
		this.blocks.add(d);
		return d;
	}

	public GenericBlockRegisterInstance add(String name, Transparency render, BlockType... blocks) {
		var d = new GenericBlockRegisterInstance(name);
		for (BlockType block : blocks) {
			d.addSub(block);
		}
		d.setTransparency(render);
		this.blocks.add(d);
		return d;
	}

	private void genericRegisterHelper(String name, GenericBlockProp entry, Block b) {
		Item i;
		Registry.register(Registries.BLOCK, getBlockId(name, entry), b);
		Registry.register(Registries.ITEM, getBlockId(name, entry), i = new BlockItem(b, addItemGroup(entry)));
		generatedBlocks.add(new GeneratedBlockInstance(name + entry.subname, b, entry));
		generatedItems.add(new GeneratedItemInstance(name + entry.subname, i, entry));
	}

	public void register() {
		this.blocks.forEach(entry -> {
			entry.blocks.values().forEach(v -> {
				if (v.itemGroup != null && !itemGroups.containsKey(v.itemGroup)) {
					itemGroups.put(v.itemGroup, new BlockVariantItem(new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex).maxCount(1)));
				}
				for (String name : entry.names) {
					if (v.itemGroup != null)
						itemGroups.get(v.itemGroup).addVariant(getBlockId(name, v));
					var settings = processSettings(v);
					switch (v.type) {
						case SIMPLE -> genericRegisterHelper(name, v, v.toggleable ? new GenericBlockToggleable(settings, v) : new GenericBlock(settings, v));
						case PILLAR -> genericRegisterHelper(name, v, v.toggleable ? new GenericPillarToggleable(settings, v) : new GenericPillar(settings, v));
						case CONNECTED_PILLAR -> genericRegisterHelper(name, v, new GenericConnectedPillar(settings, v));
						case OMNI_BLOCK -> genericRegisterHelper(name, v, v.toggleable ? new OmniBlockToggleable(settings, v) : new OmniBlock(settings, v));
						case ROTATABLE_SLAB -> genericRegisterHelper(name, v, new RotatableSlabBlock(settings));
						case PANE -> genericRegisterHelper(name, v, new PaneBlock(settings));
						case STAIRS -> genericRegisterHelper(name, v, new StairsBlock(Blocks.STONE.getDefaultState(), settings));
						case LECTERN ->
								genericRegisterHelper(name, v, new LecternShapedBlock(processSettings(v, AbstractBlock.Settings.create().mapColor(Blocks.OAK_PLANKS.getDefaultMapColor()).nonOpaque().pistonBehavior(PistonBehavior.DESTROY))));
						case FENCE -> genericRegisterHelper(name, v, new FenceBlock(processSettings(v, settings)));
						case DOOR -> {
							Item i;
							Block b = new DoorLongBlock(processSettings(v, AbstractBlock.Settings.create().mapColor(Blocks.OAK_PLANKS.getDefaultMapColor()).nonOpaque().pistonBehavior(PistonBehavior.DESTROY).solidBlock(Blocks::never).suffocates(Blocks::never).blockVision(Blocks::never)), BlockSetType.OAK);
							Registry.register(Registries.BLOCK, getBlockId(name, v), b);
							Registry.register(Registries.ITEM, getBlockId(name, v), i = new TallBlockItem(b, addItemGroup(v)));
							generatedBlocks.add(new GeneratedBlockInstance(name, b, v));
							generatedItems.add(new GeneratedItemInstance(name, i, v));
						}
						case LANTERN ->
								genericRegisterHelper(name, v, new LanternGeneric(processSettings(v, AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).solid().strength(3.5f).sounds(BlockSoundGroup.LANTERN).luminance(LanternGeneric::produceLight).nonOpaque().pistonBehavior(PistonBehavior.DESTROY))));
						case BED ->
								genericRegisterHelper(name, v, new BedGeneric(processSettings(v, AbstractBlock.Settings.create().mapColor(MapColor.WHITE_GRAY).sounds(BlockSoundGroup.WOOD).strength(0.2f).nonOpaque().burnable().pistonBehavior(PistonBehavior.DESTROY))));
						case TRAPDOOR ->
								genericRegisterHelper(name, v, new TrapdoorBlock(processSettings(v, AbstractBlock.Settings.create().mapColor(Blocks.OAK_PLANKS.getDefaultMapColor()).nonOpaque().solidBlock(Blocks::never).suffocates(Blocks::never).blockVision(Blocks::never)), BlockSetType.OAK));
					}
				}
			});

		});
		itemGroups.keySet().forEach(v -> Registry.register(Registries.ITEM, new Identifier(GreenResurgence.ID, this.subdomain + "_" + v), itemGroups.get(v)));

	}

	private Identifier getBlockId(String base, BlockType type, SubBlock sub) {
		return new Identifier(GreenResurgence.ID, this.subdomain + "_" + base + getSuffix(type, sub));
	}

	private Identifier getBlockId(String base, GenericBlockProp reg) {
		return new Identifier(GreenResurgence.ID, this.subdomain + "_" + base + reg.subname);
	}

	private OwoItemSettings addItemGroup(GenericBlockProp prop) {
		if (prop.itemGroup != null)
			return new OwoItemSettings();
		return new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(this.tabIndex);
	}

	private AbstractBlock.Settings processSettings(GenericBlockProp entry) {
		return processSettings(entry, AbstractBlock.Settings.create());
	}

	private AbstractBlock.Settings processSettings(GenericBlockProp entry, AbstractBlock.Settings settings) {
		settings = settings.sounds(entry.sound);
		if (entry.transparency != Transparency.OPAQUE)
			settings = settings.nonOpaque().solidBlock(Blocks::never);
		if (entry.transparency == Transparency.NOTFULL)
			settings = settings.notSolid().nonOpaque();
		var l = entry.light;
		if (l > 0) {
			if (entry.toggleable)
				settings = settings.luminance(st -> st.getProperties().contains(Properties.OPEN) && st.get(Properties.OPEN) ? l : 0);
			else
				settings = settings.luminance(st -> l);
		}
		return settings;
	}

	public Map<Block, Transparency> getGlasses() {
		var res = new HashMap<Block, Transparency>();
		generatedBlocks.forEach(v -> {
			if (v.props.transparency == Transparency.CUTOUT || v.props.transparency == Transparency.TRANSPARENT)
				res.put(v.block, v.props.transparency);
		});
		return res;
	}

	public void tagGenerator(Function<TagKey<Block>, FabricTagBuilder> factory) {
		for (GeneratedBlockInstance b : generatedBlocks) {
			if (b.props.type == BlockType.FENCE)
				factory.apply(BlockTags.FENCES).add(b.block);
			if (b.props.type == BlockType.DOOR) {
				factory.apply(BlockTags.DOORS).add(b.block);
				factory.apply(BlockTags.WOODEN_DOORS).add(b.block);
			}
			if (b.props.type == BlockType.BED)
				factory.apply(BlockTags.BEDS).add(b.block);
			//    if (b.props.type == BlockType.LADDER)
			//       factory.apply(BlockTags.CLIMBABLE).add(b.block);
			if (b.props.type == BlockType.STAIRS)
				factory.apply(BlockTags.STAIRS).add(b.block);
			if (b.props.subtype == SubBlock.SLAB)
				factory.apply(BlockTags.SLABS).add(b.block);
			for (TagKey<Block> tag : b.props.tags) {
				factory.apply(tag).add(b.block);
			}


		}
	}

	public void langGenerator(FabricLanguageProvider.TranslationBuilder builder) {
		for (GeneratedBlockInstance b : generatedBlocks) {
			builder.add(b.block, LangGenerator.capitalizeString(b.name.replaceAll("_", " ")));
		}
		itemGroups.forEach((k, v) -> {
			var f = generatedBlocks.stream().filter(v1 -> k.equals(v1.props.itemGroup)).findFirst();
			f.ifPresent(generatedBlockInstance -> builder.add("item." + GreenResurgence.ID + "." + this.subdomain + "_" + k, "[" + LangGenerator.capitalizeString(generatedBlockInstance.name.replaceAll("_", " ")) + "]"));
		});
	}

	public void modelGenerator(ItemModelGenerator generator) {
		for (GeneratedItemInstance b : generatedItems) {

			if (b.type.genBlockState) {

				if (b.type.type == BlockType.PANE)
					new Model(Optional.of(new Identifier("item/generated")), Optional.empty(), TextureKey.LAYER0).upload(ModelIds.getItemModelId(b.item), TextureMap.layer0(new Identifier(GreenResurgence.ID, "block/" + this.subdomain + "/" + b.name.replace("_pane", ""))), generator.writer);
				else if (b.type.type == BlockType.FENCE) {
					new Model(Optional.of(new Identifier("block/fence_inventory")), Optional.empty(), TextureKey.TEXTURE).upload(ModelIds.getItemModelId(b.item), TextureMap.texture(new Identifier(GreenResurgence.ID, "block/" + this.subdomain + "/" + b.name.replace("_fence", b.type.variants > 0 ? "/" + b.name.replace("_fence", "") + "0" : ""))), generator.writer);
				} else if (b.type.type == BlockType.DOOR)
					generator.register(b.item, new Model(Optional.of(helper.getBlockModelId(b.name).withSuffixedPath("_left")), Optional.empty()));
//                else if (b.type.type == BlockType.CONNECTED_V)
					//       generator.register(b.item, new Model(Optional.of(helper.getBlockModelId(b.name).withSuffixedPath("_bottom")), Optional.empty()));
					//  else if(b.type.subtype== SubBlock.CARPET)
					//{
					//  generator.register(b.item, new Model(Optional.of(helper.transformVariantModelId(helper.getBlockModelId(b.name.replace("_carpet","")),b.type.variants)), Optional.empty()));
					//}
				else {
					generator.register(b.item, new Model(Optional.of(helper.transformVariantModelId(helper.getBlockModelId(b.name), b.type.variants)), Optional.empty()));
				}
			}
		}
		itemGroups.forEach((k, v) -> {
			var f = generatedBlocks.stream().filter(v1 -> k.equals(v1.props.itemGroup)).findFirst();
			f.ifPresent(generatedBlockInstance -> generator.register(Registries.ITEM.get(new Identifier(GreenResurgence.ID, this.subdomain + "_" + k)), new Model(Optional.of(helper.getBlockModelId(generatedBlockInstance.name)), Optional.empty())));
		});
	}

	public void modelGenerator(BlockStateModelGenerator generator) {
		generatedBlocks.forEach(b -> {
			if (b.props.genBlockState) {
				boolean noModel = !b.props.genModel;
				int variants = b.props.variants;
				var model = b.props.model;
				var Tname = b.name;
				if (b.props.subtype == SubBlock.CARPET)
					Tname = b.name.replace("_carpet", "");
				else if (b.props.subtype == SubBlock.SLAB)
					Tname = b.name.replace("_slab", "");
				switch (b.props.type) {

					case SIMPLE -> {
						if (b.props.toggleable) {
							var map = BlockStateVariantMap.create(Properties.OPEN)
									.register(true, BlockStateVariant.create().put(VariantSettings.MODEL, helper.getBlockModelId(b.name).withSuffixedPath("_open")))
									.register(false, BlockStateVariant.create().put(VariantSettings.MODEL, helper.getBlockModelId(b.name)));
							generator.blockStateCollector.accept(helper.createVariantsStates(VariantsBlockStateSupplier.create(b.block).coordinate(map), variants));
						} else
							generator.blockStateCollector.accept(helper.createVariantsStates(BlockStateModelGenerator.createSingletonBlockState(b.block, helper.getBlockModelId(b.name)), variants));
						if (noModel || b.props.toggleable) return;
						TexturedModel.Factory factory = helper.getModeleFactoryFor(model, Tname);
						helper.registerModel(factory, model, b.block, b.name, generator, variants);

					}
					case PILLAR -> {
						if (b.props.toggleable) {
							generator.blockStateCollector.accept(helper.createVariantsStates(VariantsBlockStateSupplier.create(b.block,
									BlockStateVariant.create().put(VariantSettings.MODEL, helper.getBlockModelId(b.name))).coordinate(
									ModelHelper.fillToggleableVariantMap(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.OPEN),
											helper.getBlockModelId(b.name), helper.getBlockModelId(b.name).withSuffixedPath("_open"), false)), variants));
						} else
							generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(b.block, BlockStateVariant.create().put(VariantSettings.MODEL, helper.getBlockModelId(b.name))).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
						if (noModel || b.props.toggleable) return;
						TexturedModel.Factory factory = helper.getModeleFactoryFor(model, Tname);
						factory.get(b.block).getModel().upload(helper.getBlockModelId(b.name), factory.get(b.block).getTextures(), generator.modelCollector);

					}
					case CONNECTED_PILLAR -> {

						generator.blockStateCollector.accept(helper.createVariantsStates(VariantsBlockStateSupplier.create(b.block,
										BlockStateVariant.create().put(VariantSettings.MODEL, helper.getBlockModelId(b.name))).coordinate(
										ModelHelper.fillConnectedBlockVariantMap(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, SideShelfBlock.MODEL),
												helper.getBlockModelId(b.name), helper.getBlockModelId(b.name).withSuffixedPath("_middle"), helper.getBlockModelId(b.name).withSuffixedPath("_left"), helper.getBlockModelId(b.name).withSuffixedPath("_right")))
								, variants));

					}
					case OMNI_BLOCK -> {
						if (b.props.toggleable) {
							generator.blockStateCollector.accept(helper.createVariantsStates(VariantsBlockStateSupplier.create(b.block,
									BlockStateVariant.create().put(VariantSettings.MODEL, helper.getBlockModelId(b.name))).coordinate(
									ModelHelper.fillToggleableVariantMap(BlockStateVariantMap.create(Properties.FACING, Properties.OPEN),
											helper.getBlockModelId(b.name), helper.getBlockModelId(b.name).withSuffixedPath("_open"), true)), variants));
						} else
							generator.blockStateCollector.accept(helper.createVariantsStates(VariantsBlockStateSupplier.create(b.block, BlockStateVariant.create().put(VariantSettings.MODEL, helper.getBlockModelId(b.name))).coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates()), variants));
						if (noModel || b.props.toggleable) return;
						TexturedModel.Factory factory = helper.getModeleFactoryFor(model, Tname);
						helper.registerModel(factory, model, b.block, b.name, generator, variants);
					}
					case PANE -> {
						helper.registerGlassPane(generator, b.name.replace("_pane", ""), b.block, false);
					}
					case DOOR -> {
						helper.registerDoor(generator, b.name, b.block);
					}
					case LANTERN -> {
						helper.registerLantern(generator, b.name, b.block);
					}
					case FENCE -> {
						helper.registerFence(generator, b.name, b.block, variants, model == ModelType.WALL);
					}
					case STAIRS -> {
						generator.blockStateCollector.accept(helper.createVariantsStates(BlockStateModelGenerator.createStairsBlockState(b.block, helper.getBlockModelId(b.name).withSuffixedPath("_inner"), helper.getBlockModelId(b.name), helper.getBlockModelId(b.name).withSuffixedPath("_outer")), variants));
						if (noModel) return;
						var facts = helper.getModelForStairs(b.name.replace("_stair", ""), model);
						helper.registerModel(facts[0], b.block, b.name, generator, variants, TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
						helper.registerModel(facts[1], b.block, b.name + "_inner", generator, variants, TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
						helper.registerModel(facts[2], b.block, b.name + "_outer", generator, variants, TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
					}
					case BED -> {
						helper.registerBed(generator, b.name, b.block);
					}
					case TRAPDOOR -> {
						if (noModel) return;
						helper.registerTrapdoor(generator, b.name, b.block);
					}
				}
			}
		});

	}

	public record GeneratedBlockInstance(String name, Block block, GenericBlockProp props) {
	}

	private record GeneratedItemInstance(String name, Item item, GenericBlockProp type) {
	}

	protected static class GenericBlockRegisterInstance {
		private final String[] names;
		private final Map<String, GenericBlockProp> blocks = new HashMap<>();

		private GenericBlockRegisterInstance(String... names) {
			this.names = names;
		}

		private static HitBox DefaultHitbox(SubBlock sub) {
			switch (sub) {
				case SLAB:
					return HitBox.SLAB;
				case CARPET:
					return HitBox.CARPET;
			}
			return HitBox.FULL;
		}

		public GenericBlockRegisterInstance addSub(BlockType type, ModelType model, HitBox hitbox, SubBlock sub) {
			var prop = new GenericBlockProp();
			prop.type = type;
			prop.solid = true;
			prop.sound = BlockSoundGroup.METAL;
			prop.model = model;
			prop.hitbox = hitbox;
			prop.subtype = sub;
			prop.subname = getSuffix(type, sub);
			prop.genModel = true;
			prop.genBlockState = true;
			prop.transparency = Transparency.UNDEFINED;
			blocks.put(getSuffix(type, sub), prop);
			return this;
		}

		public GenericBlockRegisterInstance addSub(BlockType type, ModelType model, HitBox hitbox) {
			return addSub(type, model, hitbox, SubBlock.NONE);
		}

		public GenericBlockRegisterInstance addSub(BlockType type, ModelType model) {
			return addSub(type, model, HitBox.FULL, SubBlock.NONE);
		}

		public GenericBlockRegisterInstance addSub(BlockType type, ModelType model, SubBlock sub) {
			return addSub(type, model, DefaultHitbox(sub), sub);
		}

		public GenericBlockRegisterInstance addSub(BlockType type) {
			return addSub(type, type == BlockType.PILLAR ? ModelType.PILLAR : ModelType.SIMPLE, HitBox.FULL, SubBlock.NONE);
		}

		public GenericBlockRegisterInstance addTags(TagKey<Block>... tags) {
			blocks.values().forEach(v -> {
				Collections.addAll(v.tags, tags);
			});
			return this;
		}

		public GenericBlockRegisterInstance addGroup(String id) {
			blocks.values().forEach(v -> {
				v.itemGroup = id;
			});
			return this;
		}

		public GenericBlockRegisterInstance setTransparency(Transparency trans) {
			blocks.values().forEach(v -> {
				v.transparency = trans;
			});
			return this;
		}

		public GenericBlockRegisterInstance setDamage(float damage) {
			blocks.values().forEach(v -> {
				v.damage = damage;
			});
			return this;
		}

		public GenericBlockRegisterInstance variant(int variants) {
			blocks.values().forEach(v -> {
				v.variants = variants;
			});
			return this;
		}

		public GenericBlockRegisterInstance light(int value) {
			blocks.values().forEach(v -> {
				v.light = value;
			});
			return this;
		}

		public GenericBlockRegisterInstance sound(BlockSoundGroup sound) {
			blocks.values().forEach(v -> {
				v.sound = sound;
			});
			return this;
		}

		public GenericBlockRegisterInstance notSolid() {
			blocks.values().forEach(v -> {
				v.solid = false;
			});
			return this;
		}

		public GenericBlockRegisterInstance notSolid(SubBlock sub) {
			blocks.values().forEach(v -> {
				if (v.subtype == sub)
					v.solid = false;
			});
			return this;
		}

		public GenericBlockRegisterInstance seat(float level) {
			blocks.values().forEach(v -> {
				v.seatLevel = level;
				v.isSeat = true;
			});
			return this;
		}

		public GenericBlockRegisterInstance disableGen(boolean genBlockStateAndItem, SubBlock subtype) {

			blocks.values().forEach(v -> {
				if (v.subtype == subtype) {
					v.genModel = false;
					v.genBlockState = genBlockStateAndItem;
				}
			});
			return this;
		}

		public GenericBlockRegisterInstance disableGen(boolean genBlockStateAndItem) {
			blocks.values().forEach(v -> {
				v.genModel = false;
				v.genBlockState = genBlockStateAndItem;
			});
			return this;
		}

		public GenericBlockRegisterInstance togglable() {
			blocks.values().forEach(v -> {
				v.toggleable = true;
			});
			return this;
		}
	}

	public static class GenericBlockProp {
		protected boolean solid;
		protected BlockSoundGroup sound;
		protected SubBlock subtype;
		protected Transparency transparency;
		protected String subname;
		protected ModelType model;
		protected BlockType type;
		protected int light;
		protected HitBox hitbox;
		protected boolean genModel;
		protected boolean genBlockState;
		protected boolean toggleable;
		protected float damage;
		protected float seatLevel;
		protected boolean isSeat;
		protected String itemGroup;
		protected int variants;
		protected List<TagKey<Block>> tags = new ArrayList<>();

	}

	private static String getSuffix(BlockType type, SubBlock sub) {
		switch (type) {
			case FENCE:
				return "_fence";
			case STAIRS:
				return "_stair";
			case PANE:
				return "_pane";
			default:
				switch (sub) {

					case NONE:
						return "";
					case PANE:
						return "_pane";
					case SLAB:
						return "_slab";
					case CARPET:
						return "_carpet";
				}
		}
		return "";
	}

	public enum Transparency {
		TRANSPARENT,
		CUTOUT,
		OPAQUE,
		NOTFULL,
		UNDEFINED
	}

	public enum HitBox {
		FULL(Block.createCuboidShape(0, 0, 0, 16, 16, 16)),
		SLAB(Block.createCuboidShape(0, 0, 0, 16, 8, 16)),
		FIXED_SLAB(Block.createCuboidShape(0, 0, 0, 16, 8, 16), false),
		CARPET(Block.createCuboidShape(0, 0, 0, 16, 1, 16)),
		LARGE_CARPET(Block.createCuboidShape(0, 0, -16, 32, 1, 32)),
		CARPET_FIXED(Block.createCuboidShape(0, 0, 0, 16, 1, 16), false),
		CENTER(Block.createCuboidShape(7, 0, 7, 9, 16, 9), false),
		SMALL_BOTTOM(Block.createCuboidShape(6, 0, 6, 10, 8, 10), false),
		MEDIUM(Block.createCuboidShape(3, 0, 3, 13, 13, 13), false),
		SMALL_TOP(Block.createCuboidShape(6, 8, 6, 10, 16, 10), false),
		STAIR(VoxelShapes.union(Block.createCuboidShape(0, 0, 0, 16, 8, 16), Block.createCuboidShape(0, 8, 0, 16, 16, 8)), true, true);
		public final VoxelShape shape;
		public final boolean needRotate;
		public final boolean horizontal;

		HitBox(VoxelShape shape) {
			this.shape = shape;
			this.needRotate = true;
			this.horizontal = false;
		}

		HitBox(VoxelShape shape, boolean needRotate) {
			this.shape = shape;
			this.needRotate = needRotate;
			this.horizontal = false;
		}

		HitBox(VoxelShape shape, boolean needRotate, boolean horizontal) {
			this.shape = shape;
			this.needRotate = needRotate;
			this.horizontal = horizontal;
		}
	}

	public enum ModelType {
		SIMPLE,
		PILLAR,
		SLAB,
		SLAB_3TEX,
		INVERSED_PILLAR,
		COMPOSTER,
		MACHINE,
		BOTOMLESS_MACHINE,
		TWO_TEXTURED_MACHINE,
		WALL,
		LADDER,
		CARPET

	}

	public enum SubBlock {
		NONE,
		PANE,
		SLAB,
		CARPET
	}

	public enum BlockType {
		SIMPLE,
		PILLAR,
		CONNECTED_PILLAR,
		STAIRS,
		PANE,
		LECTERN,
		DOOR,
		FENCE,
		ROTATABLE_SLAB,
		WALL,
		OMNI_BLOCK,
		LANTERN,
		BED,
		TRAPDOOR
	}
}
