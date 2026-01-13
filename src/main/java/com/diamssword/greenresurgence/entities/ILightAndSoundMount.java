package com.diamssword.greenresurgence.entities;

import com.diamssword.greenresurgence.systems.equipement.utils.IFlashLightProvider;
import net.minecraft.sound.SoundEvent;

public interface ILightAndSoundMount extends IFlashLightProvider {
	public void setHasLight(boolean light);

	public SoundEvent getKlaxonSound();
}
