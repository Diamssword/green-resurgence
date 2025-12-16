package com.diamssword.greenresurgence.mixin;

//@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

/*
	// ThreadLocal is the safest way to transfer the value to ModifyArg
	private static final ThreadLocal<Hand> CAPTURED_HAND = new ThreadLocal<>();

	@Inject(
			method = "swingHand(Lnet/minecraft/util/Hand;Z)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/network/packet/s2c/play/EntityAnimationS2CPacket;<init>(Lnet/minecraft/entity/Entity;I)V"
			),
			locals = LocalCapture.CAPTURE_FAILEXCEPTION
	)
	private void captureHand(Hand hand, boolean fromServerPlayer, CallbackInfo ci) {
		CAPTURED_HAND.set(hand);
	}

	@ModifyArg(at =
	@At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/EntityAnimationS2CPacket;<init>(Lnet/minecraft/entity/Entity;I)V"), index = 1, method = "swingHand(Lnet/minecraft/util/Hand;Z)V")
	public int onSwing(int par2) {
		Hand hand = CAPTURED_HAND.get();
		CAPTURED_HAND.remove();
		if(((Object) this) instanceof PlayerEntity pl) {
			hand = pl.getComponent(Components.PLAYER_DATA).nextHandSwing;

		}
		return hand == Hand.MAIN_HAND ? EntityAnimationS2CPacket.SWING_MAIN_HAND : EntityAnimationS2CPacket.SWING_OFF_HAND;
	}

 */
}