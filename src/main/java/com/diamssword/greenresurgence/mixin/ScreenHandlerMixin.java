package com.diamssword.greenresurgence.mixin;

import com.diamssword.greenresurgence.containers.AbstractMultiInvScreenHandler;
import com.diamssword.greenresurgence.containers.player.compatibility.RoutedSlot;
import com.diamssword.greenresurgence.systems.Components;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
	@Unique
	private boolean markForRouting = false;
	@Shadow
	@Final
	public DefaultedList<Slot> slots;

	@Inject(method = "addSlot", at = @At("HEAD"))
	private void replacePlayerSlot(Slot slot, CallbackInfoReturnable<Slot> cir) {
		if(!markForRouting && slot.inventory instanceof PlayerInventory pl && pl.player instanceof ServerPlayerEntity) {
			markForRouting = (Object) this != pl.player.playerScreenHandler && !((Object) this instanceof PlayerScreenHandler) && !((Object) this instanceof AbstractMultiInvScreenHandler<?>);
		}
	}

	@Unique
	private void replaceSlots() {
		markForRouting = false;
		SimpleInventory overflow = null;
		DefaultedList<Slot> l = DefaultedList.of();
		for(Slot s : slots) {
			if(s.inventory instanceof PlayerInventory pl) {
				if(pl.player.isCreative())
					return;
				if(overflow == null) {
					overflow = new SimpleInventory(pl.size());
					overflow.addListener(in -> {
						for(int i = 0; i < in.size(); i++) {
							pl.player.dropItem(in.removeStack(i), true);
						}
					});
				}
				var pinv = pl.player.getComponent(Components.PLAYER_INVENTORY);
				l.add(RoutedSlot.createSlot(pinv.getInventory(), s.inventory, overflow, s.getIndex(), s.x, s.y));
			} else
				l.add(s);
		}
		slots.clear();
		slots.addAll(l);
	}

	@Inject(at = @At("HEAD"), method = "sendContentUpdates")
	private void sendContentUpdates(CallbackInfo ci) {
		if(markForRouting)
			replaceSlots();
	}

	@Inject(at = @At("HEAD"), method = "updateToClient")
	private void updateToClient(CallbackInfo ci) {
		if(markForRouting)
			replaceSlots();
	}
}
