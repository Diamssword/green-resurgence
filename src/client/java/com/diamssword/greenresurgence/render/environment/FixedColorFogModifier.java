package com.diamssword.greenresurgence.render.environment;

import com.diamssword.greenresurgence.particles.FogArea;
import com.diamssword.greenresurgence.particles.FogRenderer;
import com.diamssword.greenresurgence.particles.SporeParticleEffect;
import com.diamssword.greenresurgence.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class FixedColorFogModifier extends FogModifier {
	public final Vector3f fog;
	public final Vector3f vignette;
	public final Vector4f particleColor;
	public final Vector4f particleColor1;
	public FogArea fogRender;

	public FixedColorFogModifier(NbtCompound tag) {
		super(Utils.boxFromNBT(tag.getCompound("box")), tag.getBoolean("bottom"));
		this.fog = Utils.vecFromNBT(tag.getCompound("fog"));
		this.vignette = Utils.vecFromNBT(tag.getCompound("vignette"));
		this.particleColor = Utils.vec4FromNBT(tag.getCompound("particle"));
		this.particleColor1 = Utils.vec4FromNBT(tag.getCompound("particle1"));
		setFogRenderer();
	}

	public FixedColorFogModifier(Vector3f fog, Vector3f vignette, Box box, boolean strongAtBottom, Vector4f particleColor, Vector4f particleColor1) {
		super(box, strongAtBottom);
		this.fog = fog;
		this.vignette = vignette;
		this.particleColor = particleColor;
		this.particleColor1 = particleColor1;
		setFogRenderer();
	}

	private void setFogRenderer() {
		if(fogRender != null)
			fogRender.setDead();
		FogRenderer.addArea(fogRender = new FogArea(particleColor, particleColor1, (float) (getBox().getAverageSideLength() / 30f), (float) (getBox().getAverageSideLength() / 60f), 0.01, 0.02, 40, 30, 5, 500, getBox()));
	}

	@Override
	public Vector3f getFogColor(float intensity) {
		if(fadeInFrom != null) {
			var int1 = getIntensity(MinecraftClient.getInstance().player.getPos(), fadeInFrom.getBox(), fadeInFrom.strongAtBottom);
			var c1 = fadeInFrom.getFogColor(int1);
			float r = MathHelper.lerp(1f - intensity, fog.x, c1.x);
			float g = MathHelper.lerp(1f - intensity, fog.y, c1.y);
			float b = MathHelper.lerp(1f - intensity, fog.z, c1.z);
			return new Vector3f(r, g, b);
		}
		return fog;
	}

	@Override
	public Vector4f getVignetteColor(float intensity) {
		if(fadeInFrom != null) {
			var int1 = getIntensity(MinecraftClient.getInstance().player.getPos(), fadeInFrom.getBox(), fadeInFrom.strongAtBottom);
			var c1 = fadeInFrom.getVignetteColor(int1);
			float r = MathHelper.lerp(1f - intensity, vignette.x, c1.x);
			float g = MathHelper.lerp(1f - intensity, vignette.y, c1.y);
			float b = MathHelper.lerp(1f - intensity, vignette.z, c1.z);
			return new Vector4f(r, g, b, Math.min(1, int1 + intensity));
		}

		return new Vector4f(vignette.x, vignette.y, vignette.z, intensity);
	}

	@Override
	public void onDestroy() {
		fogRender.setDead();
	}


	@Override
	public void insideZoneUpdate(long time, boolean isActiveZone) {
		var cl = MinecraftClient.getInstance();
		float intensity = getIntensity(cl.player.getPos(), getBox(), strongAtBottom);
		if(isActiveZone)
			spawnSporeParticles(cl, intensity);
	}

	private void spawnSporeParticles(MinecraftClient client, float intensity) {
		Vec3d pos = client.player.getPos();


		// scale spawn rate with intensity
		int count = 1 + (int) (intensity * 2); // tweak


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
		double vx = (client.world.random.nextDouble() - 0.5) * 0.01;
		double vy = (client.world.random.nextDouble() - 0.5) * 0.01;
		double vz = (client.world.random.nextDouble() - 0.5) * 0.01;

		client.world.addParticle(
				new SporeParticleEffect(new Vector4f(getFogColor(intensity), 1f), 0.1f), // temporary, replace later
				x, y, z,
				vx, vy, vz
		);
	}
}
