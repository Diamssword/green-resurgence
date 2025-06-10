package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.gui.faction.FactionMainGui;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.CurrentZonePacket;
import com.diamssword.greenresurgence.network.GuiPackets;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Keybinds {
    private static final Map<KeyBinding, Runnable> bindings = new HashMap<>();
    private static final List<KeyBinding> keys = new ArrayList<>();
    private static final String CAT = "category." + GreenResurgence.ID;
    private static final String K = "key." + GreenResurgence.ID;
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static KeyBinding FACTION_INV = registerOnPress(new KeyBinding(K + ".base_inv", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, CAT), () -> Channels.MAIN.clientHandle().send(new GuiPackets.KeyPress(GuiPackets.KEY.Inventory)));
    public static KeyBinding FACTION_MAIN = registerOnPress(new KeyBinding(K + ".faction", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F, CAT), () -> {
        if (CurrentZonePacket.myGuild != null)
            client.setScreen(new FactionMainGui(CurrentZonePacket.myGuild.id(), CurrentZonePacket.myGuild.name()));
    });
    public static KeyBinding CRAWL = registerOnTap(new KeyBinding(K + ".crawl", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_X, CAT), 10, () -> Channels.MAIN.clientHandle().send(new GuiPackets.KeyPress(GuiPackets.KEY.Crawl)));

    public static void init() {
        keys.forEach(KeyBindingHelper::registerKeyBinding);
        bindings.keySet().forEach(KeyBindingHelper::registerKeyBinding);

    }

    public static void tick() {
        bindings.values().forEach(Runnable::run);
    }

    public static KeyBinding register(KeyBinding key, Runnable callback) {
        bindings.put(key, callback);
        return key;
    }

    public static KeyBinding register(KeyBinding key) {
        keys.add(key);
        return key;
    }

    public static KeyBinding registerOnRelease(KeyBinding key, Runnable callback) {

        bindings.put(key, () -> {
            if (key.wasPressed())
                callback.run();
        });
        return key;
    }

    public static KeyBinding registerOnTap(KeyBinding key, int cooldown, Runnable callback) {
        AtomicInteger d = new AtomicInteger();
        bindings.put(key, () -> {
            if (key.isPressed() && d.get() == 0) {
                callback.run();
                d.set(cooldown);
            }
            if (d.get() > 0)
                d.getAndDecrement();

        });
        return key;
    }

    public static KeyBinding registerOnPress(KeyBinding key, Runnable callback) {

        bindings.put(key, () -> {
            if (key.isPressed())
                callback.run();
        });
        return key;
    }
}
