package com.diamssword.greenresurgence.gui.hud;

import com.diamssword.greenresurgence.GreenResurgence;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.event.WindowResizeCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class CustomHud {
    private static HudGui instance;

    public static void init()
    {
        HudRenderCallback.EVENT.register(CustomHud::renderCustomHud);

        final boolean[] deb = {false};
        if(FabricLoader.getInstance().isDevelopmentEnvironment()) {
            var debug = KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + GreenResurgence.ID + ".debug_hud", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I, "category." + GreenResurgence.ID + ".debug"));
            var reload = KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + GreenResurgence.ID + ".reload_hud", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category." + GreenResurgence.ID + ".debug"));
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if(instance !=null) {
                    if (debug.isPressed()) {
                        deb[0] = !deb[0];
                        instance.debug(deb[0]);
                    }
                    if (reload.isPressed()) {
                        instance = new HudGui();
                        instance.onDisplayed();
                        var mc = MinecraftClient.getInstance();
                        instance.init(mc, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
                    }
                }
            });
        }
        WindowResizeCallback.EVENT.register((l,d)->{
            onResize(l,d.getScaledWidth(),d.getScaledHeight());
        });
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (instance != null && client.world != null) {
                Screen.wrapScreenError(() -> instance.tick(), "Ticking screen", instance.getClass().getCanonicalName());
            }
        });
    }
    public static void onResize(MinecraftClient cl,int w,int h)
    {
        if(instance !=null)
            instance.resize(cl,w,h);

    }
    private static void renderCustomHud(DrawContext context, float tickDelta) {

        if(instance ==null) {
            instance = new HudGui();
            instance.onDisplayed();
            var mc=MinecraftClient.getInstance();
            context.push().translate(0, 0, 100);
            instance.init(mc, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
            context.pop();
        }
        context.push();
        context.getMatrices().translate(0.0F, 0.0F, 100.0F); //prevent chat form hiding components
        instance.renderWithTooltip(context,0,0,tickDelta);
        context.pop();
    }
}
