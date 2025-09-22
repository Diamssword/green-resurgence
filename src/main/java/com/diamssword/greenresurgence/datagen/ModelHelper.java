package com.diamssword.greenresurgence.datagen;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blocks.SideShelfBlock;
import com.diamssword.greenresurgence.genericBlocks.GenericBlockSet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModelHelper {

	public final String subdomain;

	public ModelHelper(String subdomain) {
		this.subdomain = subdomain;
	}

	public BlockStateSupplier createVariantsStates(BlockStateSupplier base, int count) {
		if(count > 0)
			return new MyVariantsBlockStateSupplier(base, count);
		else
			return base;
	}

	public Identifier transformVariantModelId(Identifier parent, int variants) {
		if(variants > 0) {
			return parent.withSuffixedPath("/0");
		} else
			return parent;
	}

	public void registerModel(TexturedModel.Factory factory, Block block, String name, BlockStateModelGenerator generator, int variants, TextureKey... keys) {
		var tm = factory.get(block);
		var mod = tm.getModel();
		//  var map=createTextureMap(Stream.of(getTextureKeysFor(model,name)),factory.get(block).getTextures());
		if(variants <= 0) {
			mod.upload(getBlockModelId(name), factory.get(block).getTextures(), generator.modelCollector);
		} else {
			for(int i = 0; i < variants; i++) {
				var map = new TextureMap();
				for(TextureKey textureKey : keys) {
					var path = tm.getTextures().getTexture(textureKey);
					var st = path.getPath().substring(0, path.getPath().lastIndexOf("/"));
					var st1 = path.getPath().substring(path.getPath().lastIndexOf("/"));
					map.put(textureKey, new Identifier(path.getNamespace(), st + st1 + st1 + i));
				}
				mod.upload(getBlockModelId(name + "/" + i), map, generator.modelCollector);
			}
		}

	}

	private void registerModel(Model model, String name, String suffix, BlockStateModelGenerator generator, int variants, TextureMap map, TextureKey... keys) {

		if(variants <= 0) {
			model.upload(getBlockModelId(name + suffix), map, generator.modelCollector);
		} else {
			for(int i = 0; i < variants; i++) {
				var map1 = new TextureMap();
				for(TextureKey textureKey : keys) {
					var path = map.getTexture(textureKey);
					var st = path.getPath().substring(0, path.getPath().lastIndexOf("/"));
					var st1 = path.getPath().substring(path.getPath().lastIndexOf("/"));
					map1.put(textureKey, new Identifier(path.getNamespace(), st + st1 + st1 + i));
				}
				model.upload(getBlockModelId(name + suffix + "/" + i), map1, generator.modelCollector);
			}
		}
	}

	public void registerModel(TexturedModel.Factory factory, GenericBlockSet.ModelType model, Block block, String name, BlockStateModelGenerator generator, int variants) {
		registerModel(factory, block, name, generator, variants, getTextureKeysFor(model));
	}

	public TextureKey[] getTextureKeysFor(GenericBlockSet.ModelType type) {
		switch(type) {
			case SIMPLE:
				return new TextureKey[]{TextureKey.ALL};

			case PILLAR, INVERSED_PILLAR:
				return new TextureKey[]{TextureKey.END, TextureKey.SIDE};

			case COMPOSTER:
				return new TextureKey[]{TextureKey.PARTICLE, TextureKey.SIDE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.INSIDE};

			case MACHINE:
				return new TextureKey[]{TextureKey.TOP, TextureKey.SIDE, TextureKey.FRONT, TextureKey.BOTTOM};
			case BOTOMLESS_MACHINE:
				return new TextureKey[]{TextureKey.TOP, TextureKey.SIDE, TextureKey.FRONT};
			case TWO_TEXTURED_MACHINE:
				return new TextureKey[]{TextureKey.SIDE, TextureKey.FRONT};
			case SLAB, SLAB_3TEX:
				return new TextureKey[]{TextureKey.FRONT, TextureKey.SIDE, TextureKey.BACK};
			case CARPET, LADDER:
				return new TextureKey[]{TextureKey.PARTICLE};
		}
		return new TextureKey[]{TextureKey.ALL};
	}

	public TexturedModel.Factory getModeleFactoryFor(GenericBlockSet.ModelType type, String name) {
		switch(type) {

			case SIMPLE -> {
				return TexturedModel.makeFactory(b1 -> TextureMap.all(this.getBlockModelId(name)), new Model(Optional.of(new Identifier("minecraft", "block/cube_all")), Optional.empty(), getTextureKeysFor(type)));
			}
			case PILLAR -> {
				return TexturedModel.makeFactory(b1 -> this.textureMapPillar(name), new Model(Optional.of(new Identifier("minecraft", "block/cube_column_horizontal")), Optional.empty(), getTextureKeysFor(type)));
			}
			case COMPOSTER -> {
				return TexturedModel.makeFactory(b1 -> this.textureMapComposter(name), new Model(Optional.of(new Identifier("block/composter")), Optional.empty(), getTextureKeysFor(type)));
			}
			case INVERSED_PILLAR -> {
				return TexturedModel.makeFactory(b1 -> this.textureMapPillar(name), new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/cube_column_vertical")), Optional.empty(), getTextureKeysFor(type)));
			}
			case MACHINE -> {
				return TexturedModel.makeFactory(b1 -> this.textureMapMachine(name, false, false), new Model(Optional.of(new Identifier("minecraft", "block/orientable_with_bottom")), Optional.empty(), getTextureKeysFor(type)));
			}
			case BOTOMLESS_MACHINE -> {
				return TexturedModel.makeFactory(b1 -> this.textureMapMachine(name, true, false), new Model(Optional.of(new Identifier("minecraft", "block/orientable")), Optional.empty(), getTextureKeysFor(type)));
			}
			case TWO_TEXTURED_MACHINE -> {
				return TexturedModel.makeFactory(b1 -> this.textureMapMachine(name, true, true), new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/simple_orientable")), Optional.empty(), getTextureKeysFor(type)));
			}
			case WALL -> {

			}
			case SLAB_3TEX -> {
				return TexturedModel.makeFactory(b1 -> textureOmniSlab(name, GenericBlockSet.ModelType.MACHINE),
						new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/omni_slab")), Optional.empty(), getTextureKeysFor(type)));
			}
			case SLAB -> {
				return TexturedModel.makeFactory(b1 -> textureOmniSlab(name, GenericBlockSet.ModelType.SIMPLE),
						new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/omni_slab")), Optional.empty(), getTextureKeysFor(type)));

			}
			case LADDER -> {
				TextureMap map = new TextureMap();
				map.put(TextureKey.PARTICLE, getBlockModelId(name));
				return TexturedModel.makeFactory(b1 -> map, new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/ladder")), Optional.empty(), getTextureKeysFor(type)));
			}
			case CARPET -> {
				return TexturedModel.makeFactory(b1 -> new TextureMap().put(TextureKey.PARTICLE, getBlockModelId(name)),
						new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/omni_carpet")), Optional.empty(), getTextureKeysFor(type)));
			}
		}
		return TexturedModel.makeFactory(b1 -> TextureMap.all(this.getBlockModelId(name)), new Model(Optional.of(new Identifier("minecraft", "block/cube_all")), Optional.empty(), getTextureKeysFor(type)));
	}

	public TexturedModel.Factory[] getModelForStairs(String name, GenericBlockSet.ModelType model) {
		return new TexturedModel.Factory[]{
				TexturedModel.makeFactory(b1 -> textureMapStairs(name, model), new Model(Optional.of(new Identifier("minecraft", "block/stairs")), Optional.empty(), TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE)),
				TexturedModel.makeFactory(b1 -> textureMapStairs(name, model), new Model(Optional.of(new Identifier("minecraft", "block/inner_stairs")), Optional.empty(), TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE)),
				TexturedModel.makeFactory(b1 -> textureMapStairs(name, model), new Model(Optional.of(new Identifier("minecraft", "block/outer_stairs")), Optional.empty(), TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE))
		};
	}

	public TextureMap textureMapPillar(String name) {
		TextureMap map = new TextureMap();
		map.put(TextureKey.SIDE, getBlockModelId(name).withSuffixedPath("_side"));
		map.put(TextureKey.END, getBlockModelId(name).withSuffixedPath("_top"));
		return map;
	}

	public TextureMap textureMapComposter(String name) {
		TextureMap map = new TextureMap();
		map.put(TextureKey.SIDE, getBlockModelId(name).withSuffixedPath("_side"));
		map.put(TextureKey.TOP, getBlockModelId(name).withSuffixedPath("_top"));
		map.put(TextureKey.PARTICLE, getBlockModelId(name).withSuffixedPath("_side"));
		map.put(TextureKey.BOTTOM, getBlockModelId(name).withSuffixedPath("_bottom"));
		map.put(TextureKey.INSIDE, getBlockModelId(name).withSuffixedPath("_bottom"));
		return map;
	}

	public TextureMap textureMapMachine(String name, boolean noBottom, boolean noTop) {
		TextureMap map = new TextureMap();
		map.put(TextureKey.SIDE, getBlockModelId(name).withSuffixedPath("_side"));
		if(!noTop)
			map.put(TextureKey.TOP, getBlockModelId(name).withSuffixedPath("_top"));
		map.put(TextureKey.FRONT, getBlockModelId(name).withSuffixedPath("_front"));
		if(!noBottom)
			map.put(TextureKey.BOTTOM, getBlockModelId(name).withSuffixedPath("_bottom"));
		return map;
	}

	public TextureMap textureOmniSlab(String name, GenericBlockSet.ModelType type) {
		TextureMap map = new TextureMap();
		if(type == GenericBlockSet.ModelType.SIMPLE) {
			map.put(TextureKey.SIDE, getBlockModelId(name));
			map.put(TextureKey.FRONT, getBlockModelId(name));
			map.put(TextureKey.BACK, getBlockModelId(name));
		} else {

			map.put(TextureKey.SIDE, getBlockModelId(name).withSuffixedPath("_side"));
			map.put(TextureKey.FRONT, getBlockModelId(name).withSuffixedPath("_front"));
			map.put(TextureKey.BACK, getBlockModelId(name).withSuffixedPath("_back"));
		}

		return map;
	}

	public TextureMap textureMapStairs(String name, GenericBlockSet.ModelType type) {
		TextureMap map = new TextureMap();
		if(type == GenericBlockSet.ModelType.SIMPLE) {
			map.put(TextureKey.SIDE, getBlockModelId(name));
			map.put(TextureKey.BOTTOM, getBlockModelId(name));
			map.put(TextureKey.TOP, getBlockModelId(name));
		} else {
			map.put(TextureKey.SIDE, getBlockModelId(name).withSuffixedPath("_side"));
			map.put(TextureKey.BOTTOM, getBlockModelId(name).withSuffixedPath("_back"));
			map.put(TextureKey.TOP, getBlockModelId(name).withSuffixedPath("_front"));
		}

		return map;
	}

	public void registerTrapdoor(BlockStateModelGenerator generator, String name, Block trapdoorBlock) {
		TextureMap textureMap = TextureMap.texture(getBlockModelId(name));
		Identifier identifier = Models.TEMPLATE_TRAPDOOR_TOP.upload(getBlockModelId(name).withSuffixedPath("_top"), textureMap, generator.modelCollector);
		Identifier identifier2 = Models.TEMPLATE_TRAPDOOR_BOTTOM.upload(getBlockModelId(name), textureMap, generator.modelCollector);
		Identifier identifier3 = Models.TEMPLATE_TRAPDOOR_OPEN.upload(getBlockModelId(name).withSuffixedPath("_open"), textureMap, generator.modelCollector);
		generator.blockStateCollector.accept(BlockStateModelGenerator.createTrapdoorBlockState(trapdoorBlock, identifier, identifier2, identifier3));
	}

	public final void registerLantern(BlockStateModelGenerator generator, String name, Block lantern) {

		TextureMap textureMap = new TextureMap().put(TextureKey.LANTERN, getBlockModelId(name));
		TextureMap textureMap_off = new TextureMap().put(TextureKey.LANTERN, getBlockModelId(name).withSuffixedPath("_off"));
		Identifier identifier1 = new Model(Optional.of(new Identifier("minecraft", "block/template_lantern")), Optional.empty(), TextureKey.LANTERN).upload(getBlockModelId(name), textureMap, generator.modelCollector);
		Identifier identifier2 = new Model(Optional.of(new Identifier("minecraft", "block/template_hanging_lantern")), Optional.empty(), TextureKey.LANTERN).upload(getBlockModelId(name).withSuffixedPath("_top"), textureMap, generator.modelCollector);
		Identifier identifier3 = new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/side_lantern")), Optional.empty(), TextureKey.LANTERN).upload(getBlockModelId(name).withSuffixedPath("_side"), textureMap, generator.modelCollector);

		Identifier identifier4 = new Model(Optional.of(new Identifier("minecraft", "block/template_lantern")), Optional.empty(), TextureKey.LANTERN).upload(getBlockModelId(name).withSuffixedPath("_off"), textureMap_off, generator.modelCollector);
		Identifier identifier5 = new Model(Optional.of(new Identifier("minecraft", "block/template_hanging_lantern")), Optional.empty(), TextureKey.LANTERN).upload(getBlockModelId(name).withSuffixedPath("_top_off"), textureMap_off, generator.modelCollector);
		Identifier identifier6 = new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/side_lantern")), Optional.empty(), TextureKey.LANTERN).upload(getBlockModelId(name).withSuffixedPath("_side_off"), textureMap_off, generator.modelCollector);

		generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(lantern).coordinate(fillLanternVariantMap(BlockStateVariantMap.create(Properties.FACING, Properties.LIT), identifier1, identifier2, identifier3, identifier4, identifier5, identifier6)));

	}

	public final void registerBed(BlockStateModelGenerator generator, String name, Block lantern) {

		TextureKey key = TextureKey.of("bed");
		TextureMap textureMap = new TextureMap().put(key, getBlockModelId(name));

		Identifier identifier1 = new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/bed")), Optional.empty(), key).upload(getBlockModelId(name), textureMap, generator.modelCollector);
		generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(lantern).coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
				.register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.MODEL, identifier1))
				.register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.MODEL, identifier1).put(VariantSettings.Y, VariantSettings.Rotation.R90))
				.register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.MODEL, identifier1).put(VariantSettings.Y, VariantSettings.Rotation.R270))
				.register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.MODEL, identifier1).put(VariantSettings.Y, VariantSettings.Rotation.R180))
		));

	}

	public final void registerGlassPane(BlockStateModelGenerator generator, String name, Block glassPane, boolean isIron) {

		TextureMap textureMap = new TextureMap().put(TextureKey.PANE, getBlockModelId(name)).put(TextureKey.EDGE, isIron ? getBlockModelId(name) : getBlockModelId(name).withSuffixedPath("_pane_top"));
		Identifier identifier = Models.TEMPLATE_GLASS_PANE_POST.upload(getBlockModelId(name).withSuffixedPath("_post"), textureMap, generator.modelCollector);
		Identifier identifier2 = Models.TEMPLATE_GLASS_PANE_SIDE.upload(getBlockModelId(name).withSuffixedPath("_side"), textureMap, generator.modelCollector);
		Identifier identifier3 = Models.TEMPLATE_GLASS_PANE_SIDE_ALT.upload(getBlockModelId(name).withSuffixedPath("_side_alt"), textureMap, generator.modelCollector);
		Identifier identifier4 = Models.TEMPLATE_GLASS_PANE_NOSIDE.upload(getBlockModelId(name).withSuffixedPath("_noside"), textureMap, generator.modelCollector);
		Identifier identifier5 = Models.TEMPLATE_GLASS_PANE_NOSIDE_ALT.upload(getBlockModelId(name).withSuffixedPath("_noside_alt"), textureMap, generator.modelCollector);
		// Models.GENERATED.upload(getBlockModelId(name).withSuffixedPath("_pane"), TextureMap.layer0(glassPane), generator.modelCollector);
		generator.blockStateCollector.accept(MultipartBlockStateSupplier.create(glassPane).with(BlockStateVariant.create().put(VariantSettings.MODEL, identifier)).with(When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier2)).with(When.create().set(Properties.EAST, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R90)).with(When.create().set(Properties.SOUTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier3)).with(When.create().set(Properties.WEST, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R90)).with(When.create().set(Properties.NORTH, false), BlockStateVariant.create().put(VariantSettings.MODEL, identifier4)).with(When.create().set(Properties.EAST, false), BlockStateVariant.create().put(VariantSettings.MODEL, identifier5)).with(When.create().set(Properties.SOUTH, false), BlockStateVariant.create().put(VariantSettings.MODEL, identifier5).put(VariantSettings.Y, VariantSettings.Rotation.R90)).with(When.create().set(Properties.WEST, false), BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.Y, VariantSettings.Rotation.R270)));
	}

	public final void registerFence(BlockStateModelGenerator generator, String name, Block fence, int variants, boolean isWall) {
		var key = isWall ? TextureKey.WALL : TextureKey.TEXTURE;
		TextureMap textureMap = new TextureMap().put(key, getBlockModelId(name.replace("_fence", "")));
		registerModel(isWall ? Models.TEMPLATE_WALL_POST : Models.FENCE_POST, name, "_post", generator, variants, textureMap, key);
		registerModel(isWall ? Models.TEMPLATE_WALL_SIDE : Models.FENCE_SIDE, name, "_side", generator, variants, textureMap, key);
		Identifier identifier = getBlockModelId(name).withSuffixedPath("_post");// Models.FENCE_POST.upload(getBlockModelId(name).withSuffixedPath("_post"), textureMap, generator.modelCollector);
		Identifier identifier2 = getBlockModelId(name).withSuffixedPath("_side");//Models.FENCE_SIDE.upload(getBlockModelId(name).withSuffixedPath("_side"), textureMap, generator.modelCollector);
		var bs = MultipartBlockStateSupplier.create(fence)
				.with(BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
				.with(When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.UVLOCK, true))
				.with(When.create().set(Properties.EAST, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.UVLOCK, true).put(VariantSettings.Y, VariantSettings.Rotation.R90))
				.with(When.create().set(Properties.SOUTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.UVLOCK, true).put(VariantSettings.Y, VariantSettings.Rotation.R180))
				.with(When.create().set(Properties.WEST, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.UVLOCK, true).put(VariantSettings.Y, VariantSettings.Rotation.R270));
		generator.blockStateCollector.accept(createVariantsStates(bs, variants));
	}


	public void registerDoor(BlockStateModelGenerator generator, String name, Block doorBlock) {
		TextureMap textureMap = TextureMap.texture(getBlockModelId(name));
		Identifier identifier = new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/door_left")), Optional.empty(), TextureKey.PARTICLE).upload(getBlockModelId(name).withSuffixedPath("_left"), textureMap, generator.modelCollector);
		Identifier identifier2 = new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/door_left_open")), Optional.empty(), TextureKey.PARTICLE).upload(getBlockModelId(name).withSuffixedPath("_left_open"), textureMap, generator.modelCollector);
		Identifier identifier3 = new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/door_right")), Optional.empty(), TextureKey.PARTICLE).upload(getBlockModelId(name).withSuffixedPath("_right"), textureMap, generator.modelCollector);
		Identifier identifier4 = new Model(Optional.of(new Identifier(GreenResurgence.ID, "block/generic/door_right_open")), Optional.empty(), TextureKey.PARTICLE).upload(getBlockModelId(name).withSuffixedPath("_right_open"), textureMap, generator.modelCollector);
		generator.blockStateCollector.accept(createDoorBlockState(doorBlock, identifier, identifier2, identifier3, identifier4));
	}

	public static BlockStateSupplier createDoorBlockState(Block doorBlock, Identifier LeftHingeClosedModelId, Identifier LeftHingeOpenModelId, Identifier RightHingeClosedModelId, Identifier RightHingeOpenModelId) {
		return VariantsBlockStateSupplier.create(doorBlock).coordinate(fillDoorVariantMap(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, Properties.DOOR_HINGE, Properties.OPEN), LeftHingeClosedModelId, LeftHingeOpenModelId, RightHingeClosedModelId, RightHingeOpenModelId));
	}

	public static BlockStateVariantMap.TripleProperty<Direction, DoorHinge, Boolean> fillDoorVariantMap(BlockStateVariantMap.TripleProperty<Direction, DoorHinge, Boolean> variantMap, Identifier leftHingeClosedModelId, Identifier leftHingeOpenModelId, Identifier rightHingeClosedModelId, Identifier rightHingeOpenModelId) {
		return variantMap
				.register(Direction.WEST, DoorHinge.LEFT, false, BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeClosedModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180))
				.register(Direction.WEST, DoorHinge.LEFT, true, BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeOpenModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180))
				.register(Direction.NORTH, DoorHinge.LEFT, false, BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeClosedModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270))
				.register(Direction.NORTH, DoorHinge.LEFT, true, BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeOpenModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270))
				.register(Direction.EAST, DoorHinge.RIGHT, false, BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeClosedModelId))
				.register(Direction.EAST, DoorHinge.RIGHT, true, BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeOpenModelId))
				.register(Direction.SOUTH, DoorHinge.RIGHT, false, BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeClosedModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90))
				.register(Direction.SOUTH, DoorHinge.RIGHT, true, BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeOpenModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90))
				.register(Direction.WEST, DoorHinge.RIGHT, false, BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeClosedModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180))
				.register(Direction.WEST, DoorHinge.RIGHT, true, BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeOpenModelId).put(VariantSettings.Y, VariantSettings.Rotation.R180))
				.register(Direction.NORTH, DoorHinge.RIGHT, false, BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeClosedModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270))
				.register(Direction.NORTH, DoorHinge.RIGHT, true, BlockStateVariant.create().put(VariantSettings.MODEL, rightHingeOpenModelId).put(VariantSettings.Y, VariantSettings.Rotation.R270))
				.register(Direction.EAST, DoorHinge.LEFT, false, BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeClosedModelId))
				.register(Direction.EAST, DoorHinge.LEFT, true, BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeOpenModelId))
				.register(Direction.SOUTH, DoorHinge.LEFT, false, BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeClosedModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90))
				.register(Direction.SOUTH, DoorHinge.LEFT, true, BlockStateVariant.create().put(VariantSettings.MODEL, leftHingeOpenModelId).put(VariantSettings.Y, VariantSettings.Rotation.R90));
	}

	public static BlockStateVariantMap.DoubleProperty<Direction, Boolean> fillConnectedPillarVariantMap(BlockStateVariantMap.DoubleProperty<Direction, Boolean> variantMap, Identifier down, Identifier up) {
		return variantMap
				.register(Direction.WEST, true, BlockStateVariant.create().put(VariantSettings.MODEL, up).put(VariantSettings.Y, VariantSettings.Rotation.R270))
				.register(Direction.NORTH, true, BlockStateVariant.create().put(VariantSettings.MODEL, up).put(VariantSettings.Y, VariantSettings.Rotation.R0))
				.register(Direction.SOUTH, true, BlockStateVariant.create().put(VariantSettings.MODEL, up).put(VariantSettings.Y, VariantSettings.Rotation.R180))
				.register(Direction.EAST, true, BlockStateVariant.create().put(VariantSettings.MODEL, up).put(VariantSettings.Y, VariantSettings.Rotation.R90))

				.register(Direction.WEST, false, BlockStateVariant.create().put(VariantSettings.MODEL, down).put(VariantSettings.Y, VariantSettings.Rotation.R270))
				.register(Direction.NORTH, false, BlockStateVariant.create().put(VariantSettings.MODEL, down).put(VariantSettings.Y, VariantSettings.Rotation.R0))
				.register(Direction.SOUTH, false, BlockStateVariant.create().put(VariantSettings.MODEL, down).put(VariantSettings.Y, VariantSettings.Rotation.R180))
				.register(Direction.EAST, false, BlockStateVariant.create().put(VariantSettings.MODEL, down).put(VariantSettings.Y, VariantSettings.Rotation.R90));
	}

	public static BlockStateVariantMap.DoubleProperty<Direction, Boolean> fillLanternVariantMap(BlockStateVariantMap.DoubleProperty<Direction, Boolean> variantMap, Identifier down, Identifier up, Identifier side, Identifier down_off, Identifier up_off, Identifier side_off) {
		return variantMap
				.register(Direction.UP, true, BlockStateVariant.create().put(VariantSettings.MODEL, up))
				.register(Direction.DOWN, true, BlockStateVariant.create().put(VariantSettings.MODEL, down))
				.register(Direction.WEST, true, BlockStateVariant.create().put(VariantSettings.MODEL, side).put(VariantSettings.Y, VariantSettings.Rotation.R270))
				.register(Direction.NORTH, true, BlockStateVariant.create().put(VariantSettings.MODEL, side).put(VariantSettings.Y, VariantSettings.Rotation.R0))
				.register(Direction.SOUTH, true, BlockStateVariant.create().put(VariantSettings.MODEL, side).put(VariantSettings.Y, VariantSettings.Rotation.R180))
				.register(Direction.EAST, true, BlockStateVariant.create().put(VariantSettings.MODEL, side).put(VariantSettings.Y, VariantSettings.Rotation.R90))

				.register(Direction.UP, false, BlockStateVariant.create().put(VariantSettings.MODEL, up_off))
				.register(Direction.DOWN, false, BlockStateVariant.create().put(VariantSettings.MODEL, down_off))
				.register(Direction.WEST, false, BlockStateVariant.create().put(VariantSettings.MODEL, side_off).put(VariantSettings.Y, VariantSettings.Rotation.R270))
				.register(Direction.NORTH, false, BlockStateVariant.create().put(VariantSettings.MODEL, side_off).put(VariantSettings.Y, VariantSettings.Rotation.R0))
				.register(Direction.SOUTH, false, BlockStateVariant.create().put(VariantSettings.MODEL, side_off).put(VariantSettings.Y, VariantSettings.Rotation.R180))
				.register(Direction.EAST, false, BlockStateVariant.create().put(VariantSettings.MODEL, side_off).put(VariantSettings.Y, VariantSettings.Rotation.R90));
	}

	public static BlockStateVariantMap.DoubleProperty<Direction, SideShelfBlock.Model> fillConnectedBlockVariantMap(BlockStateVariantMap.DoubleProperty<Direction, SideShelfBlock.Model> variantMap, Identifier single, Identifier center, Identifier left, Identifier right) {
		var res = variantMap
				.register(Direction.WEST, SideShelfBlock.Model.SINGLE, BlockStateVariant.create().put(VariantSettings.MODEL, single).put(VariantSettings.Y, VariantSettings.Rotation.R270))
				.register(Direction.NORTH, SideShelfBlock.Model.SINGLE, BlockStateVariant.create().put(VariantSettings.MODEL, single).put(VariantSettings.Y, VariantSettings.Rotation.R0))
				.register(Direction.SOUTH, SideShelfBlock.Model.SINGLE, BlockStateVariant.create().put(VariantSettings.MODEL, single).put(VariantSettings.Y, VariantSettings.Rotation.R180))
				.register(Direction.EAST, SideShelfBlock.Model.SINGLE, BlockStateVariant.create().put(VariantSettings.MODEL, single).put(VariantSettings.Y, VariantSettings.Rotation.R90))

				.register(Direction.WEST, SideShelfBlock.Model.CENTER, BlockStateVariant.create().put(VariantSettings.MODEL, center).put(VariantSettings.Y, VariantSettings.Rotation.R270))
				.register(Direction.NORTH, SideShelfBlock.Model.CENTER, BlockStateVariant.create().put(VariantSettings.MODEL, center).put(VariantSettings.Y, VariantSettings.Rotation.R0))
				.register(Direction.SOUTH, SideShelfBlock.Model.CENTER, BlockStateVariant.create().put(VariantSettings.MODEL, center).put(VariantSettings.Y, VariantSettings.Rotation.R180))
				.register(Direction.EAST, SideShelfBlock.Model.CENTER, BlockStateVariant.create().put(VariantSettings.MODEL, center).put(VariantSettings.Y, VariantSettings.Rotation.R90))

				.register(Direction.WEST, SideShelfBlock.Model.LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, left).put(VariantSettings.Y, VariantSettings.Rotation.R270))
				.register(Direction.NORTH, SideShelfBlock.Model.LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, left).put(VariantSettings.Y, VariantSettings.Rotation.R0))
				.register(Direction.SOUTH, SideShelfBlock.Model.LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, left).put(VariantSettings.Y, VariantSettings.Rotation.R180))
				.register(Direction.EAST, SideShelfBlock.Model.LEFT, BlockStateVariant.create().put(VariantSettings.MODEL, left).put(VariantSettings.Y, VariantSettings.Rotation.R90))

				.register(Direction.WEST, SideShelfBlock.Model.RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, right).put(VariantSettings.Y, VariantSettings.Rotation.R270))
				.register(Direction.NORTH, SideShelfBlock.Model.RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, right).put(VariantSettings.Y, VariantSettings.Rotation.R0))
				.register(Direction.SOUTH, SideShelfBlock.Model.RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, right).put(VariantSettings.Y, VariantSettings.Rotation.R180))
				.register(Direction.EAST, SideShelfBlock.Model.RIGHT, BlockStateVariant.create().put(VariantSettings.MODEL, right).put(VariantSettings.Y, VariantSettings.Rotation.R90));

		return res;
	}

	public static BlockStateVariantMap.DoubleProperty<Direction, Boolean> fillToggleableVariantMap(BlockStateVariantMap.DoubleProperty<Direction, Boolean> variantMap, Identifier normal, Identifier toggled, boolean allrotation) {
		var res = variantMap
				.register(Direction.WEST, true, BlockStateVariant.create().put(VariantSettings.MODEL, toggled).put(VariantSettings.Y, VariantSettings.Rotation.R270))
				.register(Direction.NORTH, true, BlockStateVariant.create().put(VariantSettings.MODEL, toggled).put(VariantSettings.Y, VariantSettings.Rotation.R0))
				.register(Direction.SOUTH, true, BlockStateVariant.create().put(VariantSettings.MODEL, toggled).put(VariantSettings.Y, VariantSettings.Rotation.R180))
				.register(Direction.EAST, true, BlockStateVariant.create().put(VariantSettings.MODEL, toggled).put(VariantSettings.Y, VariantSettings.Rotation.R90))

				.register(Direction.WEST, false, BlockStateVariant.create().put(VariantSettings.MODEL, normal).put(VariantSettings.Y, VariantSettings.Rotation.R270))
				.register(Direction.NORTH, false, BlockStateVariant.create().put(VariantSettings.MODEL, normal).put(VariantSettings.Y, VariantSettings.Rotation.R0))
				.register(Direction.SOUTH, false, BlockStateVariant.create().put(VariantSettings.MODEL, normal).put(VariantSettings.Y, VariantSettings.Rotation.R180))
				.register(Direction.EAST, false, BlockStateVariant.create().put(VariantSettings.MODEL, normal).put(VariantSettings.Y, VariantSettings.Rotation.R90));
		if(allrotation) {
			res.register(Direction.UP, true, BlockStateVariant.create().put(VariantSettings.MODEL, toggled).put(VariantSettings.X, VariantSettings.Rotation.R270));
			res.register(Direction.DOWN, true, BlockStateVariant.create().put(VariantSettings.MODEL, toggled).put(VariantSettings.X, VariantSettings.Rotation.R90));
			res.register(Direction.UP, false, BlockStateVariant.create().put(VariantSettings.MODEL, normal).put(VariantSettings.X, VariantSettings.Rotation.R270));
			res.register(Direction.DOWN, false, BlockStateVariant.create().put(VariantSettings.MODEL, normal).put(VariantSettings.X, VariantSettings.Rotation.R90));
		}
		return res;
	}

	public static BlockStateVariantMap.DoubleProperty<Direction, Integer> fillMultistateVariantMap(BlockStateVariantMap.DoubleProperty<Direction, Integer> variantMap, Identifier base, int max) {
		for(int i = 1; i <= max; i++) {
			variantMap
					.register(Direction.WEST, i, BlockStateVariant.create().put(VariantSettings.MODEL, base.withSuffixedPath("_" + i)).put(VariantSettings.Y, VariantSettings.Rotation.R270))
					.register(Direction.NORTH, i, BlockStateVariant.create().put(VariantSettings.MODEL, base.withSuffixedPath("_" + i)).put(VariantSettings.Y, VariantSettings.Rotation.R0))
					.register(Direction.SOUTH, i, BlockStateVariant.create().put(VariantSettings.MODEL, base.withSuffixedPath("_" + i)).put(VariantSettings.Y, VariantSettings.Rotation.R180))
					.register(Direction.EAST, i, BlockStateVariant.create().put(VariantSettings.MODEL, base.withSuffixedPath("_" + i)).put(VariantSettings.Y, VariantSettings.Rotation.R90));
		}
		return variantMap;
	}

	public Identifier getBlockModelId(String baseID) {
		return new Identifier(GreenResurgence.ID, "block/" + this.subdomain + "/" + baseID);

	}

	public Identifier getBlockModelId(String baseID, String prefixPath) {
		return new Identifier(GreenResurgence.ID, "block/" + this.subdomain + "/" + prefixPath + baseID);

	}

	public SchematicBlockStateSupplier alterSchematic(Block b, String schemName, String key, String replace) {
		var m = new HashMap<String, String>();
		m.put(key, replace);
		return new SchematicBlockStateSupplier(b, schemName, m);
	}

	public SchematicBlockStateSupplier alterSchematic(Block b, String schemName, Map<String, String> keys) {
		return new SchematicBlockStateSupplier(b, schemName, keys);
	}

	public static JsonObject readDevModel(String name) {
		Gson gson = new Gson();
		Path path = LangGenerator.getDevPath(name);
		try(Reader reader = Files.newBufferedReader(path)) {
			return gson.fromJson(reader, JsonObject.class);
		} catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
