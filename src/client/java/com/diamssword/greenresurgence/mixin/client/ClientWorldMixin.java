package com.diamssword.greenresurgence.mixin.client;

import com.diamssword.greenresurgence.GreenResurgenceClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
  @Mutable
  @Shadow
  @Final
  private static Set<Item> BLOCK_MARKER_ITEMS;

  @Inject(method = "<clinit>", at = @At("TAIL"))
  private static void init(CallbackInfo ci) {
    var ls=new ArrayList<>(BLOCK_MARKER_ITEMS);
    ls.addAll(List.of(GreenResurgenceClient.MARKER_ITEMS));
    BLOCK_MARKER_ITEMS=Set.of(ls.toArray(new Item[0]));
  }
}