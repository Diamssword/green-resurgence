package com.diamssword.greenresurgence.particles;

import com.diamssword.greenresurgence.MParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.dynamic.Codecs;
import org.joml.Quaternionf;
import org.joml.Vector4f;

public class SporeParticleEffect extends ColoredParticleEffect {
	public static final Codec<SporeParticleEffect> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
							Codecs.QUATERNIONF.fieldOf("color").forGetter(effect -> new Quaternionf(effect.color.x, effect.color.y, effect.color.z, effect.color.w)), Codec.FLOAT.fieldOf("scale").forGetter(effect -> effect.scale)
					)
					.apply(instance, SporeParticleEffect::new)
	);
	public static final ParticleEffect.Factory<SporeParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<>() {
		public SporeParticleEffect read(ParticleType<SporeParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
			Vector4f vector3f = readColor(stringReader);
			stringReader.expect(' ');
			float f = stringReader.readFloat();
			return new SporeParticleEffect(vector3f, f);
		}

		public SporeParticleEffect read(ParticleType<SporeParticleEffect> particleType, PacketByteBuf packetByteBuf) {
			return new SporeParticleEffect(readColor(packetByteBuf), packetByteBuf.readFloat());
		}
	};

	public SporeParticleEffect(Quaternionf color, float scale) {
		super(color, scale);
	}

	public SporeParticleEffect(Vector4f color, float scale) {
		super(color, scale);
	}

	@Override
	public ParticleType<?> getType() {
		return MParticles.AIR_SPORE;
	}
}