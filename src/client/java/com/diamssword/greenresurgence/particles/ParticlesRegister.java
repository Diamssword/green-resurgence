package com.diamssword.greenresurgence.particles;

import com.diamssword.greenresurgence.MParticles;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

//import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;

public class ParticlesRegister {
	//public static final ParticleEmitterInfo SMOKE = new ParticleEmitterInfo(GreenResurgence.asRessource("smoke"));


	public static void init() {
		ParticleFactoryRegistry.getInstance().register(MParticles.AIR_SPORE, SporeParticle.SporeBlossomAirFactory::new);
		ParticleFactoryRegistry.getInstance().register(MParticles.BLOOD, BloodParticle.Factory::new);
	/*	ClientTickEvents.END_WORLD_TICK.register(w -> {
			if(w.getTime() % 500 == 0) {
				AAALevel.addParticle(w, HERALD.clone().position(-20, 70, 123));
				AAALevel.addParticle(w, METEOR.clone().position(-10, 70, 123));
				//	AAALevel.addParticle(w, SMOKE.clone().position(-50, 70, 123));

			}
		});

	 */
	}

}
