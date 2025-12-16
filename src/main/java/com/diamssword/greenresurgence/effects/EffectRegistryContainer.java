package com.diamssword.greenresurgence.effects;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface EffectRegistryContainer extends AutoRegistryContainer<StatusEffect> {
	@Override
	default Registry<StatusEffect> getRegistry() {
		return Registries.STATUS_EFFECT;
	}

	@Override
	default Class<StatusEffect> getTargetFieldType() {
		return StatusEffect.class;
	}
}
