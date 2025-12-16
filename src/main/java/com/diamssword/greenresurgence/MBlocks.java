package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.blockEntities.ModBlockEntity;
import com.diamssword.greenresurgence.blocks.*;
import com.diamssword.greenresurgence.datagen.BlockLootGenerator;
import com.diamssword.greenresurgence.datagen.ModelGenerator;
import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public class MBlocks implements BlockRegistryContainer {

	@NoItemGroup
	public static final LootedBlock LOOTED_BLOCK = new LootedBlock(AbstractBlock.Settings.create().nonOpaque().dropsNothing().strength(99999, 99999).suffocates(Blocks::never));
	@DiamsGroup
	public static final ConnectorBlock CONNECTOR = new ConnectorBlock(AbstractBlock.Settings.create().nonOpaque().strength(99999, 99999).suffocates(Blocks::never), new Vec3d(0, 0.25, 0));
	@DiamsGroup
	public static final ConnectorBlock ELECTRICAL_POWER_COIL = new ConnectorBlock(AbstractBlock.Settings.create().nonOpaque().strength(99999, 99999).suffocates(Blocks::never), new Vec3d(0, 0.8, 0));

	@DiamsGroup
	public static final MetroCorridorFull METRO_CORRIDOR = new MetroCorridorFull(AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL), 0);
	@DiamsGroup
	public static final MetroCorridorFull METRO_CORRIDOR_SLAB = new MetroCorridorFull(AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL), 1);
	@DiamsGroup
	public static final MetroCorridorFull METRO_CORRIDOR_SLAB_INVERT = new MetroCorridorFull(AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL), 2);
	@DiamsGroup
	public static final LayerModelBlock CEREALS = new LayerModelBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.CANDLE)) {
		public int layers() {
			return 10;
		}
	};
	@DiamsGroup
	public static final LayerModelBlock TRASH_BAGS = new LayerModelBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.CANDLE)) {
		public int layers() {
			return 10;
		}
	};
	@DiamsGroup
	public static final LayerModelBlock BOOK_VERTICAL_A = new LayerModelBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.CANDLE)) {
		public int layers() {
			return 6;
		}
	};
	@DiamsGroup
	public static final LayerModelBlock BOOK_VERTICAL_B = new LayerModelBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.CANDLE)) {
		public int layers() {
			return 6;
		}
	};
	@DiamsGroup
	public static final LayerModelBlock MILK_CARDBOARD = new LayerModelBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.CANDLE)) {
		public int layers() {
			return 10;
		}
	};
	//public static final StructureBlock STRUCTURE_BLOCK =new StructureBlock(FabricBlockSettings.create().resistance(20000).solidBlock((_1, __, ___)->false).nonOpaque());
	//public static final SmartStructureBlock STRUCTURE_BLOCK_SMART =new SmartStructureBlock(FabricBlockSettings.create().resistance(20000).solidBlock((_1, __, ___)->false).nonOpaque());
	public static final ItemBlock ITEM_BLOCK = new ItemBlock(FabricBlockSettings.create().resistance(20000).solidBlock((_1, __, ___) -> false).nonOpaque());
	public static final LootableItemBlock LOOT_ITEM_BLOCK = new LootableItemBlock(FabricBlockSettings.create().resistance(20000).solidBlock((_1, __, ___) -> false).nonOpaque());
	@DiamsGroup
	public static final ShelfBlock SHELF_BLOCK = new ShelfBlock(FabricBlockSettings.create().resistance(20000).solidBlock((_1, __, ___) -> false).nonOpaque(), false) {
		@Override
		public boolean hasBottomLogic() {
			return true;
		}

	};
	@DiamsGroup
	public static final ShelfBlock SIDEWAY_SHELF_BLOCK = new SideShelfBlock(FabricBlockSettings.create().resistance(20000).solidBlock((_1, __, ___) -> false).nonOpaque());
	@DiamsGroup
	public static final ShelfBlock WOOD_CRATE_SHELF_BLOCK = new SideShelfBlock(FabricBlockSettings.create().resistance(20000).solidBlock((_1, __, ___) -> false).nonOpaque());
	@DiamsGroup
	public static final ShelfBlock ICE_COOLER_SHELF_RIGHT = new ShelfBlock(FabricBlockSettings.create().resistance(20000).solidBlock((_1, __, ___) -> false).nonOpaque(), true);
	@DiamsGroup
	public static final ShelfBlock ICE_COOLER_SHELF_LEFT = new ShelfBlock(FabricBlockSettings.create().resistance(20000).solidBlock((_1, __, ___) -> false).nonOpaque(), true);
	@DiamsGroup
	public static final ShelfBlock PLASTIC_TRASH_CAN_BROWN = new ShelfBlock(FabricBlockSettings.create().resistance(20000).solidBlock((_1, __, ___) -> false).nonOpaque(), true);
	public static final ImageBlock IMAGE_BLOCK = new ImageBlock(FabricBlockSettings.create().resistance(20000).solidBlock((_1, __, ___) -> false).nonOpaque());

	public static final Block SHADOW_BLOCk = new TintedGlassBlock(FabricBlockSettings.create().nonOpaque().strength(-1.0F, 3600000.0F).dropsNothing().allowsSpawning(Blocks::never)) {
		public BlockRenderType getRenderType(BlockState state) {
			return BlockRenderType.INVISIBLE;
		}
	};
	@ModelGen
	public static final BaseStorageBlock BASE_CRATE_T1 = new BaseStorageBlock(FabricBlockSettings.create().resistance(20000).sounds(BlockSoundGroup.WOOD), 9);
	@ModelGen
	public static final BaseStorageBlock BASE_CRATE_T2 = new BaseStorageBlock(FabricBlockSettings.create().resistance(20000).sounds(BlockSoundGroup.WOOD), 18);
	@ModelGen
	public static final CrafterBlock CRAFTER = new CrafterBlock();
	@ModelGen
	public static final GeneratorBlock GENERATOR_T1 = new GeneratorBlock(FabricBlockSettings.create());
	@NoItemGroup
	public static final DeployableLadderBlock DEPLOYABLE_LADDER = new DeployableLadderBlock(FabricBlockSettings.create().resistance(20000).sounds(BlockSoundGroup.WOOL));
	@ModelGen
	public static final ClaimBlock NANOTEK_GENERATOR_RELAY = new ClaimBlock(FabricBlockSettings.create().nonOpaque().resistance(100).sounds(BlockSoundGroup.METAL));
	@ModelGen
	public static final NanoGeneratorBlock NANOTEK_GENERATOR_BIG_ANTENNA = new NanoGeneratorBlock(FabricBlockSettings.create().nonOpaque().resistance(100).sounds(BlockSoundGroup.METAL), NanoGeneratorBlock.HITBOX.small);
	@ModelGen
	public static final NanoGeneratorBlock NANOTEK_GENERATOR_CANISTER = new NanoGeneratorBlock(FabricBlockSettings.create().nonOpaque().resistance(100).sounds(BlockSoundGroup.AMETHYST_BLOCK), NanoGeneratorBlock.HITBOX.side);
	@ModelGen
	public static final NanoGeneratorBlock NANOTEK_GENERATOR_COMPUTER = new NanoGeneratorBlock(FabricBlockSettings.create().nonOpaque().resistance(100).sounds(BlockSoundGroup.METAL), NanoGeneratorBlock.HITBOX.big);
	@ModelGen
	public static final NanoGeneratorBlock NANOTEK_GENERATOR_PILLAR = new NanoGeneratorBlock(FabricBlockSettings.create().nonOpaque().resistance(100).sounds(BlockSoundGroup.METAL), NanoGeneratorBlock.HITBOX.pole);
	@ModelGen
	public static final NanoGeneratorBlock NANOTEK_GENERATOR_SERVER = new NanoGeneratorBlock(FabricBlockSettings.create().nonOpaque().resistance(100).sounds(BlockSoundGroup.METAL), NanoGeneratorBlock.HITBOX.side_revert);
	@ModelGen
	public static final NanoGeneratorBlock NANOTEK_GENERATOR_SMALL_ANTENNA = new NanoGeneratorBlock(FabricBlockSettings.create().nonOpaque().resistance(100).sounds(BlockSoundGroup.METAL), NanoGeneratorBlock.HITBOX.pole);
	@ModelGen
	@NoDrop
	public static final ArmorTinkererBlock ARMOR_TINKERER = new ArmorTinkererBlock(AbstractBlock.Settings.create().nonOpaque().strength(1, 100).suffocates(Blocks::never));
	@ModelGen
	public static final EquipmentTinkererBlock EQUIPMENT_TINKERER = new EquipmentTinkererBlock(AbstractBlock.Settings.create().nonOpaque().strength(1, 100).suffocates(Blocks::never));
	@ModelGen
	public static final CrumbelingBlock CRUMBELING_BLOCK = new CrumbelingBlock(AbstractBlock.Settings.create().nonOpaque().dropsNothing().strength(99999, 99999).suffocates(Blocks::never));
	@ModelGen
	public static final CameleonBlock CAMO_WALL = new CameleonBlock(true);
	@ModelGen
	public static final CameleonBlock CAMO_FLOOR = new CameleonBlock(false);

	@Override
	public void afterFieldProcessing() {
	}

	@Override
	public void postProcessField(String namespace, Block value, String identifier, Field field) {
		if(value instanceof ModBlockEntity<?> be) {
			MBlockEntities.addToRegister(be);
		}
		// preserve normal traversal behaviour
		if(field.isAnnotationPresent(NoItemGroup.class)) return;
		Item i;
		if(field.isAnnotationPresent(DiamsGroup.class))
			i = new BlockItem(value, new OwoItemSettings().group(GenericBlocks.GENERIC_GROUP).tab(1));
		else
			i = new BlockItem(value, new OwoItemSettings().group(MItems.GROUP));
		if(field.isAnnotationPresent(ModelGen.class)) {
			ModelGenerator.blockItems.put(new Identifier(namespace, identifier), i);
		}
		Registry.register(Registries.ITEM, new Identifier(namespace, identifier), i);
		if(FabricLoader.getInstance().isDevelopmentEnvironment()) {
			if(!field.isAnnotationPresent(NoDrop.class))
				BlockLootGenerator.blocks.add(value);
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface NoItemGroup {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface DiamsGroup {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface NoDrop {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface ModelGen {
	}
}
