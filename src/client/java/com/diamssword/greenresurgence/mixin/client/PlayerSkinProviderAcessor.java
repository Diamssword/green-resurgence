package com.diamssword.greenresurgence.mixin.client;

import net.minecraft.client.texture.PlayerSkinProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;

@Mixin(PlayerSkinProvider.class)
public interface PlayerSkinProviderAcessor {
  @Accessor("skinCacheDir")
  public File getCacheDir();
}