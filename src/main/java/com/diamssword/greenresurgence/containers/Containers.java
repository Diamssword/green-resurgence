package com.diamssword.greenresurgence.containers;

import com.diamssword.greenresurgence.blockEntities.ArmorTinkererBlockEntity;
import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import com.diamssword.greenresurgence.blocks.BaseStorageBlock;
import com.diamssword.greenresurgence.blocks.CrafterBlock;
import com.diamssword.greenresurgence.blocks.ItemBlock;
import com.diamssword.greenresurgence.blocks.ShelfBlock;
import com.diamssword.greenresurgence.containers.player.VanillaPlayerInvMokup;
import com.diamssword.greenresurgence.items.BlockVariantItem;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionTerrainStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class Containers implements ContainerRegistryContainer {
	//public static final ScreenHandlerType<MutliInvScreenHandler> RELATIVE = build(MutliInvScreenHandler::new);
	public static final ScreenHandlerType<ItemBlock.ScreenHandler> ITEMBLOCK = build(ItemBlock.ScreenHandler::new);
	public static final ScreenHandlerType<ShelfBlock.ScreenHandler> ITEMBLOCKSIMPLE = build(ShelfBlock.ScreenHandler::new);
	public static final ScreenHandlerType<LootedBlockEntity.Container> LOOTABLE_INV = build(LootedBlockEntity.Container::new);
	public static final ScreenHandlerType<ArmorTinkererBlockEntity.Container> ARMOR_TINKERER = build(ArmorTinkererBlockEntity.Container::new);
	public static final ScreenHandlerType<BlockVariantItem.Container> BLOCK_VARIANT_INV = build(BlockVariantItem.Container::new);
	public static final ScreenHandlerType<CrafterBlock.ScreenHandler> CRAFTER = build(CrafterBlock.ScreenHandler::new);
	public static final ScreenHandlerType<FactionTerrainStorage.ScreenHandler> FAC_STORAGE = build(FactionTerrainStorage.ScreenHandler::new);
	public static final ScreenHandlerType<BaseStorageBlock.ScreenHandler> FAC_CHEST = build(BaseStorageBlock.ScreenHandler::new);
	public static final ScreenHandlerType<GenericContainer> GENERIC_CONTAINER = build(GenericContainer::new);
	public static final ScreenHandlerType<VanillaPlayerInvMokup> PLAYER = build(VanillaPlayerInvMokup::new);
	public static final ScreenHandlerType<EquipmentScreenHandler> EQUIPMENT_TINKERER = build(EquipmentScreenHandler::new);

	private static <T extends ScreenHandler> ScreenHandlerType<T> build(ScreenHandlerType.Factory<T> factory) {
		return new ScreenHandlerType<T>(factory, FeatureFlags.VANILLA_FEATURES);
	}

	public static void createHandler(PlayerEntity player, BlockPos pos, HandlerFactory factory) {
		NamedScreenHandlerFactory screen = new NamedScreenHandlerFactory() {
			@Nullable
			@Override
			public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
				var handler = factory.create(syncId, playerInventory, player);
				handler.setPos(pos);
				return handler;
			}

			@Override
			public Text getDisplayName() {
				return Text.of("");
			}
		};
		player.openHandledScreen(screen);
	}

	@FunctionalInterface
	public interface HandlerFactory {
		AbstractMultiInvScreenHandler create(int syncId, PlayerInventory inventory, PlayerEntity player);
	}

}
