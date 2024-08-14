package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.network.AdventureInteract;
import com.diamssword.greenresurgence.network.ClientGuiPacket;
import com.diamssword.greenresurgence.network.ClientZonePacket;

public class ClientNetwork {
    public static void initialize()
    {
        ClientGuiPacket.init();
        ClientZonePacket.init();
    }
}
