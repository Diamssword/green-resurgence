package com.diamssword.greenresurgence.render.cosmetics;

import com.diamssword.greenresurgence.http.APIService;
import com.diamssword.greenresurgence.mixin.client.PlayerSkinProviderAcessor;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.render.images.TextureCache;
import com.diamssword.greenresurgence.systems.character.SkinServerCache;
import com.diamssword.greenresurgence.textures.B64PlayerSkinTexture;
import com.google.common.hash.Hashing;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Consumer;

public class SkinsLoader {
	public static final SkinServerCache clientSkinCache = new SkinServerCache();
	private final MinecraftSessionService sessionService = MinecraftClient.getInstance().getSessionService();
	private final Set<UUID> needReload = new HashSet<>();
	private final Set<UUID> requested = new HashSet<>();
	private final File cacheDir;
	public static SkinsLoader instance = new SkinsLoader();

	public boolean doesNeedReload(UUID playerid) {
		return needReload.contains(playerid);
	}

	public boolean markReload(UUID playerid, boolean needed) {
		if (needed) {
			this.requested.remove(playerid);
			this.needReload.add(playerid);
		} else {
			if (needReload.contains(playerid)) {
				this.needReload.remove(playerid);
				return true;
			}
		}
		return false;
	}

	public SkinsLoader() {
		cacheDir = ((PlayerSkinProviderAcessor) MinecraftClient.getInstance().getSkinProvider()).getCacheDir();
	}

	public static void loadHead(UUID playerID, Consumer<Identifier> callback) {
		TextureCache.instance().getImage(APIService.url + "/files/skin/" + playerID.toString().replaceAll("-", "") + "_head.png", callback);
	}

	public void loadSkinNew(GameProfile profile, SkinTextureAvailableCallback callback) {
		var force = markReload(profile.getId(), false);
		Runnable runnable = () -> {
			MinecraftClient.getInstance().execute(() -> {
				RenderSystem.recordRenderCall(() -> {
					var skin = clientSkinCache.getSkin(profile.getId());
					if (skin.isPresent()) {
						requested.remove(profile.getId());
						var map1 = new HashMap<String, String>();
						map1.put("slim", skin.get().slim() ? "true" : "false");
						//  CustomPlayerModel.playerProps.put(profile.getId(),new CustomPlayerModel.PlayerProps(map1));
						this.loadSkin(new B64MinecraftProfileTexture(skin.get().skin(), map1), callback, force);

					} else {
						if (!requested.contains(profile.getId())) {
							Channels.MAIN.clientHandle().send(new SkinServerCache.RequestPlayerInfos(profile.getId()));
							requested.add(profile.getId());
						}
					}
				});
			});
		};
		Util.getMainWorkerExecutor().execute(runnable);
	}

	public void loadSkin(GameProfile profile, PlayerSkinProvider.SkinTextureAvailableCallback callback, boolean requireSecure) {
		var force = markReload(profile.getId(), false);
		Runnable runnable = () -> {
			MinecraftClient.getInstance().execute(() -> {
				RenderSystem.recordRenderCall(() -> {
					var partUrl = APIService.url + "/files/skin/" + profile.getId().toString().replaceAll("-", "");
					APIService.getRequest(partUrl + ".json", "").thenAccept(d -> {
						if (d.statusCode() == 200) {
							var datas = JsonParser.parseString(d.body()).getAsJsonObject();
							var map1 = new HashMap<String, String>();
							datas.keySet().forEach(v -> {
								map1.put(v, datas.get(v).getAsString());
							});
							//  CustomPlayerModel.playerProps.put(profile.getId(),new CustomPlayerModel.PlayerProps(map1));
							this.loadSkin(new MinecraftProfileTexture(partUrl + ".png", map1), MinecraftProfileTexture.Type.SKIN, callback, force);

						}
					});

				});
			});
		};
		Util.getMainWorkerExecutor().execute(runnable);
	}

	private Identifier loadSkin(MinecraftProfileTexture profileTexture, MinecraftProfileTexture.Type type, @Nullable PlayerSkinProvider.SkinTextureAvailableCallback callback, boolean force) {

		String string = Hashing.sha1().hashUnencodedChars(profileTexture.getHash()).toString();
		Identifier identifier = getSkinId(type, string);
		AbstractTexture abstractTexture = MinecraftClient.getInstance().getTextureManager().getOrDefault(identifier, MissingSprite.getMissingSpriteTexture());
		if (force || abstractTexture == MissingSprite.getMissingSpriteTexture()) {
			File file = new File(cacheDir, string.length() > 2 ? string.substring(0, 2) : "xx");
			File file2 = new File(file, string);
			if (force && file2.exists())
				file2.delete();
			PlayerSkinTexture playerSkinTexture = new PlayerSkinTexture(file2, profileTexture.getUrl(), DefaultSkinHelper.getTexture(), false, () -> {
				if (callback != null) {
					callback.onSkinTextureAvailable(type, identifier, profileTexture);
				}

			});
			MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, playerSkinTexture);
		} else if (callback != null) {
			callback.onSkinTextureAvailable(type, identifier, profileTexture);
		}

		return identifier;
	}

	private Identifier loadSkin(B64MinecraftProfileTexture profileTexture, @Nullable SkinTextureAvailableCallback callback, boolean force) {

		String string = Hashing.sha1().hashUnencodedChars(profileTexture.getHash()).toString();
		Identifier identifier = getSkinId(MinecraftProfileTexture.Type.SKIN, string);
		AbstractTexture abstractTexture = MinecraftClient.getInstance().getTextureManager().getOrDefault(identifier, MissingSprite.getMissingSpriteTexture());
		if (force || abstractTexture == MissingSprite.getMissingSpriteTexture()) {
			File file = new File(cacheDir, string.length() > 2 ? string.substring(0, 2) : "xx");
			File file2 = new File(file, string);
			if (force && file2.exists())
				file2.delete();
			B64PlayerSkinTexture playerSkinTexture = new B64PlayerSkinTexture(file2, profileTexture.getData(), DefaultSkinHelper.getTexture(), () -> {
				if (callback != null) {
					callback.onSkinTextureAvailable(MinecraftProfileTexture.Type.SKIN, identifier, profileTexture);
				}

			});
			MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, playerSkinTexture);
		} else if (callback != null) {
			callback.onSkinTextureAvailable(MinecraftProfileTexture.Type.SKIN, identifier, profileTexture);
		}

		return identifier;
	}

	@Environment(EnvType.CLIENT)
	public interface SkinTextureAvailableCallback {
		void onSkinTextureAvailable(MinecraftProfileTexture.Type type, Identifier id, B64MinecraftProfileTexture texture);
	}

	private static Identifier getSkinId(MinecraftProfileTexture.Type skinType, String hash) {
		String var10000;
		switch (skinType) {
			case SKIN:
				var10000 = "skins";
				break;
			case CAPE:
				var10000 = "capes";
				break;
			case ELYTRA:
				var10000 = "elytra";
				break;
			default:
				throw new IncompatibleClassChangeError();
		}

		String string = var10000;
		return new Identifier(string + "/" + hash);
	}

	public class B64MinecraftProfileTexture {


		private final String data;
		private final Map<String, String> metadata;

		public B64MinecraftProfileTexture(final String data, final Map<String, String> metadata) {
			this.data = data;
			this.metadata = metadata;
		}

		public String getData() {
			return data;
		}

		@Nullable
		public String getMetadata(final String key) {
			if (metadata == null) {
				return null;
			}
			return metadata.get(key);
		}

		public String getHash() {
			try {
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
				return bytesToHex(hashBytes);
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException("SHA-256 not supported", e);
			}
		}

		// Convertit un tableau de bytes en hexadécimal
		private static String bytesToHex(byte[] bytes) {
			StringBuilder hexString = new StringBuilder(2 * bytes.length);
			for (byte b : bytes) {
				hexString.append(String.format("%02x", b));
			}
			return hexString.toString();
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this)
					.append("base64", data)
					.append("hash", getHash())
					.toString();
		}
	}
}

