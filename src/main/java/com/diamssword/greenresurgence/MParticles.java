package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.particles.SporeParticleEffect;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.Function;

public class MParticles {

	public static final ParticleType<SporeParticleEffect> AIR_SPORE = register("air_spore", false, SporeParticleEffect.PARAMETERS_FACTORY, f -> SporeParticleEffect.CODEC);

	private static DefaultParticleType register(String name) {
		return Registry.register(Registries.PARTICLE_TYPE, GreenResurgence.asRessource(name), FabricParticleTypes.simple());
	}

	private static <T extends net.minecraft.particle.ParticleEffect> ParticleType<T> register(String name, ParticleEffect.Factory<T> factory) {
		return Registry.register(Registries.PARTICLE_TYPE, GreenResurgence.asRessource(name), FabricParticleTypes.complex(factory));
	}

	private static <T extends ParticleEffect> ParticleType<T> register(String name, boolean alwaysShow, ParticleEffect.Factory<T> factory, Function<ParticleType<T>, Codec<T>> codecGetter) {
		return Registry.register(Registries.PARTICLE_TYPE, GreenResurgence.asRessource(name), new ParticleType<T>(alwaysShow, factory) {
			@Override
			public Codec<T> getCodec() {
				return codecGetter.apply(this);
			}
		});
	}

	public static void init() {

	}

}
