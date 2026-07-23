package com.diamssword.greenresurgence.effects;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.MParticles;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;

public class BleedingStatutEffect extends StatusEffect {
	public static final RegistryKey<DamageType> BLEEDING_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, GreenResurgence.asRessource("bleeding"));

	protected BleedingStatutEffect(StatusEffectCategory category, int color) {
		super(category, color);
	}

	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier) {
		var rand = entity.getRandom();
		if(rand.nextFloat() > 0.7) {
			entity.damage(entity.getDamageSources().create(BLEEDING_DAMAGE), rand.nextFloat() + (amplifier * 0.1f));
			if(entity.getWorld() instanceof ServerWorld sw) {
				sw.spawnParticles(MParticles.BLOOD, entity.getX(), entity.getBodyY(0.5f), entity.getZ(), 10 + rand.nextInt(10), 0, 0.0, 0, 0.01);
			}
		}

	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return duration % 20 == 0;
	}
}
