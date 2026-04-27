package com.diamssword.greenresurgence.mixin.client;

import com.diamssword.greenresurgence.GreenResurgenceClient;
import com.diamssword.greenresurgence.render.environment.EnvironementAreas;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
	@Mutable
	@Shadow
	@Final
	private static Set<Item> BLOCK_MARKER_ITEMS;

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void init(CallbackInfo ci) {
		var ls = new ArrayList<>(BLOCK_MARKER_ITEMS);
		ls.addAll(List.of(GreenResurgenceClient.MARKER_ITEMS));
		BLOCK_MARKER_ITEMS = Set.of(ls.toArray(new Item[0]));
	}

	@Inject(method = "getSkyColor", at = @At("RETURN"), cancellable = true)
	public void getSkyColor(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
		var col = cir.getReturnValue();
		EnvironementAreas.getCurrentFogModifier().ifPresent(f -> cir.setReturnValue(f.modifySky(cameraPos, tickDelta, col)));

	}
}