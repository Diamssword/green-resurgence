package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.containers.Containers;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class Handlers {
    public static void init()
    {

       // HandledScreens.register(Containers.RELATIVE, SurvivalistInventory::new);
        HandledScreens.register(Containers.ITEMBLOCK, ItemBlockGui::new);
        HandledScreens.register(Containers.LOOTABLE_INV, LootableInvGui::new);
        HandledScreens.register(Containers.BLOCK_VARIANT_INV, BlockVariantScreen::new);
        HandledScreens.register(Containers.CRAFTER, CrafterScreen::new);

    }
}
