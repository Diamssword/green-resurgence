package com.diamssword.greenresurgence.render.images;

import com.diamssword.greenresurgence.GreenResurgence;
import net.minecraft.client.texture.NativeImage;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.UUID;
import java.util.function.Consumer;

public class ImageTools {


    private static final int MAX_IMAGE_SIZE = 1920;

    public static BufferedImage fromNativeImage(NativeImage nativeImage) {
        BufferedImage bufferedImage = new BufferedImage(nativeImage.getWidth(), nativeImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < nativeImage.getWidth(); x++) {
            for (int y = 0; y < nativeImage.getHeight(); y++) {
                int rgba = nativeImage.getColor(x, y);
                int alpha = RenderUtils.getAlpha(rgba);
                int red = RenderUtils.getRed(rgba);
                int green = RenderUtils.getGreen(rgba);
                int blue = RenderUtils.getBlue(rgba);
                bufferedImage.setRGB(x, y, RenderUtils.getArgb(alpha, blue, green, red));
            }
        }

        return bufferedImage;
    }

    public static NativeImage toNativeImage(BufferedImage bufferedImage) {
        NativeImage nativeImage = new NativeImage(bufferedImage.getWidth(), bufferedImage.getHeight(), false);
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                int rgba = bufferedImage.getRGB(x, y);
                int alpha = RenderUtils.getAlpha(rgba);
                int red = RenderUtils.getRed(rgba);
                int green = RenderUtils.getGreen(rgba);
                int blue = RenderUtils.getBlue(rgba);
                nativeImage.setColor(x, y, RenderUtils.getArgb(alpha, blue, green, red));
            }
        }
        return nativeImage;
    }

    public static byte[] toBytes(BufferedImage image) throws IOException {
        ImageIO.setUseCache(false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        baos.flush();
        byte[] data = baos.toByteArray();
        baos.close();
        return data;
    }

    public static BufferedImage fromBytes(byte[] data) throws IOException {
        ImageIO.setUseCache(false);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        BufferedImage image = ImageIO.read(bais);
        bais.close();
        return image;
    }

    public static byte[] optimizeImage(BufferedImage image) throws IOException {
        float ratio = ((float) image.getHeight()) / ((float) image.getWidth());
        int newWidth = image.getWidth();
        int newHeight = image.getHeight();

        if (image.getHeight() > MAX_IMAGE_SIZE || image.getWidth() > MAX_IMAGE_SIZE) {
            if (ratio < 1F) {
                newHeight = ((int) (((float) MAX_IMAGE_SIZE) * ratio));
                newWidth = MAX_IMAGE_SIZE;
            } else {
                newWidth = ((int) (((float) MAX_IMAGE_SIZE) / ratio));
                newHeight = MAX_IMAGE_SIZE;
            }
        }

        image = ImageTools.resize(image, newWidth, newHeight);

        float factor = 0.5f;// Main.SERVER_CONFIG.imageCompression.get().floatValue();
        byte[] data;

        while ((data = ImageTools.compressToBytes(image, factor)).length >200_000) { // Main.SERVER_CONFIG.maxImageSize.get()
            GreenResurgence.LOGGER.debug("Trying to compress image: {}% {} bytes (max {})", Math.round(factor * 100F), data.length, 200_000);
            factor -= 0.025F;
            if (factor <= 0F) {
                throw new IOException("Image could not be compressed (too large)");
            }
        }

        GreenResurgence.LOGGER.debug("Image compressed to {}% ({} bytes)", Math.round(factor * 100F), data.length);

        return data;
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage bufferedImage = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return bufferedImage;
    }

    public static BufferedImage compress(BufferedImage img, float factor) throws IOException {
        return fromBytes(compressToBytes(img, factor));
    }

    public static byte[] compressToBytes(BufferedImage img, float factor) throws IOException {
        ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
        jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpgWriteParam.setCompressionQuality(factor);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MemoryCacheImageOutputStream stream = new MemoryCacheImageOutputStream(baos);
        jpgWriter.setOutput(stream);
        IIOImage outputImage = new IIOImage(img, null, null);
        jpgWriter.write(null, outputImage, jpgWriteParam);
        jpgWriter.dispose();
        baos.flush();
        byte[] data = baos.toByteArray();
        baos.close();
        return data;
    }




    public static BufferedImage loadImage(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);

        BufferedImage bufferedImage = ImageIO.read(fis);

        if (bufferedImage == null) {
            throw new IOException("Failed to read image");
        }

        return bufferedImage;
    }


}