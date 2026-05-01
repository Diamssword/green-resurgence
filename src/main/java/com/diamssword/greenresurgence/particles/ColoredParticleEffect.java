package com.diamssword.greenresurgence.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.joml.Vector4f;

import java.util.Locale;

public abstract class ColoredParticleEffect implements ParticleEffect {


	public static Vector4f readColor(StringReader reader) throws CommandSyntaxException {
		reader.expect(' ');
		float f = reader.readFloat();
		reader.expect(' ');
		float g = reader.readFloat();
		reader.expect(' ');
		float h = reader.readFloat();
		reader.expect(' ');
		float i = reader.readFloat();
		return new Vector4f(f, g, h, i);
	}

	public static Vector4f readColor(PacketByteBuf buf) {
		return new Vector4f(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
	}


	protected final Vector4f color;
	protected final float scale;

	public ColoredParticleEffect(Quaternionf color, float scale) {
		this(new Vector4f(color.x, color.y, color.z, color.w), scale);
	}

	public ColoredParticleEffect(Vector4f color, float scale) {

		this.color = color;
		this.scale = MathHelper.clamp(scale, 0.01F, 9999f);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeFloat(this.color.x());
		buf.writeFloat(this.color.y());
		buf.writeFloat(this.color.z());
		buf.writeFloat(this.scale);
	}

	@Override
	public String asString() {
		return String.format(
				Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f", Registries.PARTICLE_TYPE.getId(this.getType()), this.color.x(), this.color.y(), this.color.z(), this.color.w(), this.scale
		);
	}

	public Vector4f getColor() {
		return this.color;
	}

	public float getScale() {
		return this.scale;
	}

}