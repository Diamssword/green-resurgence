package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.containers.player.HoloPanelConfig;
import com.diamssword.greenresurgence.systems.Components;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class InventoryPackets {
	public record SyncCreativeHotbar(ItemStack[] stacks) {
	}

	public record SyncHoloPanels(NbtCompound panels) {
	}

	public static void init() {
		Channels.MAIN.registerServerbound(InventoryPackets.SyncCreativeHotbar.class, (msg, ctx) -> {
			if(ctx.player().isCreative()) {
				var inv = ctx.player().getComponent(Components.PLAYER_INVENTORY);
				for(int i = 0; i < msg.stacks.length; i++) {
					inv.getInventory().getHotBar().setStack(i, msg.stacks[i]);
				}
			}

		});
		Channels.MAIN.registerServerbound(InventoryPackets.SyncHoloPanels.class, (msg, ctx) -> {
			var ls = msg.panels.getList("ls", NbtElement.COMPOUND_TYPE);
			HoloPanelConfig[] cfg = new HoloPanelConfig[ls.size()];
			for(int i = 0; i < ls.size(); i++) {
				cfg[i] = HoloPanelConfig.fromNBT(ls.getCompound(i));
			}
			Components.PLAYER_INVENTORY.get(ctx.player()).setHoloPanels(cfg);

		});
	}
}
