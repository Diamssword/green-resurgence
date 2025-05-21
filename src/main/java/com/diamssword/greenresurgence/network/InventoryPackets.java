package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.systems.Components;
import net.minecraft.item.ItemStack;

public class InventoryPackets {
	public record SyncCreativeHotbar(ItemStack[] stacks) {
	}

	public static void init() {
		Channels.MAIN.registerServerbound(InventoryPackets.SyncCreativeHotbar.class, (msg, ctx) -> {
			if (ctx.player().isCreative()) {
				var inv = ctx.player().getComponent(Components.PLAYER_INVENTORY);
				for (int i = 0; i < msg.stacks.length; i++) {
					inv.getInventory().getHotBar().setStack(i, msg.stacks[i]);
				}
			}

		});
	}
}
