package com.diamssword.greenresurgence.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class BloodParticle extends SpriteBillboardParticle {
	private static final Random RANDOM = Random.create();
	private boolean landed = false;


	BloodParticle(ClientWorld world, SpriteProvider spriteProvider, double x, double y, double z) {
		super(world, x, y - 0.125, z);
		this.setBoundingBoxSpacing(0.01F, 0.01F);
		this.setSprite(spriteProvider);
		this.scale = this.scale * (this.random.nextFloat() * 1.2f);
		this.maxAge = 200 + RANDOM.nextInt(100);
		this.collidesWithWorld = true;
		this.velocityMultiplier = 0.97F;
		this.gravityStrength = 0.18F;
		float r = 0.45F + random.nextFloat() * 0.15F;
		setColor(r, 0.02F, 0.02F);
	}

	BloodParticle(ClientWorld world, SpriteProvider spriteProvider, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		super(world, x, y - 0.125, z, velocityX, velocityY, velocityZ);
		this.setBoundingBoxSpacing(0.01F, 0.01F);
		this.setSprite(spriteProvider);
		this.scale = this.scale * (this.random.nextFloat() * 1.2f);
		//this.scale = this.scale * (this.random.nextFloat() * 0.6F + 0.2F);
		this.maxAge = 200 + RANDOM.nextInt(100);
		this.collidesWithWorld = true;
		this.velocityMultiplier = 0.97F;
		this.gravityStrength = 0.18F;
		float r = 0.45F + random.nextFloat() * 0.15F;
		setColor(r, 0.02F, 0.02F);
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		//	if(!onGround)
		super.buildGeometry(vertexConsumer, camera, tickDelta);
		//	else
		//		buildFlatGeometry(vertexConsumer, camera, tickDelta);
	}

	public void buildFlatGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		Vec3d cameraPos = camera.getPos();

		float x = (float) (MathHelper.lerp(tickDelta, this.prevPosX, this.x) - cameraPos.x);
		float y = (float) (MathHelper.lerp(tickDelta, this.prevPosY, this.y) - cameraPos.y);
		float z = (float) (MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - cameraPos.z);

		float s = this.getSize(tickDelta);

		float minU = this.getMinU();
		float maxU = this.getMaxU();
		float minV = this.getMinV();
		float maxV = this.getMaxV();

		int light = this.getBrightness(tickDelta);

		// Quad parallel to the ground (XZ plane)
		vertexConsumer.vertex(x - s, y, z - s)
				.texture(minU, maxV)
				.color(red, green, blue, alpha)
				.light(light);

		vertexConsumer.vertex(x - s, y, z + s)
				.texture(minU, minV)
				.color(red, green, blue, alpha)
				.light(light);

		vertexConsumer.vertex(x + s, y, z + s)
				.texture(maxU, minV)
				.color(red, green, blue, alpha)
				.light(light);

		vertexConsumer.vertex(x + s, y, z - s)
				.texture(maxU, maxV)
				.color(red, green, blue, alpha)
				.light(light);
	}

	@Override
	public void tick() {
		super.tick();
		if(!onGround) {
			move(velocityX, velocityY, velocityZ);
		} else if(!landed) {
			landed = true;
			// stop immediately
			velocityX = 0;
			velocityY = 0;
			velocityZ = 0;
			y += 0.01f;
			//gravityStrength = 0;
			// optional slight shrink
			//scale *= 0.995F;
			//	angle = random.nextFloat() * 360F;
			scale *= 1.5F;
		}
		if(age > maxAge - 30) {
			alpha = (maxAge - age) / 30F;
		}
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Override
		public @Nullable Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
			var bl = new BloodParticle(world, spriteProvider, x, y, z);
			bl.setVelocity(velocityX, velocityY, velocityZ);
			return bl;
		}
	}
}
