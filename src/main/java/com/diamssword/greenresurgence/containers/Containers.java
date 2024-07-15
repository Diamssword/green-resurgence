package com.diamssword.greenresurgence.containers;

import com.diamssword.greenresurgence.blockEntities.LootedBlockEntity;
import com.diamssword.greenresurgence.blocks.ItemBlock;
import com.diamssword.greenresurgence.items.BlockVariantItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class Containers implements ContainerRegistryContainer {
   public static final ScreenHandlerType<GenericScreenHandler> GENERIC = build(GenericScreenHandler::new);
    //public static final ScreenHandlerType<MutliInvScreenHandler> RELATIVE = build(MutliInvScreenHandler::new);
    public static final ScreenHandlerType<ItemBlock.ScreenHandler> ITEMBLOCK = build(ItemBlock.ScreenHandler::new);
    public static final ScreenHandlerType<LootedBlockEntity.Container> LOOTABLE_INV = build(LootedBlockEntity.Container::new);
    public static final ScreenHandlerType<BlockVariantItem.Container> BLOCK_VARIANT_INV = build(BlockVariantItem.Container::new);

    private static <T extends ScreenHandler> ScreenHandlerType<T> build(ScreenHandlerType.Factory<T> factory) {
        return new ScreenHandlerType<T>(factory, FeatureFlags.VANILLA_FEATURES);
    }
    public static void createHandler(PlayerEntity player, BlockPos pos,HandlerFactory factory)
    {
        NamedScreenHandlerFactory screen=new NamedScreenHandlerFactory() {
            @Nullable
            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                var handler=factory.create(syncId,playerInventory,player);
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
    public static interface HandlerFactory
    {
          MultiInvScreenHandler create(int syncId,PlayerInventory inventory,PlayerEntity player);
    }
}
