package com.diamssword.greenresurgence.cosmetics;

import net.minecraft.entity.player.PlayerEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AnimatablePlayer implements GeoAnimatable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
  //  private final PlayerEntity player;
    public AnimatablePlayer()
    {
    //    player=entity;
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                // Add our flying animation controller
              /*  new AnimationController<>(this, 10, state -> state.setAndContinue(this.isFlying ? DefaultAnimations.FLY : DefaultAnimations.IDLE))
                        // Handle the custom instruction keyframe that is part of our animation json
                        .setCustomInstructionKeyframeHandler(state -> {
                            PlayerEntity player = ClientUtils.getClientPlayer();

                            if (player != null)
                                player.sendMessage(Text.literal("KeyFraming"), true);
                        }),
               */
                // Add our generic living animation controller
                DefaultAnimations.genericLivingController(this)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public double getTick(Object object) {
        return ((PlayerEntity) object).age;
    }
}
