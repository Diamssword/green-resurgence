package com.diamssword.greenresurgence.render.images;

import com.diamssword.greenresurgence.GreenResurgence;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.DynamicTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec2f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TextureCache {

    private Map<String, Identifier> clientResourceCache;
    private Map<String, Long> waitingCache;
    private Map<Identifier, Pair<Integer,Integer>> ratioCache;

    public static TextureCache instance;

    public TextureCache() {
        waitingCache=new HashMap<>();
        ratioCache=new HashMap<>();
        clientResourceCache = new HashMap<>();
    }
    public void loadImageFromUrl(String url)
    {
        waitingCache.put(url,System.currentTimeMillis());
        new Thread(()->{
            try {
                addImage(url, ImageIO.read(new URL(url)));
            } catch (IOException e) {
                GreenResurgence.LOGGER.error("Could not read image from url: "+url);
            }
        }).start();
    }
    public void addImage(String url, BufferedImage image) {
        if(image ==null)
        {
            GreenResurgence.LOGGER.error("Received null image from url: "+url);
            return;
        }
        Identifier resourceLocation = GreenResurgence.asRessource("texures/images/" + UUID.randomUUID());
        ImageTextureObject cameraTextureObject = new ImageTextureObject(ImageTools.toNativeImage(image));
        ratioCache.put(resourceLocation, new Pair<>(image.getWidth(),image.getHeight()));
        clientResourceCache.put(url, resourceLocation);
        MinecraftClient.getInstance().getEntityRenderDispatcher().textureManager.registerTexture(resourceLocation, cameraTextureObject);

        waitingCache.remove(url);
    }

    public Identifier getImage(String url) {
        Identifier id=clientResourceCache.get(url);
        if(id ==null && url !=null && url.length()>1)
        {
            if(waitingCache.containsKey(url))
            {
                if(waitingCache.get(url)+10_000<System.currentTimeMillis())
                    waitingCache.remove(url);
            }
            else
                loadImageFromUrl(url);
        }
        return id;
    }
    public Pair<Integer,Integer> getSize(Identifier id)
    {
        return ratioCache.get(id);
    }

    public class ImageTextureObject extends NativeImageBackedTexture {

        public ImageTextureObject(NativeImage image) {
            super(image);
        }
    }

    public static TextureCache instance() {
        if (instance == null) {
            instance = new TextureCache();
        }
        return instance;
    }

}