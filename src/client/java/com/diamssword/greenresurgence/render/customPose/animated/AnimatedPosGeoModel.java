package com.diamssword.greenresurgence.render.customPose.animated;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.character.customPoses.AnimatedPose;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import software.bernie.geckolib.core.molang.MolangParser;
import software.bernie.geckolib.core.molang.MolangQueries;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.RenderUtils;

public class AnimatedPosGeoModel extends GeoModel<AnimatedPose> {
	private final Identifier animationsPath;

	public AnimatedPosGeoModel() {
		this.animationsPath = buildFormattedAnimationPath();

	}

	@Override
	public void applyMolangQueries(AnimatedPose animatable, double animTime) {
		var entity = animatable.player;
		MolangParser parser = MolangParser.INSTANCE;
		MinecraftClient mc = MinecraftClient.getInstance();

		parser.setMemoizedValue(MolangQueries.LIFE_TIME, () -> animTime / 20d);
		parser.setMemoizedValue(MolangQueries.ACTOR_COUNT, mc.world::getRegularEntityCount);
		parser.setMemoizedValue(MolangQueries.TIME_OF_DAY, () -> mc.world.getTimeOfDay() / 24000f);
		parser.setMemoizedValue(MolangQueries.MOON_PHASE, mc.world::getMoonPhase);

		parser.setMemoizedValue(MolangQueries.DISTANCE_FROM_CAMERA, () -> mc.gameRenderer.getCamera().getPos().distanceTo(entity.getPos()));
		parser.setMemoizedValue(MolangQueries.IS_ON_GROUND, () -> RenderUtils.booleanToFloat(entity.isOnGround()));
		parser.setMemoizedValue(MolangQueries.IS_IN_WATER, () -> RenderUtils.booleanToFloat(entity.isTouchingWater()));
		parser.setMemoizedValue(MolangQueries.IS_IN_WATER_OR_RAIN, () -> RenderUtils.booleanToFloat(entity.isTouchingWaterOrRain()));
		parser.setMemoizedValue(MolangQueries.IS_ON_FIRE, () -> RenderUtils.booleanToFloat(entity.isOnFire()));

		parser.setMemoizedValue(MolangQueries.HEALTH, entity::getHealth);
		parser.setMemoizedValue(MolangQueries.MAX_HEALTH, entity::getMaxHealth);
		parser.setMemoizedValue("query.is_still", () -> {
			Vec3d velocity = entity.getVelocity();
			double horizontalSpeed = Math.sqrt(
					velocity.x * velocity.x +
							velocity.z * velocity.z
			);
			return horizontalSpeed > 0.2f * 0.2f ? 0f : 1f;
		});
		parser.setMemoizedValue(MolangQueries.GROUND_SPEED, () -> {
			Vec3d velocity = entity.getVelocity();
			return MathHelper.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
		});
		parser.setMemoizedValue(MolangQueries.YAW_SPEED, () -> entity.getYaw() - entity.prevYaw);

	}

	@Override
	public AnimationProcessor<AnimatedPose> getAnimationProcessor() {
		return super.getAnimationProcessor();
	}

	public Identifier buildFormattedAnimationPath() {
		return GreenResurgence.asRessource("animations/player_emotes.animation.json");
	}

	@Override
	public Identifier getModelResource(AnimatedPose animatable) {
		return null;
	}

	@Override
	public Identifier getTextureResource(AnimatedPose animatable) {
		return null;
	}

	@Override
	public Identifier getAnimationResource(AnimatedPose animatable) {
		return animationsPath;
	}
}
