package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.clothing.ClothingLoader;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PosesPackets {
    public record LiftOtherPlayer(UUID player,boolean carried){};
    public record DismountedPlayerNotify(UUID player){};
    public static void init()
    {
        Channels.MAIN.registerClientboundDeferred(LiftOtherPlayer.class);
        Channels.MAIN.registerClientboundDeferred(DismountedPlayerNotify.class);
    }
}
