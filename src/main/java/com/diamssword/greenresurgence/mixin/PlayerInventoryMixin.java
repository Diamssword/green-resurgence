package com.diamssword.greenresurgence.mixin;

import com.diamssword.greenresurgence.containers.player.CustomPlayerInventory;
import com.diamssword.greenresurgence.systems.Components;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {


	@Shadow
	@Final
	public PlayerEntity player;
	@Shadow
	public int selectedSlot;
	@Shadow
	@Final
	public DefaultedList<ItemStack> main;

	@Shadow
	public abstract void updateItems();

	@Inject(at = @At("HEAD"), method = "getSwappableHotbarSlot", cancellable = true)
	private void getSwappableHotbarSlot(CallbackInfoReturnable<Integer> cir) {
		var max = CustomPlayerInventory.getHotbarSlotCount(player);
		for (int i = 0; i < max; i++) {
			int j = (this.selectedSlot + i) % max;
			if (this.main.get(j).isEmpty()) {
				cir.setReturnValue(j);
			}
		}

		for (int ix = 0; ix < max; ix++) {
			int j = (this.selectedSlot + ix) % max;
			if (!this.main.get(j).hasEnchantments()) {
				cir.setReturnValue(j);
			}
		}
		cir.setReturnValue(this.selectedSlot);
	}

	@Inject(at = @At("RETURN"), method = "addPickBlock")
	private void addPickBlock(ItemStack stack, CallbackInfo ci) {
		if (this.player.getWorld().isClient) {
			var pinv = player.getComponent(Components.PLAYER_INVENTORY);
			pinv.getInventory().syncHotbarToServer();
		}

	}

	@Inject(at = @At("HEAD"), method = "scrollInHotbar", cancellable = true)
	protected void scrollInHotbar(double scroll, CallbackInfo ci) {
		var max = CustomPlayerInventory.getHotbarSlotCount(player);
		int i = (int) Math.signum(scroll);
		this.selectedSlot -= i;

		while (this.selectedSlot < 0) {
			this.selectedSlot += max;
		}

		while (this.selectedSlot >= max) {
			this.selectedSlot -= max;
		}
		ci.cancel();
	}

	@Inject(at = @At("HEAD"), method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", cancellable = true)
	protected void insertStack(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (!player.isCreative()) {
			if (stack.isEmpty()) {
				cir.setReturnValue(false);
			} else {
				var pinv = player.getComponent(Components.PLAYER_INVENTORY);

				try {
					cir.setReturnValue(pinv.getInventory().insterStack(stack));

				} catch (Throwable var6) {
					CrashReport crashReport = CrashReport.create(var6, "Adding item to inventory");
					CrashReportSection crashReportSection = crashReport.addElement("Item being added");
					crashReportSection.add("Item ID", Item.getRawId(stack.getItem()));
					crashReportSection.add("Item data", stack.getDamage());
					crashReportSection.add("Item name", () -> stack.getName().getString());
					throw new CrashException(crashReport);
				}
			}
		}
	}
}