package com.diamssword.greenresurgence.containers;

import com.diamssword.greenresurgence.blocks.ItemBlock;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class Containers implements ContainerRegistryContainer {
   public static final ScreenHandlerType<GenericScreenHandler> GENERIC = build(GenericScreenHandler::new);
    //public static final ScreenHandlerType<MutliInvScreenHandler> RELATIVE = build(MutliInvScreenHandler::new);
    public static final ScreenHandlerType<ItemBlock.ScreenHandler> ITEMBLOCK = build(ItemBlock.ScreenHandler::new);

    private static <T extends ScreenHandler> ScreenHandlerType<T> build(ScreenHandlerType.Factory<T> factory) {
        return new ScreenHandlerType<T>(factory, FeatureFlags.VANILLA_FEATURES);
    }
}
