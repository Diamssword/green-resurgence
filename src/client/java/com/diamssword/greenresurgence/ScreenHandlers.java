package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.gui.BlockVariantScreen;
import com.diamssword.greenresurgence.gui.ItemBlockGui;
import com.diamssword.greenresurgence.gui.ItemBlockGuiSimple;
import com.diamssword.greenresurgence.gui.playerContainers.*;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ScreenHandlers {
	public static void init() {

		// HandledScreens.register(Containers.RELATIVE, SurvivalistInventory::new);
		HandledScreens.register(Containers.ITEMBLOCK, ItemBlockGui::new);
		HandledScreens.register(Containers.ITEMBLOCKSIMPLE, ItemBlockGuiSimple::new);
		HandledScreens.register(Containers.LOOTABLE_INV, LootableInvGui::new);
		HandledScreens.register(Containers.BLOCK_VARIANT_INV, BlockVariantScreen::new);
		HandledScreens.register(Containers.CRAFTER, CrafterScreen::new);
		HandledScreens.register(Containers.FAC_STORAGE, FactionStorageScreen::new);
		HandledScreens.register(Containers.FAC_CHEST, FactionChestGui::new);
		HandledScreens.register(Containers.GENERIC_CONTAINER, GenericContainerGui::new);
		HandledScreens.register(Containers.PLAYER, PlayerInventoryGui::new);
		HandledScreens.register(Containers.ARMOR_TINKERER, ArmorTinkererContainerGui::new);
		HandledScreens.register(Containers.EQUIPMENT_TINKERER, EquipmentTinkererContainerGui::new);

	}
}
