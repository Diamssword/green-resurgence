package com.diamssword.greenresurgence.mixin.client;

import com.diamssword.greenresurgence.render.cosmetics.SkinsLoader;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {
	@Shadow
	private boolean texturesLoaded;
	@Shadow
	@Final
	private final Map<MinecraftProfileTexture.Type, Identifier> textures = Maps.newEnumMap(MinecraftProfileTexture.Type.class);
	@Shadow
	private String model;


	/*@Inject(at = @At("HEAD"), method = "getModel",cancellable = true)
	public void getModel(CallbackInfoReturnable<String> cir) {
		if (this.model == null) {
			cir.setReturnValue("default");
		}
		else
			cir.setReturnValue(this.model);
	}
*/
	@Shadow
	public abstract GameProfile getProfile();

	@Shadow
	@Final
	private GameProfile profile;

	@Inject(at = @At("HEAD"), method = "loadTextures", cancellable = true)
	protected void loadTextures(CallbackInfo ci) {
		synchronized (this) {
			if (!this.texturesLoaded || SkinsLoader.instance.doesNeedReload(this.getProfile().getId())) {
				this.texturesLoaded = true;
				SkinsLoader.instance.loadSkinNew(this.getProfile(), (type, id, texture) -> {
					this.textures.put(type, id);
					if (type == MinecraftProfileTexture.Type.SKIN) {
						this.model = "true".equals(texture.getMetadata("slim")) ? "slim" : "default";

					}

				});
			}
			ci.cancel();
		}
	}
}
