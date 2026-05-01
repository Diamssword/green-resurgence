package com.diamssword.greenresurgence.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class SporeParticle extends SpriteBillboardParticle {
	SporeParticle(ClientWorld world, SpriteProvider spriteProvider, double x, double y, double z) {
		super(world, x, y - 0.125, z);
		this.setBoundingBoxSpacing(0.01F, 0.01F);
		this.setSprite(spriteProvider);
		this.scale = this.scale * (this.random.nextFloat() * 0.6F + 0.2F);
		this.maxAge = (int) (16.0 / (Math.random() * 0.8 + 0.2));
		this.collidesWithWorld = true;
		this.velocityMultiplier = 1.0F;
		this.gravityStrength = 0.0F;
	}

	SporeParticle(ClientWorld world, SpriteProvider spriteProvider, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		super(world, x, y - 0.125, z, velocityX, velocityY, velocityZ);
		this.setBoundingBoxSpacing(0.01F, 0.01F);
		this.setSprite(spriteProvider);
		this.scale = this.scale * (this.random.nextFloat() * 0.6F + 0.6F);
		this.maxAge = (int) (16.0 / (Math.random() * 0.8 + 0.2));
		this.collidesWithWorld = true;
		this.velocityMultiplier = 1.0F;
		this.gravityStrength = 0.0F;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Environment(EnvType.CLIENT)
	public static class CrimsonSporeFactory implements ParticleFactory<SporeParticleEffect> {
		private final SpriteProvider spriteProvider;

		public CrimsonSporeFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SporeParticleEffect defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			Random random = clientWorld.random;
			double j = random.nextGaussian() * 1.0E-6F;
			double k = random.nextGaussian() * 1.0E-4F;
			double l = random.nextGaussian() * 1.0E-6F;
			SporeParticle sporeParticle = new SporeParticle(clientWorld, this.spriteProvider, d, e, f, j, k, l);
			sporeParticle.setColor(0.9F, 0.4F, 0.5F);
			return sporeParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class SporeBlossomAirFactory implements ParticleFactory<SporeParticleEffect> {
		private final SpriteProvider spriteProvider;

		public SporeBlossomAirFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SporeParticleEffect defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			SporeParticle sporeParticle = new SporeParticle(clientWorld, this.spriteProvider, d, e, f, 0.0, -0.8F, 0.0) {
				@Override
				public Optional<ParticleGroup> getGroup() {
					return Optional.of(ParticleGroup.SPORE_BLOSSOM_AIR);
				}
			};
			sporeParticle.maxAge = MathHelper.nextBetween(clientWorld.random, 500, 800);
			sporeParticle.gravityStrength = 0.0001F;
			sporeParticle.setColor(defaultParticleType.color.x, defaultParticleType.color.y, defaultParticleType.color.z);
			sporeParticle.alpha = defaultParticleType.color.w;
			sporeParticle.scale = defaultParticleType.scale;
			return sporeParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class UnderwaterFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public UnderwaterFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			SporeParticle sporeParticle = new SporeParticle(clientWorld, this.spriteProvider, d, e, f);
			sporeParticle.setColor(0.4F, 0.4F, 0.7F);
			return sporeParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class WarpedSporeFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public WarpedSporeFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			double j = clientWorld.random.nextFloat() * -1.9 * clientWorld.random.nextFloat() * 0.1;
			SporeParticle sporeParticle = new SporeParticle(clientWorld, this.spriteProvider, d, e, f, 0.0, j, 0.0);
			sporeParticle.setColor(0.1F, 0.1F, 0.3F);
			sporeParticle.setBoundingBoxSpacing(0.001F, 0.001F);
			return sporeParticle;
		}
	}
}
