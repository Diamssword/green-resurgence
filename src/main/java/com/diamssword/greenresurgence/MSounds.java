package com.diamssword.greenresurgence;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

public class MSounds {

	public static SoundEvent BIKE_DRIVING = register("entity.bike.driving");
	public static SoundEvent BIKE_BELL = register("entity.bike.bell");

	private static SoundEvent register(String name) {
		var ev = SoundEvent.of(GreenResurgence.asRessource(name));
		Registry.register(Registries.SOUND_EVENT, GreenResurgence.asRessource(name), ev);
		return ev;
	}

	public static void init() {

	}

}
