package com.diamssword.greenresurgence.cosmetics;

import com.diamssword.greenresurgence.http.APIService;
import com.diamssword.greenresurgence.mixin.client.PlayerSkinProviderAcessor;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Debug;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class SkinsLoader {
    private final MinecraftSessionService sessionService=MinecraftClient.getInstance().getSessionService();
    private final Set<UUID> needReload=new HashSet<>();
    private final File cacheDir;
    public static SkinsLoader instance=new SkinsLoader();

    public boolean doesNeedReload(UUID playerid)
    {
        return needReload.contains(playerid);
    }
    public boolean markReload(UUID playerid,boolean needed)
    {
        if(needed)
            this.needReload.add(playerid);
        else
        {
            if(needReload.contains(playerid))
            {
                this.needReload.remove(playerid);
                return true;
            }
        }
            return false;
    }
    public SkinsLoader()
    {
        cacheDir = ((PlayerSkinProviderAcessor) MinecraftClient.getInstance().getSkinProvider()).getCacheDir();
    }
    public void loadSkin(GameProfile profile, PlayerSkinProvider.SkinTextureAvailableCallback callback, boolean requireSecure) {
      var force=  markReload(profile.getId(),false);
        Runnable runnable = () -> {
            MinecraftClient.getInstance().execute(() -> {
                RenderSystem.recordRenderCall(() -> {
                    var partUrl=APIService.url+"/files/skin/"+profile.getId().toString().replaceAll("-","");
                    APIService.getRequest(partUrl+".json","").thenAccept(d->{
                        if(d.statusCode()==200) {
                            var datas=JsonParser.parseString(d.body()).getAsJsonObject();
                            var map1=new HashMap<String,String>();
                            datas.keySet().forEach(v->{
                                map1.put(v,datas.get(v).getAsString());
                            });
                          //  CustomPlayerModel.playerProps.put(profile.getId(),new CustomPlayerModel.PlayerProps(map1));
                            this.loadSkin(new MinecraftProfileTexture(partUrl + ".png",map1), MinecraftProfileTexture.Type.SKIN, callback, force);

                        }
                    });

                });
            });
        };
        Util.getMainWorkerExecutor().execute(runnable);
    }
    private Identifier loadSkin(MinecraftProfileTexture profileTexture, MinecraftProfileTexture.Type type, @Nullable PlayerSkinProvider.SkinTextureAvailableCallback callback,boolean force) {

        String string = Hashing.sha1().hashUnencodedChars(profileTexture.getHash()).toString();
        Identifier identifier = getSkinId(type, string);
        AbstractTexture abstractTexture = MinecraftClient.getInstance().getTextureManager().getOrDefault(identifier, MissingSprite.getMissingSpriteTexture());
        if (force || abstractTexture == MissingSprite.getMissingSpriteTexture()) {
            File file = new File(cacheDir, string.length() > 2 ? string.substring(0, 2) : "xx");
            File file2 = new File(file, string);
            if(force && file2.exists())
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

}
