package com.diamssword.greenresurgence.containers;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;

public interface ContainerRegistryContainer extends AutoRegistryContainer<ScreenHandlerType<?>> {

        @Override
        default Registry<ScreenHandlerType<?>> getRegistry() {
            return Registries.SCREEN_HANDLER;
        }

        @Override
        default Class<ScreenHandlerType<?>> getTargetFieldType() {
            return AutoRegistryContainer.conform(ScreenHandlerType.class);
        }
    }