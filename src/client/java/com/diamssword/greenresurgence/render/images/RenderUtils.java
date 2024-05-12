package com.diamssword.greenresurgence.render.images;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec2f;
import org.joml.Vector3f;

public class RenderUtils {

    public static int getArgb(int a, int red, int green, int blue) {
        return a << 24 | red << 16 | green << 8 | blue;
    }

    public static int getAlpha(int argb) {
        return (argb >> 24) & 0xFF;
    }

    public static int getRed(int argb) {
        return (argb >> 16) & 0xFF;
    }

    public static int getGreen(int argb) {
        return (argb >> 8) & 0xFF;
    }

    public static int getBlue(int argb) {
        return argb & 0xFF;
    }

    public static float getAlphaFloat(int argb) {
        return (float) getAlpha(argb) / 255F;
    }

    public static float getRedFloat(int argb) {
        return (float) getRed(argb) / 255F;
    }

    public static float getGreenFloat(int argb) {
        return (float) getGreen(argb) / 255F;
    }

    public static float getBlueFloat(int argb) {
        return (float) getBlue(argb) / 255F;
    }


}