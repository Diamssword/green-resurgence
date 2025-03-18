package com.diamssword.greenresurgence.mixin.client;

import com.diamssword.greenresurgence.containers.player.CustomPlayerInventory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class ClientMixing {

    @Shadow
    public ClientPlayerEntity player;
    @Redirect(at=@At(value="INVOKE",target = "Lnet/minecraft/entity/player/PlayerInventory;isValidHotbarIndex(I)Z"),method = "doItemPick")
    private boolean doItemPick(int slot)
    {
        return slot>=0 && slot< CustomPlayerInventory.getHotbarSlotCount(player);
    }
    @Inject(at=@At("TAIL"), method = "handleInputEvents")
    private void onInputHandle(CallbackInfo ci)
    {
        var inv = player.getInventory();
        var max=CustomPlayerInventory.getHotbarSlotCount(player);
        if(inv.selectedSlot>=max)
            inv.selectedSlot=max-1;
    }
}
