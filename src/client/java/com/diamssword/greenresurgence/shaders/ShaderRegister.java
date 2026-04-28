package com.diamssword.greenresurgence.shaders;

import com.diamssword.greenresurgence.GreenResurgence;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;

public class ShaderRegister {
	public static final ParticleEmitterInfo SMOKE = new ParticleEmitterInfo(GreenResurgence.asRessource("smoke"));


	public static void init() {
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
