package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.blockEntities.ArmorTinkererBlockEntity;
import com.diamssword.greenresurgence.systems.armor.ArmorLoader;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class ModularArmorPackets {
	public static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

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
					if (msg.slot >= 0 && msg.slot < 4) {
						var mod = ArmorLoader.loader.getModel(msg.model);

						if (mod.isPresent() && ArmorLoader.isSLotValidFor(mod.get(), EQUIPMENT_SLOT_ORDER[msg.slot])) {
							at.getInventory().getStack(msg.slot).getOrCreateNbt().putString("model", msg.model);
							((ServerWorld) at.getWorld()).getChunkManager().markForUpdate(at.getPos());
							at.openInventory(ctx.player());
						}
					}
				}
			}
		});
	}
}
