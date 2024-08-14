package com.diamssword.greenresurgence.mixin;

import com.diamssword.greenresurgence.systems.Components;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PacketByteBuf.class)
public abstract class PacketByteBufMixin {


	/**
	 * Allow for more than 64 stack size
	 */
	@Inject(method = "writeItemStack", at = @At("HEAD"), cancellable = true)
	public void writeItem(ItemStack stack, CallbackInfoReturnable<PacketByteBuf> cir) {
		PacketByteBuf thisObject = (PacketByteBuf)(Object)this;
		if (stack.isEmpty()) {
			thisObject.writeBoolean(false);
		} else {
			thisObject.writeBoolean(true);
			Item item = stack.getItem();
			thisObject.writeRegistryValue(Registries.ITEM, item);
			thisObject.writeInt(stack.getCount());
			NbtCompound nbtCompound = null;
			if (item.isDamageable() || item.isNbtSynced()) {
				nbtCompound = stack.getNbt();
			}
			thisObject.writeNbt(nbtCompound);
		}
		cir.setReturnValue(thisObject);
	}

	/**
	 * Allow for more than 64 stack size
	 */
	@Inject(method = "readItemStack", at = @At("HEAD"), cancellable = true)
	public void readItemStack(CallbackInfoReturnable<ItemStack> cir) {
		PacketByteBuf thisObject = (PacketByteBuf)(Object)this;
		if (!thisObject.readBoolean()) {
			cir.setReturnValue(ItemStack.EMPTY);
		} else {
			Item item = (Item)thisObject.readRegistryValue(Registries.ITEM);
			int i = thisObject.readInt();
			ItemStack itemStack = new ItemStack(item, i);
			itemStack.setNbt(thisObject.readNbt());
			cir.setReturnValue(itemStack);
		}
	}
}