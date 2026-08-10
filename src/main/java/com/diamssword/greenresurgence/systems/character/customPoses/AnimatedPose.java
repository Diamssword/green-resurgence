package com.diamssword.greenresurgence.systems.character.customPoses;

import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.PosesPackets;
import com.diamssword.greenresurgence.systems.character.PosesManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AnimatedPose implements IPlayerCustomPose, GeoAnimatable {
	private final RawAnimation anim;
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private final PoseType type;
	public final String animation;
	private final int maxTick;
	public final PlayerEntity player;
	private int tick;

	public AnimatedPose(PlayerEntity player, PoseType type, String animation) {
		this(player, type, animation, 1000);
	}

	public AnimatedPose(PlayerEntity player, PoseType type, String animation, int maxTickDuration) {
		this.type = type;
		this.animation = animation;
		this.maxTick = maxTickDuration;
		this.player = player;
		this.anim = RawAnimation.begin().thenPlay(animation);
	}

	@Override
	public boolean canBeStoppedByClient() {
		return true;
	}

	@Override
	public void tick() {
		if(maxTick > -1)
			this.tick++;
		if(player.getWorld().isClient) {
			var cont = this.cache.getManagerForId(player.getId()).getAnimationControllers().get("base_controller");
			if(cont != null && cont.hasAnimationFinished()) {
				Channels.MAIN.clientHandle().send(new PosesPackets.EmoteRequest(animation, true));
			} else
				addExtraEffects();
		}
	}

	private void addExtraEffects() {
		if(this.animation.equals(PosesManager.CRY_EMOTE)) {
			if(tick > 10 && tick % 10 == 0) {
				Vec3d eyePos = player.getEyePos();
				Vec3d center = eyePos.add(getRotationVector(player.getPitch(), player.bodyYaw).multiply(0.25f));
				Vec3d right = new Vec3d(
						Math.cos(Math.toRadians(player.bodyYaw)),
						0,
						Math.sin(Math.toRadians(player.bodyYaw))
				);

				double offset = 0.1;

				Vec3d leftTear = center.subtract(right.multiply(offset));
				Vec3d rightTear = center.add(right.multiply(offset));
				player.getWorld().addParticle(ParticleTypes.FALLING_WATER, leftTear.x, leftTear.y, leftTear.z, 0, 0, 0);
				player.getWorld().addParticle(ParticleTypes.FALLING_WATER, rightTear.x, rightTear.y, rightTear.z, 0, 0, 0);
			}


		}
	}

	protected final Vec3d getRotationVector(float pitch, float yaw) {
		float f = pitch * ((float) Math.PI / 180);
		float g = -yaw * ((float) Math.PI / 180);
		float h = MathHelper.cos(g);
		float i = MathHelper.sin(g);
		float j = MathHelper.cos(f);
		float k = MathHelper.sin(f);
		return new Vec3d(i * j, -k, h * j);
	}

	public String getAnimation() {
		return animation;
	}

	@Override
	public boolean shouldExitPose() {
		return maxTick != -1 && tick > maxTick;
	}

	@Override
	public PoseType getPoseType() {
		return type;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		var cont = new AnimationController<>(this, 1, state -> state.setAndContinue(anim));
		controllers.add(cont);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	public double getTick(Object object) {
		return player.age;
	}
}
