package com.diamssword.greenresurgence.render.environment;

import com.diamssword.greenresurgence.shaders.ShaderRegister;
import com.diamssword.greenresurgence.utils.Utils;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class FixedColorFogModifier extends FogModifier {
	public final Vector3f fog;
	public final Vector3f vignette;

	public FixedColorFogModifier(NbtCompound tag) {
		super(Utils.boxFromNBT(tag.getCompound("box")), tag.getBoolean("bottom"));
		this.fog = Utils.vecFromNBT(tag.getCompound("fog"));
		this.vignette = Utils.vecFromNBT(tag.getCompound("vignette"));
	}

	public FixedColorFogModifier(Vector3f fog, Vector3f vignette, Box box, boolean strongAtBottom) {
		super(box, strongAtBottom);
		this.fog = fog;
		this.vignette = vignette;
	}

	@Override
	public Vector3f getFogColor(float intensity) {
		return fog;
	}

	@Override
	public Vector4f getVignetteColor(float intensity) {
		return new Vector4f(vignette.x, vignette.y, vignette.z, intensity);
	}

	@Override
	public void outsideZoneUpdate(double distance, long time) {
		if(time % 200 == 0 && distance > 32 && distance < MinecraftClient.getInstance().options.getClampedViewDistance() * 16) {
			float s = (float) (getBox().getAverageSideLength() / 100f);
			AAALevel.addParticle(MinecraftClient.getInstance().world, ShaderRegister.SMOKE.clone().scale(s).position(getBox().getCenter()));
		}
		//if(distance < 16)
		//spawnFogParticles(MinecraftClient.getInstance(), 0);
	}

	@Override
	public void insideZoneUpdate(double distanceFromCenter, long time) {
		var cl = MinecraftClient.getInstance();

		float intensity = getIntensity(cl.player.getPos(), getBox(), strongAtBottom);
		spawnFogParticles(cl, intensity);
	}

	private void spawnFogParticles(MinecraftClient client, float intensity) {
		Vec3d pos = client.player.getPos();


		// scale spawn rate with intensity
		int count = 1 + (int) (intensity * 5); // tweak


		for(int i = 0; i < count; i++) {
			spawnSingleParticle(client, pos, intensity);
		}
	}

	private void spawnSingleParticle(MinecraftClient client, Vec3d center, float intensity) {
		double radius = 16.0; // spawn area

		double x = center.x + (client.world.random.nextDouble() - 0.5) * radius;
		double y = center.y + client.world.random.nextDouble() * 8.0; // slightly above ground
		double z = center.z + (client.world.random.nextDouble() - 0.5) * radius;
		// slow drifting motion
		double vx = (client.world.random.nextDouble() - 0.5) * 0.02;
		double vy = client.world.random.nextDouble() * 1;
		double vz = (client.world.random.nextDouble() - 0.5) * 0.02;

		client.world.addParticle(
				ParticleTypes.SPORE_BLOSSOM_AIR, // temporary, replace later
				x, y, z,
				vx, vy, vz
		);
	}
}
