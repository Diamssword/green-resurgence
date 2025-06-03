package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.blockEntities.ArmorTinkererBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;

public class ModularArmorPackets {

	public final static String[] modeles = new String[]{"circulation_plot_hat", "makeshift_heavy", "makeshift_light", "makeshift_medium", "welding_mask", "welding_mask_strip"};

	public record ChangeModel(BlockPos pos, int slot, String model) {
	}

	public record RequestGui(BlockPos pos) {
	}

	public static void init() {
		Channels.MAIN.registerServerbound(RequestGui.class, (msg, ctx) -> {
			if (msg.pos.isWithinDistance(ctx.player().getPos(), 8)) {
				var te = ctx.player().getWorld().getBlockEntity(msg.pos);
				if (te instanceof ArmorTinkererBlockEntity at) {
					at.openInventory(ctx.player());
				}
			}
		});
		Channels.MAIN.registerServerbound(ChangeModel.class, (msg, ctx) -> {

			if (msg.pos.isWithinDistance(ctx.player().getPos(), 8)) {
				var te = ctx.player().getWorld().getBlockEntity(msg.pos);
				if (te instanceof ArmorTinkererBlockEntity at) {
					if (msg.slot >= 0 && msg.slot < 4 && Arrays.stream(modeles).toList().contains(msg.model)) {
						at.getInventory().getStack(msg.slot).getOrCreateNbt().putString("model", msg.model);
						((ServerWorld) at.getWorld()).getChunkManager().markForUpdate(at.getPos());
						at.openInventory(ctx.player());
					}
				}
			}
		});
	}
}
