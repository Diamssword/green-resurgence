package com.diamssword.greenresurgence.mixin.client;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayerEntity.class)
public interface ClientPlayerAccessor {

	@Mutable
	@Accessor("riding")
		// Name must match the private field in LivingEntity
	void setRiding(boolean riding);

}
