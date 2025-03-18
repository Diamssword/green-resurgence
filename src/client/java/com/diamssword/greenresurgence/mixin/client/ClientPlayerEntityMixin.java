package com.diamssword.greenresurgence.mixin.client;

import com.diamssword.greenresurgence.containers.player.CustomPlayerInventory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public interface ClientPlayerEntityMixin {

    @Mutable
    @Accessor("riding") // Name must match the private field in LivingEntity
    void setRiding(boolean riding);

}
