package com.diamssword.greenresurgence.systems.character;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Random;

public class ContaminationEffects {
	private int effectCooldown = 200;
	private final Random rand = new Random();
	private final HealthManager manager;

	public ContaminationEffects(HealthManager parent) {
		this.manager = parent;
	}

	public void tick() {
		double perc = manager.getContaminationAmount() / manager.getMaxContaminationAmount();
		if(perc > 0.2) {
			if(effectCooldown <= 0) {
				triggerEffect(manager.player, (float) perc);
				effectCooldown = 1000 + rand.nextInt((int) (Math.max(1, (1f - perc) * 3000f)));
			} else
				effectCooldown--;
		}

	}

	public void triggerEffect(PlayerEntity player, float percent) {
		var r1 = rand.nextFloat();
		if(percent > 0.2 && percent < 0.4) {
			if(r1 < 0.8)
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 10 + rand.nextInt(80), 0));
			else
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200 + rand.nextInt(300), 0));
		} else if(percent >= 0.4 && percent < 0.7) {
			if(r1 < 0.8)
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 10 + rand.nextInt(80), 0));
			else
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 50 + rand.nextInt(200), rand.nextInt(3)));
			player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200 + rand.nextInt(500), rand.nextInt(2)));
			if(r1 <= 0.25)
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 10 + rand.nextInt(100), rand.nextInt(5)));
		} else if(percent >= 0.8) {
			player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100 + rand.nextInt(300), 1));
			if(r1 > 0.5)
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 50 + rand.nextInt(200), rand.nextInt(3)));
			else
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 10 + rand.nextInt(100), rand.nextInt(5)));
			player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200 + rand.nextInt(500), rand.nextInt(2)));

		}
		if(percent > 0.95) {
			if(r1 < 0.2)
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 50 + rand.nextInt(200), rand.nextInt(3)));
			else
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, (int) ((percent * 600f) + rand.nextInt(1000)), (int) (percent * rand.nextInt(5))));
		}
	}
}
