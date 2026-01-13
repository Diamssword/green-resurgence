package com.diamssword.greenresurgence.mixin;

import com.diamssword.greenresurgence.structure.StructureProcessor;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SinglePoolElement.class)
public abstract class SinglePoolElementMixin {
	@Inject(
			method = "createPlacementData(Lnet/minecraft/util/BlockRotation;Lnet/minecraft/util/math/BlockBox;Z)Lnet/minecraft/structure/StructurePlacementData;",
			at = @At("RETURN"),
			cancellable = true
	)
	private void addCustomProcessor(BlockRotation rotation, BlockBox box, boolean keepJigsaws, CallbackInfoReturnable<StructurePlacementData> cir) {
		StructurePlacementData data = cir.getReturnValue();

		data.addProcessor(StructureProcessor.INSTANCE);

		cir.setReturnValue(data);
	}
}