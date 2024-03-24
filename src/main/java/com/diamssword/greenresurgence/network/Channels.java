package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.GreenResurgence;
import io.wispforest.owo.network.OwoNetChannel;
import net.minecraft.util.Identifier;

public class Channels {
    public static final OwoNetChannel MAIN = OwoNetChannel.create(new Identifier(GreenResurgence.ID, "main"));

    public static void initialize()
    {
        AdventureInteract.init();
    }
}
